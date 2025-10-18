# GPU_Rentaler_0

## 项目简介

GPU_Rentaler_0 是一个用于 GPU 资源租赁与监控的系统，支持设备管理、监控 API 服务，适用于需要动态分配 GPU 资源的场景。

## 主要模块

- **Rentaler_System**：核心后端服务，负责资源管理、租赁逻辑。
- **Rentaler_Device_Monitor**：设备监控服务，采集 GPU 运行数据。
- **Rentaler_Monitor_Api**：监控 API 服务，提供数据接口。

## 依赖环境

- JDK 21
- Maven 3.6 及以上
- Docker & Docker Compose

## 主要文件结构

- `Rentaler_System/` 后端主服务
- `Rentaler_Device_Monitor/` 设备监控服务
- `Rentaler_Monitor_Api/` 监控 API 服务
- `docker-compose.yml` 容器编排文件

## License

详见 LICENSE 文件。

## 非 Docker 部署指南

### 1. 环境准备

- JDK 21
- Maven 3.6 及以上
- MySQL8.0

### 2. 配置文件编写

1. 进入 `Rentaler_System/src/main/resources/`，编辑 `application-dev.yml`，配置数据库等信息：

```yaml
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/gpu_rentaler_0?characterEncoding=utf8
        username: root
        password: 你的数据库密码
        driver-class-name: com.mysql.cj.jdbc.Driver


grpc:
    server:
        port: 50052            # 默认端口，如果想每个服务不同端口，可以在启动类配置
        enableReflection: true
        maxInboundMessageSize: 1GB
```

2. 进入 `Rentaler_Device_Monitor/src/main/resources/`，编辑 `application.yml`，配置数据库等信息：

```yaml
grpc:
    server:
        port: 50055             # 
        enableReflection: true
        maxInboundMessageSize: 1GB
    client:
        backend:
            address: "localhost:50052" # 纯内网环境情况下，与第一步的 Rentaler_System grpc保持一致即可
            negotiationType: plaintext
            maxInboundMessageSize: 50MB

ip:
    url: # 主程序与监控设备同一部机器情况置空即可，否则填写本机公网IP的服务地址
```

### 3. 依赖安装与构建

#### 后端模块（以 Rentaler_System 为例）

```bash
mvn clean compile package

cd Rentaler_System/target
java -jar Rentaler_System-0.0.1-SNAPSHOT.jar

cd ../../Rentaler_Device_Monitor/target
java -jar Rentaler_Device_Monitor-0.0.1-SNAPSHOT.jar
```

## 快速启动

### 本地启动

- 后端模块均为 Maven 项目，进入各模块目录后运行：
  ```bash
  cd Rentaler_System/target
  java -jar Rentaler_System-0.0.1-SNAPSHOT.jar
  
  cd ../../Rentaler_Device_Monitor/target
  java -jar Rentaler_Device_Monitor-0.0.1-SNAPSHOT.jar
  ```

### 5. 访问与验证

- 后端接口一般为 http://localhost:9090/（可根据配置文件端口调整）

### 6. 常见问题排查

- 端口冲突：检查 application.yml 中 server.port 配置
- 数据库连接失败：确认数据库已启动、账号密码正确、端口未被防火墙阻断
- 依赖缺失：确保 Maven依赖已正确安装

如需更详细的配置样例或遇到具体报错，联系开发者。
