# ---------- 构建阶段 ----------
FROM maven:3.8.8-eclipse-temurin-21-alpine AS build

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置和模块源码
COPY pom.xml .
COPY Rentaler_Device_Monitor ./Rentaler_Device_Monitor
COPY Rentaler_Monitor_Api ./Rentaler_Monitor_Api
COPY Rentaler_System ./Rentaler_System

# 构建 jar（跳过测试，加快速度）
RUN mvn clean package -DskipTests

# ---------- 运行阶段 ----------
FROM eclipse-temurin:21-jre-alpine-3.22

# 设置运行目录
WORKDIR /app

# 创建挂载点（与 docker-compose 中 volumes 配合）
RUN mkdir -p /app/logs /app/config /app/data

# 拷贝构建好的 jar 包
COPY --from=build /app/Rentaler_System/target/*.jar app.jar

# 开放端口（可选）
EXPOSE 9090
EXPOSE 50051

# 设置 JVM 参数变量
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Dfile.encoding=UTF-8"

# 启动服务
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
