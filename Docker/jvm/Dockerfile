FROM python:3.9.22-bookworm

RUN mkdir -p /app
WORKDIR /app

# 设置阿里云镜像源并更新 apt 缓存，然后安装 git
# 注意：这里我们使用 sed 来修改 sources.list，将默认源替换为阿里云的
# 同时安装 ca-certificates 以确保 HTTPS 连接的安全性
# 设置阿里云镜像源并更新 apt 缓存，然后安装 git
RUN echo "deb https://mirrors.aliyun.com/debian/ bookworm main contrib non-free" > /etc/apt/sources.list && \
    echo "deb https://mirrors.aliyun.com/debian/ bookworm-updates main contrib non-free" >> /etc/apt/sources.list && \
    echo "deb https://mirrors.aliyun.com/debian-security/ bookworm-security main contrib non-free" >> /etc/apt/sources.list
RUN apt-get -o Acquire::AllowInsecureRepositories=true -o Acquire::AllowDownloadInsecure=true update
RUN apt-get install -y --no-install-recommends --allow-unauthenticated ca-certificates git

# 指定用户UID和GID
ARG USER_ID=33
ARG GROUP_ID=100
ARG USERNAME=www-data

# 创建具有特定 UID/GID 的用户和组
#RUN addgroup -g ${GROUP_ID} users && \
#RUN adduser -u ${USER_ID} -G users -D -h /home/www-data -s /bin/sh www-data

# 创建或更新组和用户的脚本
RUN set -xe; \
    \
    # 检查并创建组 \
    if ! getent group ${GROUP_ID} >/dev/null; then \
        if getent group users >/dev/null; then \
            # 如果users组存在但GID不匹配，则修改GID \
            [ $(getent group users | cut -d: -f3) -eq ${GROUP_ID} ] || \
            groupmod -g ${GROUP_ID} users; \
        else \
            # 创建新组 \
            addgroup -g ${GROUP_ID} users; \
        fi; \
    fi; \
    \
    # 检查并创建用户 \
    if ! getent passwd ${USER_ID} >/dev/null; then \
        if getent passwd ${USERNAME} >/dev/null; then \
            # 如果用户存在但UID不匹配，则修改UID \
            [ $(getent passwd ${USERNAME} | cut -d: -f3) -eq ${USER_ID} ] || \
            usermod -u ${USER_ID} ${USERNAME}; \
        else \
            # 创建新用户 \
            adduser -u ${USER_ID} -G users -D -h /home/${USERNAME} -s /bin/sh ${USERNAME}; \
        fi; \
    fi; \
    \
    # 确保用户属于正确的组 \
    if ! id -nG ${USERNAME} | grep -qw users; then \
        adduser ${USERNAME} users; \
    fi

# 设置sudo权限（可选）
RUN echo '${USERNAME} ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

# 安装必要工具
RUN apt-get install -y --no-install-recommends --allow-unauthenticated \
    gcc \
    python3-dev \
    tar \
    unzip \
    curl

# quarkus 编译环境
RUN apt-get install -y --no-install-recommends --allow-unauthenticated \
    build-essential libz-dev zlib1g-dev

# 切换到该用户
#USER ${USERNAME}

# 安装 graalvm
COPY graalvm-jdk-21_linux-x64_bin.tar.gz /tmp/graalvm.tar.gz

RUN mkdir -p /graalvm
RUN cd /tmp && tar -xvzf /tmp/graalvm.tar.gz
RUN mv /tmp/graalvm-jdk-21.0.7+8.1/* /graalvm
ENV GRAALVM_HOME /graalvm
ENV JAVA_HOME ${GRAALVM_HOME}
ENV PATH ${GRAALVM_HOME}/bin:${PATH}

RUN chown -R ${USER_ID}:${GROUP_ID} /graalvm

## -- python --

# 安装 jmcomic 库，如果无法安装，请自行更换源
# RUN pip install jmcomic -i https://pypi.org/project -U
RUN pip install jmcomic -i https://mirrors.aliyun.com/pypi/simple/ -U

# 补充jmcomic项目依赖
RUN pip install flask -i https://mirrors.aliyun.com/pypi/simple/ -U

## -- python --

# 将你的 quarkus 应用代码复制到 Docker 容器中
#COPY project /app

RUN chown -R ${USER_ID}:${GROUP_ID} /tmp
RUN mkdir -p /var/www
RUN chown -R ${USER_ID}:${GROUP_ID} /var/www

CMD ["java", "-jar", "my_jmcomic_manage-1.0-SNAPSHOT-runner.jar"]