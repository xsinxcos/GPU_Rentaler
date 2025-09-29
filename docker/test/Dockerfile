# 使用官方 Ubuntu 镜像
FROM ubuntu:22.04

RUN apt-get clean

# 避免交互式提示
ENV DEBIAN_FRONTEND=noninteractive

# 切换国内镜像源（可选，国外可注释）
RUN sed -i 's/http:\/\/archive.ubuntu.com/http:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list

# 更新源并安装 SSH 服务和 sudo
RUN apt-get update && \
    apt-get install -y openssh-server sudo && \
    mkdir /var/run/sshd && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 设置 root 密码（请自行修改）
RUN echo 'root:rootpassword' | chpasswd

# 允许 root 通过 SSH 登录
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
RUN sed -i 's/UsePAM yes/UsePAM no/' /etc/ssh/sshd_config

# 开放 SSH 端口
EXPOSE 22

# 启动 SSH 服务
CMD ["/usr/sbin/sshd", "-D"]
