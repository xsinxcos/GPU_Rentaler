# test_gpu.py
import torch
import time

def test_gpu():
    while True:
        print("="*40)
        print("🕒 当前时间：", time.strftime("%Y-%m-%d %H:%M:%S"))

        if torch.cuda.is_available():
            print("✅ CUDA 可用！")
            print("当前使用的 GPU：", torch.cuda.get_device_name(0))
            x = torch.rand(3, 3).cuda()
            y = torch.rand(3, 3).cuda()
            z = x + y
            print("GPU 加法运算成功：\n", z)
        else:
            print("❌ CUDA 不可用，请检查 NVIDIA 驱动和 Docker 配置。")

        time.sleep(5)

if __name__ == "__main__":
    test_gpu()
