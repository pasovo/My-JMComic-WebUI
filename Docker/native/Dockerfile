# 使用较完整的基础镜像（含glibc等依赖）
FROM python:3.9.22-bookworm

# 创建工作目录
RUN mkdir -p /app
WORKDIR /app

# 下载 My-JMComic-WebUI 的 Linux 可执行文件，并添加执行权限
RUN curl -L -o /app/app https://github.com/Jackxwb/My-JMComic-WebUI/releases/latest/download/app_linux_amd64 && \
    chmod +x /app/app

# 执行主程序（Linux 可执行）
CMD ["/app/app"]
