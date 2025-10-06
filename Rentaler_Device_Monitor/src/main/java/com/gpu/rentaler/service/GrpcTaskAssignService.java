package com.gpu.rentaler.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.gpu.rentaler.entity.DContainerInfo;
import com.gpu.rentaler.grpc.TaskAssignServiceGrpc;
import com.gpu.rentaler.grpc.TaskAssignServiceProto;
import com.gpu.rentaler.utils.DockerExecutor;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@GrpcService
@Service
public class GrpcTaskAssignService extends TaskAssignServiceGrpc.TaskAssignServiceImplBase {

    private static final Logger log = LogManager.getLogger(GrpcTaskAssignService.class);

    @Override
    public void stopDockerContainer(TaskAssignServiceProto.StopDockerContainerRequest request, StreamObserver<Empty> responseObserver) {
        String containerId = request.getContainerId();
        try {
            DockerExecutor.stopContainer(containerId, 60);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (IOException e) {
            log.error("Error stopping container {}: {}", containerId, e.getMessage());
        }
    }

    @Override
    public StreamObserver<TaskAssignServiceProto.UpDockerImageChunkRequest> upDockerImageStream(
        StreamObserver<TaskAssignServiceProto.UpDockerImageChunkResp> responseObserver) {

        try {
            // 创建临时文件保存上传的镜像分片
            File tempImageFile = File.createTempFile("docker-image-", ".tar");
            FileOutputStream fos = new FileOutputStream(tempImageFile);

            // 用于保存 GPU 设备索引（假设所有分片的 deviceIndex 一致）
            final int[] deviceIndex = new int[1];

            return new StreamObserver<>() {
                @Override
                public void onNext(TaskAssignServiceProto.UpDockerImageChunkRequest chunkRequest) {
                    try {
                        // 写入分片数据到临时文件
                        chunkRequest.getChunkData().writeTo(fos);
                        deviceIndex[0] = chunkRequest.getDeviceIndex();
                    } catch (IOException e) {
                        log.error("写入镜像分片失败: {}", e.getMessage());
                        responseObserver.onError(e);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log.error("上传镜像流异常: {}", t.getMessage());
                    try {
                        fos.close();
                        tempImageFile.delete();
                    } catch (IOException ignored) {
                    }
                }

                @Override
                public void onCompleted() {
                    try {
                        fos.close();

                        // 用文件流导入 Docker 镜像
                        try (FileInputStream fis = new FileInputStream(tempImageFile)) {
                            String imageName = DockerExecutor.loadImageFromInputStream(fis);
                            DContainerInfo dContainerInfo = DockerExecutor.runContainerAndGetInfo(imageName, List.of(deviceIndex[0]));

                            TaskAssignServiceProto.UpDockerImageChunkResp resp =
                                TaskAssignServiceProto.UpDockerImageChunkResp.newBuilder()
                                    .setContainerName(dContainerInfo.containerName())
                                    .setContainerId(dContainerInfo.containerId() != null ? dContainerInfo.containerId() : "")
                                    .build();

                            responseObserver.onNext(resp);
                            responseObserver.onCompleted();
                        }
                    } catch (IOException e) {
                        log.error("镜像导入失败: {}", e.getMessage());
                        responseObserver.onError(e);
                    } finally {
                        // 删除临时文件
                        tempImageFile.delete();
                    }
                }
            };
        } catch (IOException e) {
            log.error("创建临时文件失败: {}", e.getMessage());
            responseObserver.onError(e);
            return new StreamObserver<>() {
                @Override public void onNext(TaskAssignServiceProto.UpDockerImageChunkRequest value) {}
                @Override public void onError(Throwable t) {}
                @Override public void onCompleted() {}
            };
        }
    }

    @Override
    public void getLog(TaskAssignServiceProto.GetLogRequest request, StreamObserver<TaskAssignServiceProto.GetLogResp> responseObserver) {
        String containerId = request.getContainerId();
        int num = request.getNum();
        try {
            String logs = DockerExecutor.getLatestLogs(containerId, num);
            TaskAssignServiceProto.GetLogResp resp = TaskAssignServiceProto.GetLogResp.newBuilder()
                .setLogContent(logs)
                .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        } catch (IOException e) {
            log.warn("{} 容器获取日志失败：{}", containerId, e.getMessage());
        }
    }

    @Override
    public void exportContainerData(TaskAssignServiceProto.ExportContainerDataRequest request, StreamObserver<TaskAssignServiceProto.ExportContainerDataResp> responseObserver) {
        String containerId = request.getContainerId();
        String exportDir = request.getExportDir();
        try (InputStream stream = DockerExecutor.exportContainerDirContentAsTarGz(containerId ,exportDir)){
            TaskAssignServiceProto.ExportContainerDataResp resp = TaskAssignServiceProto.ExportContainerDataResp.newBuilder()
                .setZipFile(ByteString.copyFrom(stream.readAllBytes()))
                .setFileName(containerId + ".tar.gz").build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        } catch (IOException e) {
            log.warn("{} 的 {} 数据导出失败：{}" ,containerId ,exportDir ,e.getMessage());
        }
    }

    @Override
    public void deleteContainer(TaskAssignServiceProto.DeleteContainerRequest request, StreamObserver<Empty> responseObserver) {
        String containerId = request.getContainerId();
        try {
            String imageId = DockerExecutor.getImageIdByContainerId(containerId);
            DockerExecutor.deleteContainerById(containerId ,true);
            DockerExecutor.deleteImageByImageId(imageId ,true);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        }catch (IOException e){
            log.warn("{} 容器删除失败：{}" ,containerId ,e.getMessage());
        }
    }
}
