package com.gpu.rentaler.sys.monitor;

import com.gpu.rentaler.entity.DContainerInfoResp;
import com.gpu.rentaler.grpc.TaskAssignServiceProto;
import com.gpu.rentaler.sys.model.GPUDevice;
import com.gpu.rentaler.sys.model.Server;
import com.gpu.rentaler.sys.service.GPUDeviceService;
import com.gpu.rentaler.sys.service.ServerService;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    private GPUDeviceService gpuDeviceService;


    public DContainerInfoResp importAndUpDockerImage(InputStream image, Long serverId, List<String> deviceIds) throws IOException {
        Path tempFile = null;
        try {
            List<GPUDevice> devices = gpuDeviceService.getByDeviceIds(deviceIds);
            List<Integer> deviceIndex = devices.stream().map(GPUDevice::getDeviceIndex).collect(Collectors.toList());
            tempFile = writeInputStreamToTempFile(image, ".tar");

            GrpcTaskAssignClient client = getGrpcTaskAssignClient(serverId);

            TaskAssignServiceProto.DContainerInfoResp resp = client.upDockerImage(tempFile, deviceIndex);
            return new DContainerInfoResp(resp.getContainerName(), resp.getContainerId());
        } catch (IOException e) {
            log.warn("exportAndUpDockerImage error : {}", e.getMessage());
        } finally {
            // 使用完立即删除
            if (tempFile != null) {
                Files.deleteIfExists(tempFile);
            }
        }
        return null;
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

    public String getLog(Long serverId, String containerId ,int num){
        GrpcTaskAssignClient client = getGrpcTaskAssignClient(serverId);
        return client.getLog(num ,containerId);
    }

    private GrpcTaskAssignClient getGrpcTaskAssignClient(Long serverId){
        Server server = serverService.getById(serverId);
        int port = 50055;
        return new GrpcTaskAssignClient(server.getIpAddress(), port);
    }

    public void stopContainer(Long serverId, String containerId) {
        GrpcTaskAssignClient client = getGrpcTaskAssignClient(serverId);
        //todo
    }

    public org.springframework.core.io.Resource exportContainerData(Long serverId, String containerId, String path) {
        GrpcTaskAssignClient client = getGrpcTaskAssignClient(serverId);
        //todo
        String testContent = "这是测试文本资源内容";
        return new ByteArrayResource(testContent.getBytes(StandardCharsets.UTF_8));
    }
}
