# Docker + 静态可执行运行文件 环境编译说明

此Docker只要Python环境即可运行，目前静态可执行运行文件只有`amd64`和`Window`，`Window`下请自行测试

此方法相对来说简单些许，理论上运行速度也快

编译所需步骤：
1. 从`github`的`releases`下载 Linux 静态可执行运行文件，重命名为`app`，放入到`project`目录下
2. 自行修改[docker-compose.yml](docker-compose.yml)为自己的内容（如外部端口、漫画库路径）。如果需要使用其他用户权限，请连同[Dockerfile](Dockerfile)一起修改
3. 在本目录执行命令`docker build`，编译所需Docker镜像（请配置好你的网络：Docker镜像、Python源、Debian源 等）
4. 在本目录执行命令`docker compose up -d`运行镜像

---

本镜像基于`python:3.9.22-bookworm`为底，自动安装`jmcomic`库

后续程序更新，只需要停止Docker容器，重复前两个步骤下载最新文件替换，然后重启Docker镜像即可