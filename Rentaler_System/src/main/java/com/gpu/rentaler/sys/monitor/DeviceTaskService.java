package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.sys.model.GPURealDevices;
import com.gpu.rentaler.sys.model.Server;
import com.gpu.rentaler.sys.service.GPURealDevicesService;
import com.gpu.rentaler.sys.service.ServerService;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeviceTaskService {

    private static final Logger log = LogManager.getLogger(DeviceTaskService.class);
    @Resource
    private ServerService serverService;

    @Resource
    private GPURealDevicesService gpuRealDevicesService;


    public DContainerInfoResp importAndUpDockerImage(InputStream stream ,String filename ,long contentLength, Long serverId, List<String> deviceIds) throws IOException {
        try {
            List<GPURealDevices> devices = gpuRealDevicesService.getByDeviceIds(deviceIds);
            List<Integer> deviceIndex = devices.stream().map(GPURealDevices::getDeviceIndex).collect(Collectors.toList());

            GrpcTaskAssignClient client = getGrpcTaskAssignClient(serverId);
            DContainerInfoResp resp = client.upDockerImageStream(stream,filename ,contentLength , deviceIndex);
            return new DContainerInfoResp(resp.containerName(), resp.containerId());
        } catch (IOException | InterruptedException e) {
            log.warn("exportAndUpDockerImage error : {}", e.getMessage());
            return new DContainerInfoResp("error", "error");
        }
    }

    /**
     * 将 InputStream 写入临时文件并返回 Path
     *
     * @param inputStream 输入流
     * @param suffix      文件后缀，例如 ".tar" ".txt"
     * @return 写入后的 Path
     * @throws IOException
     */
    private static Path writeInputStreamToTempFile(InputStream inputStream, String suffix) throws IOException {
        // 创建临时文件
        Path tempFile = Files.createTempFile("temp-stream-", suffix);

        // 将 InputStream 写入文件
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

        return tempFile;
    }

    public String getLog(Long serverId, String containerId, int num) {
        GrpcTaskAssignClient client = getGrpcTaskAssignClient(serverId);
        return client.getLog(num, containerId);
    }

    private GrpcTaskAssignClient getGrpcTaskAssignClient(Long serverId) {
        Server server = serverService.getById(serverId);
        int port = 50055;
        return new GrpcTaskAssignClient(server.getIpAddress(), port);
    }

    public void stopContainer(Long serverId, String containerId) {
        GrpcTaskAssignClient client = getGrpcTaskAssignClient(serverId);
        client.stopDockerContainer(containerId);
    }


    public void deleteContainer(Long serverId, String containerId) {
        GrpcTaskAssignClient client = getGrpcTaskAssignClient(serverId);
        client.deleteDockerContainer(containerId);
    }

    public org.springframework.core.io.Resource exportContainerData(Long serverId, String containerId, String path) {
        GrpcTaskAssignClient client = getGrpcTaskAssignClient(serverId);
        return client.exportContainerData(containerId, path);
    }

    class NamedByteArrayResource extends ByteArrayResource {
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
