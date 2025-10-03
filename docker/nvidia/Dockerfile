# NVIDIA CUDA 基础镜像
FROM nvidia/cuda:12.0.0-base-ubuntu22.04

ENV DEBIAN_FRONTEND=noninteractive

# 切换国内镜像源（可选，国外可注释）
RUN sed -i 's|http://archive.ubuntu.com|http://mirrors.aliyun.com|g' /etc/apt/sources.list

# 安装 SSH 和基本工具
RUN apt-get update && apt-get install -y \
    openssh-server sudo vim curl wget git \
    && rm -rf /var/lib/apt/lists/*

# 创建 SSH 运行目录
RUN mkdir /var/run/sshd

# 设置构建参数（默认 user）
ARG USERNAME=root
ARG USER_PASSWORD=123456

# 设置环境变量，供后续使用
ENV USERNAME=${USERNAME}
ENV USER_PASSWORD=${USER_PASSWORD}

# 添加用户并设置密码
RUN useradd -m -s /bin/bash ${USERNAME} && \
    echo "${USERNAME} ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers && \
    echo "${USERNAME}:${USER_PASSWORD}" | chpasswd

# SSH 配置调整
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config && \
    sed -i 's/PermitEmptyPasswords no/PermitEmptyPasswords yes/' /etc/ssh/sshd_config

# 暴露 SSH 端口
EXPOSE 22

# 启动 sshd
CMD ["/usr/sbin/sshd", "-D"]
