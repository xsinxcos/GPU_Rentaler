# NVIDIA CUDA 基础镜像
FROM nvidia/cuda:12.0.0-base-ubuntu22.04

ENV DEBIAN_FRONTEND=noninteractive

# 切换国内镜像源（可选，国外可注释）
RUN sed -i 's/http:\/\/archive.ubuntu.com/http:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list

# 安装 SSH 和工具
RUN apt-get update && apt-get install -y \
    openssh-server sudo vim curl wget git \
    && rm -rf /var/lib/apt/lists/*

# 创建 SSH 目录
RUN mkdir /var/run/sshd

# 创建用户
ARG USERNAME=user
RUN useradd -m -s /bin/bash $USERNAME && \
    echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

# 设置允许通过环境变量指定密码
ENV USER_PASSWORD=changeme
RUN echo "$USERNAME:$USER_PASSWORD" | chpasswd

# 配置 SSH
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
RUN sed -i 's/PermitEmptyPasswords no/PermitEmptyPasswords yes/' /etc/ssh/sshd_config

# 暴露 SSH 端口
EXPOSE 22

# 启动 SSH
CMD ["/usr/sbin/sshd", "-D"]
