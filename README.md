# GPU_Rentaler_0

## 项目简介
GPU_Rentaler_0 是一个用于 GPU 资源租赁与监控的系统，支持设备管理、监控 API及容器化部署。

## 主要模块
- **Rentaler_System**：核心后端服务，负责资源管理、租赁逻辑。
- **Rentaler_Device_Monitor**：设备监控服务，采集 GPU 运行数据。
- **Rentaler_Monitor_Api**：监控 API 服务，提供数据接口。

## 快速启动
### 1. 本地启动
- 后端模块均为 Maven 项目，进入各模块目录后运行：
  ```bash
  ./mvnw spring-boot:run
  ```

### 2. Docker 启动
- 使用根目录下 `docker-compose.yml` 一键启动所有服务：
  ```bash
  docker-compose up -d
  ```

## 依赖环境
- JDK 8+
- Maven 3.6+
- Node.js 16+
- Docker & Docker Compose

## 主要文件结构
- `Rentaler_System/` 后端主服务
- `Rentaler_Device_Monitor/` 设备监控服务
- `Rentaler_Monitor_Api/` 监控 API 服务
- `Rentaler_System_UI/` 前端界面
- `docker-compose.yml` 容器编排文件
- `docs/` 项目相关文档

## 文档与参考
- [docs/api-reference.md](docs/api-reference.md) 接口文档
- [docs/authority-management.md](docs/authority-management.md) 权限管理说明
- [docs/command.md](docs/command.md) 命令说明
- [docs/storage.md](docs/storage.md) 存储说明
- [docs/struct.md](docs/struct.md) 结构说明

## License
详见 LICENSE 文件。

