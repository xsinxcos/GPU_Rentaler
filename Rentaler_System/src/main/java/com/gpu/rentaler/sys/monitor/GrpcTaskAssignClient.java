package com.gpu.rentaler.sys.monitor;

import com.google.protobuf.ByteString;
import com.gpu.rentaler.grpc.TaskAssignServiceGrpc;
import com.gpu.rentaler.grpc.TaskAssignServiceProto;
import com.gpu.rentaler.grpc.TaskAssignServiceProto.StopDockerContainerRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.BufferPoolMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class GrpcTaskAssignClient {

    private static final Logger log = LogManager.getLogger(GrpcTaskAssignClient.class);
    private final TaskAssignServiceGrpc.TaskAssignServiceBlockingStub blockingStub;
    private final TaskAssignServiceGrpc.TaskAssignServiceStub asyncStub;

    public GrpcTaskAssignClient(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext() // 开发环境使用明文
            .build();
        blockingStub = TaskAssignServiceGrpc.newBlockingStub(channel);
        asyncStub = TaskAssignServiceGrpc.newStub(channel);
    }

    /**
     * 自适应背压控制版本（动态调整读取速度）- 使用 Log 打印
     * @param inputStream 输入流（注意：此方法不会关闭该流，调用方负责关闭）
     * @param fileName 文件名
     * @param totalSize 文件总大小（字节）
     * @param deviceIndexes 设备索引列表
     */
    public DContainerInfoResp upDockerImageStream(InputStream inputStream, String fileName, long totalSize, List<Integer> deviceIndexes)
        throws IOException, InterruptedException {

        int deviceIndex = deviceIndexes.getFirst();
        CountDownLatch latch = new CountDownLatch(1);
        DContainerInfoResp[] resultHolder = new DContainerInfoResp[1];
        Throwable[] errorHolder = new Throwable[1];

        BufferPoolMXBean directPool = DirectMemoryMonitor.getDirectBufferPool();
        long initialDirectMemory = directPool != null ? directPool.getMemoryUsed() : 0;

        // 🔥 自适应参数
        final long SAFE_THRESHOLD = 5L * 1024 * 1024;    // 5MB 安全阈值
        final long WARNING_THRESHOLD = 10L * 1024 * 1024; // 10MB 警告阈值
        final long DANGER_THRESHOLD = 20L * 1024 * 1024;  // 20MB 危险阈值

        log.info("[内存监控] 文件: {} | 大小: {} MB | 初始直接内存: {} MB",
            fileName,
            totalSize / 1024.0 / 1024.0,
            initialDirectMemory / 1024.0 / 1024.0
        );

        StreamObserver<TaskAssignServiceProto.UpDockerImageChunkResp> responseObserver =
            new StreamObserver<>() {
                @Override
                public void onNext(TaskAssignServiceProto.UpDockerImageChunkResp value) {
                    resultHolder[0] = new DContainerInfoResp(value.getContainerName(), value.getContainerId());
                }

                @Override
                public void onError(Throwable t) {
                    errorHolder[0] = t;
                    latch.countDown();
                }

                @Override
                public void onCompleted() {
                    latch.countDown();
                }
            };

        StreamObserver<TaskAssignServiceProto.UpDockerImageChunkRequest> requestObserver =
            asyncStub.upDockerImageStream(responseObserver);

        final int chunkSize = 256 * 1024; // 256KB
        final Semaphore flowControl = new Semaphore(2);

        Thread uploadThread = new Thread(() -> {
            long totalBytes = 0;
            long lastLogTime = System.currentTimeMillis();
            long startTime = lastLogTime;
            long lastBytes = 0;
            long peakDirectMemory = initialDirectMemory;
            long blockCount = 0;
            int adaptiveSleep = 0; // 🔥 自适应休眠时间

            byte[] buffer = new byte[chunkSize];

            try {
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    // 🔥 自适应背压控制
                    if (directPool != null) {
                        long currentDirect = directPool.getMemoryUsed();
                        long directDelta = currentDirect - initialDirectMemory;

                        if (directDelta > DANGER_THRESHOLD) {
                            // 危险：长时间休眠
                            adaptiveSleep = 200;
                            blockCount++;
                            log.error(
                                "[背压-危险] 直接内存 {} MB (增量 {} MB) > {} MB，休眠 {} ms",
                                currentDirect / 1024.0 / 1024.0,
                                directDelta / 1024.0 / 1024.0,
                                DANGER_THRESHOLD / 1024.0 / 1024.0,
                                adaptiveSleep
                            );
                            Thread.sleep(adaptiveSleep);
                            System.gc(); // 强制GC

                        } else if (directDelta > WARNING_THRESHOLD) {
                            // 警告：中等休眠
                            adaptiveSleep = 100;
                            blockCount++;
                            if (blockCount % 5 == 1) {
                                log.warn(
                                    "[背压-警告] 直接内存 {} MB (增量 {} MB) > {} MB，休眠 {} ms",
                                    currentDirect / 1024.0 / 1024.0,
                                    directDelta / 1024.0 / 1024.0,
                                    WARNING_THRESHOLD / 1024.0 / 1024.0,
                                    adaptiveSleep
                                );
                            }
                            Thread.sleep(adaptiveSleep);

                        } else if (directDelta > SAFE_THRESHOLD) {
                            // 注意：短暂休眠
                            adaptiveSleep = 20;
                            Thread.sleep(adaptiveSleep);

                        } else {
                            // 安全：全速传输
                            adaptiveSleep = 0;
                        }
                    }

                    flowControl.acquire();

                    ByteString chunk = ByteString.copyFrom(buffer, 0, bytesRead);

                    TaskAssignServiceProto.UpDockerImageChunkRequest chunkRequest =
                        TaskAssignServiceProto.UpDockerImageChunkRequest.newBuilder()
                            .setChunkData(chunk)
                            .setDeviceIndex(deviceIndex)
                            .build();

                    requestObserver.onNext(chunkRequest);

                    totalBytes += bytesRead;
                    flowControl.release();

                    // 📊 日志输出
                    long now = System.currentTimeMillis();
                    if (now - lastLogTime >= 3000) {
                        double percent = (totalBytes * 100.0 / totalSize);
                        double elapsedSec = (now - startTime) / 1000.0;
                        double speed = (totalBytes / 1024.0 / 1024.0) / Math.max(1, elapsedSec);
                        double recentSpeed = ((totalBytes - lastBytes) / 1024.0 / 1024.0) / ((now - lastLogTime) / 1000.0);

                        Runtime runtime = Runtime.getRuntime();
                        long heapUsed = (runtime.totalMemory() - runtime.freeMemory());
                        long currentDirectMemory = directPool != null ? directPool.getMemoryUsed() : 0;
                        long directDelta = currentDirectMemory - initialDirectMemory;

                        peakDirectMemory = Math.max(peakDirectMemory, currentDirectMemory);

                        String pressureLevel = directDelta > DANGER_THRESHOLD ? "危险" :
                            directDelta > WARNING_THRESHOLD ? "警告" :
                                directDelta > SAFE_THRESHOLD ? "注意" : "安全";

                        log.info(
                            "[上传进度] 文件: {} | 进度: {}% | 已传: {}/{} MB | 速度: {} MB/s | 休眠: {} ms",
                            fileName,
                            percent,
                            totalBytes / 1024.0 / 1024.0,
                            totalSize / 1024.0 / 1024.0,
                            recentSpeed,
                            adaptiveSleep
                        );

                        log.debug(
                            "[内存监控] 堆: {} MB | 直接: {} MB (峰值: {} MB, 增量: {} MB) | 状态: {} | 阻塞: {} 次",
                            heapUsed / 1024.0 / 1024.0,
                            currentDirectMemory / 1024.0 / 1024.0,
                            peakDirectMemory / 1024.0 / 1024.0,
                            directDelta / 1024.0 / 1024.0,
                            pressureLevel,
                            blockCount
                        );

                        lastLogTime = now;
                        lastBytes = totalBytes;
                    }
                }

                requestObserver.onCompleted();

                if (directPool != null) {
                    long finalDirectMemory = directPool.getMemoryUsed();
                    log.info(
                        "[内存统计] 文件: {} | 初始: {} MB | 峰值: {} MB | 最终: {} MB | 增量: {} MB | 总阻塞: {} 次",
                        fileName,
                        initialDirectMemory / 1024.0 / 1024.0,
                        peakDirectMemory / 1024.0 / 1024.0,
                        finalDirectMemory / 1024.0 / 1024.0,
                        (finalDirectMemory - initialDirectMemory) / 1024.0 / 1024.0,
                        blockCount
                    );
                }

                log.info("[上传完成] 文件: {} | 总大小: {} MB | 耗时: {} 秒",
                    fileName,
                    totalSize / 1024.0 / 1024.0,
                    (System.currentTimeMillis() - startTime) / 1000.0
                );

            } catch (Exception e) {
                log.error("[上传失败] 文件: {} | 错误: {}", fileName, e.getMessage(), e);
                requestObserver.onError(e);
            }
        }, "docker-image-upload-thread");

        uploadThread.start();

        boolean finished = latch.await(30, TimeUnit.MINUTES);
        if (!finished) {
            String errorMsg = "上传超时 (30分钟)";
            log.error("[上传超时] 文件: {} | {}", fileName, errorMsg);
            requestObserver.onError(new RuntimeException(errorMsg));
            uploadThread.interrupt();
            throw new RuntimeException(errorMsg);
        }

        if (errorHolder[0] != null) {
            log.error("[上传异常] 文件: {} | 异常信息: {}", fileName, errorHolder[0].getMessage());
            throw new RuntimeException("上传镜像失败", errorHolder[0]);
        }

        return resultHolder[0];
    }

    /**
     * 停止 Docker 容器
     */
    public void stopDockerContainer(String containerId) {
        StopDockerContainerRequest request = StopDockerContainerRequest.newBuilder()
            .setContainerId(containerId)
            .build();

        blockingStub.stopDockerContainer(request); // 返回 Empty，相当于 void
        System.out.println("Container stopped: " + containerId);
    }

    /**
     * 删除 Docker 容器
     */
    public void deleteDockerContainer(String containerId) {
        TaskAssignServiceProto.DeleteContainerRequest request = TaskAssignServiceProto.DeleteContainerRequest.newBuilder().setContainerId(containerId)
            .build();
        blockingStub.deleteContainer(request);
    }


    public String getLog(int num, String containerId) {
        TaskAssignServiceProto.GetLogRequest request = TaskAssignServiceProto.GetLogRequest.newBuilder()
            .setContainerId(containerId)
            .setNum(num)
            .build();
        TaskAssignServiceProto.GetLogResp log = blockingStub.getLog(request);
        return log.getLogContent();
    }

    public Resource exportContainerData(String containerId, String path) {
        TaskAssignServiceProto.ExportContainerDataRequest req = TaskAssignServiceProto.ExportContainerDataRequest.newBuilder()
            .setContainerId(containerId)
            .setExportDir(path).build();
        TaskAssignServiceProto.ExportContainerDataResp resp = blockingStub.exportContainerData(req);
        return new NamedByteArrayResource(resp.getZipFile().toByteArray(), resp.getFileName());
    }

    public class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;

        public NamedByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }

}
