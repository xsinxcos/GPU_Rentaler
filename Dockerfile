# 构建阶段
FROM maven:3.8.8-eclipse-temurin-21-alpine AS build

WORKDIR /app
COPY pom.xml .
COPY Rentaler_Device_Monitor ./Rentaler_Device_Monitor
COPY Rentaler_Monitor_Api ./Rentaler_Monitor_Api
COPY Rentaler_System ./Rentaler_System
COPY Rentaler_System_UI ./Rentaler_System_UI

# 构建应用（跳过测试）
#RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:21-jre-alpine-3.22

# 设置工作目录
WORKDIR /app

# 复制构建好的 jar 包
COPY --from=build /app/Rentaler_System/target/*.jar app.jar

# 暴露应用端口
EXPOSE 9091
# 暴露 Dubbo 端口
EXPOSE 20881
# 暴露 Dubbo QoS 端口
EXPOSE 33333

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Dfile.encoding=UTF-8"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
