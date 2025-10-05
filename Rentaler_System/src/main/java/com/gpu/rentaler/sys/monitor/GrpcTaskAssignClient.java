package com.gpu.rentaler.sys.monitor;

import com.google.protobuf.ByteString;
import com.gpu.rentaler.grpc.TaskAssignServiceGrpc;
import com.gpu.rentaler.grpc.TaskAssignServiceProto;
import com.gpu.rentaler.grpc.TaskAssignServiceProto.DContainerInfoResp;
import com.gpu.rentaler.grpc.TaskAssignServiceProto.StopDockerContainerRequest;
import com.gpu.rentaler.grpc.TaskAssignServiceProto.UpDockerImageRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class GrpcTaskAssignClient {

    private final TaskAssignServiceGrpc.TaskAssignServiceBlockingStub blockingStub;

    public GrpcTaskAssignClient(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext() // 开发环境使用明文
            .build();
        blockingStub = TaskAssignServiceGrpc.newBlockingStub(channel);
    }

    /**
     * 上传 Docker 镜像并启动容器
     */
    public DContainerInfoResp upDockerImage(Path imageFilePath, List<Integer> deviceIndexes) throws IOException {
        byte[] imageBytes = Files.readAllBytes(imageFilePath);

        UpDockerImageRequest request = UpDockerImageRequest.newBuilder()
            .setImageFile(ByteString.copyFrom(imageBytes))
            .addAllDeviceIndexs(deviceIndexes)
            .build();

        return blockingStub.upDockerImage(request);
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


    public String getLog(int num ,String containerId){
        TaskAssignServiceProto.GetLogRequest request = TaskAssignServiceProto.GetLogRequest.newBuilder()
            .setContainerId(containerId)
            .setNum(num)
            .build();
        TaskAssignServiceProto.GetLogResp log = blockingStub.getLog(request);
        return log.getLogContent();
    }

    public Resource exportContainerData(String containerId ,String path){
        TaskAssignServiceProto.ExportContainerDataRequest req = TaskAssignServiceProto.ExportContainerDataRequest.newBuilder()
            .setContainerId(containerId)
            .setExportDir(path).build();
        TaskAssignServiceProto.ExportContainerDataResp resp = blockingStub.exportContainerData(req);
        return new NamedByteArrayResource(resp.getZipFile().toByteArray() ,resp.getFileName());
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



    public static void main(String[] args) throws IOException {
        GrpcTaskAssignClient client = new GrpcTaskAssignClient("localhost", 50055);

        // 示例：上传镜像启动容器
        Path imagePath = Path.of("files/6g676_2048.tar");
        DContainerInfoResp resp = client.upDockerImage(imagePath, List.of(0));
        System.out.println("Container started: " + resp.getContainerName() + ", ID: " + resp.getContainerId());

        // 示例：停止容器
        client.stopDockerContainer(resp.getContainerId());
    }
}
