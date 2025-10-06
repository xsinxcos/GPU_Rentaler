package com.gpu.rentaler.sys.monitor;

import com.google.protobuf.ByteString;
import com.gpu.rentaler.entity.DContainerInfoResp;
import com.gpu.rentaler.grpc.TaskAssignServiceGrpc;
import com.gpu.rentaler.grpc.TaskAssignServiceProto;
import com.gpu.rentaler.grpc.TaskAssignServiceProto.StopDockerContainerRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class GrpcTaskAssignClient {

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
     * 上传 Docker 镜像分片并启动容器
     */
    public DContainerInfoResp upDockerImageStream(Path imageFilePath, List<Integer> deviceIndexes) throws IOException, InterruptedException {
        // 用 CountDownLatch 阻塞等待服务端响应
        CountDownLatch latch = new CountDownLatch(1);
        DContainerInfoResp[] resultHolder = new DContainerInfoResp[1]; // 用数组保存结果
        Throwable[] errorHolder = new Throwable[1]; // 保存异常
        int deviceIndex = deviceIndexes.getFirst();
        // 实现响应观察者
        StreamObserver<TaskAssignServiceProto.UpDockerImageChunkResp> responseObserver = new StreamObserver<>() {
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

        // 创建客户端流
        StreamObserver<TaskAssignServiceProto.UpDockerImageChunkRequest> requestObserver = asyncStub.upDockerImageStream(responseObserver);

        // 分片上传文件
        int chunkSize = 4 * 1024 * 1024; // 4MB
        byte[] buffer = new byte[chunkSize];

        try (InputStream fis = Files.newInputStream(imageFilePath)) {
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                TaskAssignServiceProto.UpDockerImageChunkRequest chunkRequest = TaskAssignServiceProto.UpDockerImageChunkRequest.newBuilder()
                    .setChunkData(ByteString.copyFrom(buffer, 0, bytesRead))
                    .setDeviceIndex(deviceIndex)
                    .build();
                requestObserver.onNext(chunkRequest);
            }
        }

        // 结束上传
        requestObserver.onCompleted();

        // 阻塞等待服务端返回
        latch.await();

        // 如果有异常，抛出
        if (errorHolder[0] != null) {
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


    public static void main(String[] args) throws IOException, InterruptedException {
        GrpcTaskAssignClient client = new GrpcTaskAssignClient("localhost", 50055);

        // 示例：上传镜像启动容器
        Path imagePath = Path.of("files/ub669_my_image.tar");
        DContainerInfoResp resp = client.upDockerImageStream(imagePath, List.of(0));
        System.out.println("Container started: " + resp.containerId() + ", ID: " + resp.containerName());

        // 示例：停止容器
        //client.stopDockerContainer(resp.getContainerId());
    }
}
