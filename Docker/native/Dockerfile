#FROM python:3.9.22-alpine
FROM python:3.9.22-bookworm

RUN mkdir -p /app
WORKDIR /app

# 指定用户UID和GID
ARG USER_ID=33
ARG GROUP_ID=100
ARG USERNAME=www-data

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

## -- python --

# 安装 jmcomic 库，如果无法安装，请自行更换源
# RUN pip install jmcomic -i https://pypi.org/project -U
RUN pip install jmcomic -i https://mirrors.aliyun.com/pypi/simple/ -U

# 补充项目依赖
RUN pip install flask -i https://mirrors.aliyun.com/pypi/simple/ -U

## -- python --

CMD ["/app/app"]