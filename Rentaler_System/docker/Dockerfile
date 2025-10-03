# 使用官方 Eclipse Temurin Java 21 镜像作为基础镜像
FROM eclipse-temurin:21-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制 Maven 构建好的 jar 包
COPY target/Rentaler_System-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口（Spring Boot 默认 8080）
EXPOSE 8080

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar"]
