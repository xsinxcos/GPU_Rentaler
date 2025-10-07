#FROM nvidia/cuda:12.0.0-base-ubuntu22.04
#
#ENV DEBIAN_FRONTEND=noninteractive
#
#RUN apt-get update && apt-get install -y \
#    python3-pip python3-dev python3-venv curl git && \
#    apt-get clean && rm -rf /var/lib/apt/lists/*
#
## 升级 pip
#RUN pip3 install --upgrade pip
#
## 安装 PyTorch（兼容 CUDA 12.0）
#RUN pip3 install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu121
#
#WORKDIR /app
#COPY test_gpu.py .
#
#CMD ["python3", "test_gpu.py"]

# 基于轻量 Python 镜像
FROM python:3.11-slim

# 安装系统依赖（合并减少层），只装必要的
RUN apt-get update && \
    apt-get install -y --no-install-recommends build-essential ca-certificates wget git && \
    rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app


# 安装 Python 包
# 注意：这里假设 requirements.txt 不包含 torch，因为我们从 PyTorch 官方 wheel 安装 CUDA 12.1 版本
RUN pip3 install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu121

# 复制代码
COPY . /app

# 默认运行 GPU 循环测试脚本
CMD ["python", "test_gpu.py"]
