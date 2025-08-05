# 使用较完整的基础镜像（含glibc等依赖）
FROM python:3.9.22-bookworm

# 安装必要工具和 Java 运行环境（如需要）
RUN apt update && \
    apt install -y curl openjdk-17-jre && \
    apt clean

# 创建工作目录
RUN mkdir -p /app
WORKDIR /app

# 下载 My-JMComic-WebUI 的 Linux 可执行文件，并添加执行权限
RUN curl -L -o /app/app https://github.com/Jackxwb/My-JMComic-WebUI/releases/latest/download/app_linux_amd64 && \
    chmod +x /app/app

# 设置用户参数
ARG USER_ID=33
ARG GROUP_ID=100
ARG USERNAME=www-data

# 创建用户和组（若不存在），并赋予 /app 权限
RUN set -xe; \
    if ! getent group ${GROUP_ID} >/dev/null; then \
        addgroup --gid ${GROUP_ID} users; \
    fi; \
    if ! getent passwd ${USER_ID} >/dev/null; then \
        adduser --uid ${USER_ID} --gid ${GROUP_ID} --disabled-password --gecos "" ${USERNAME}; \
    fi; \
    chown -R ${USERNAME}:users /app

# 切换为安全的非 root 用户运行程序
USER ${USERNAME}

# 执行主程序（Linux 可执行）
CMD ["/app/app"]
