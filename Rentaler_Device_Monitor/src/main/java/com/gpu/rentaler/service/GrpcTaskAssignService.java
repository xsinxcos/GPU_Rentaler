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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public void upDockerImage(TaskAssignServiceProto.UpDockerImageRequest request, StreamObserver<TaskAssignServiceProto.DContainerInfoResp> responseObserver) {
        byte[] imageBytes = request.getImageFile().toByteArray();
        List<Integer> deviceIndexes = request.getDeviceIndexsList();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        try {
            String imageName = DockerExecutor.loadImageFromInputStream(inputStream);
            DContainerInfo dContainerInfo = DockerExecutor.runContainerAndGetInfo(imageName, deviceIndexes);
            String containerName = dContainerInfo.containerName();
            String containerId = dContainerInfo.containerId();
            TaskAssignServiceProto.DContainerInfoResp resp = TaskAssignServiceProto.DContainerInfoResp.newBuilder()
                .setContainerName(containerName)
                .setContainerId(containerId != null ? containerId : "")
                .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        } catch (IOException e) {
            log.warn(" 镜像导入失败：{}", e.getMessage());
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
