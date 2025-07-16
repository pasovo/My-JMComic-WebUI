# Docker + JVM 环境编译说明

此Docker需要 Python + java 21 环境才能运行，理论上全平台支持（测试环境为`amd64`）

编译所需步骤：
1. 从`github`的`releases`下载JVM程序压缩包
2. 将压缩包解压到`project`目录下
3. 下载`graalvm-jdk-21_linux-x64_bin.tar.gz`到本目录下面（不可修改名称，如需修改请自行同步修改[Dockerfile](Dockerfile)里对应内容）
4. 自行修改[docker-compose.yml](docker-compose.yml)为自己的内容（如外部端口、漫画库路径）。如果需要使用其他用户权限，请连同[Dockerfile](Dockerfile)一起修改
5. 在本目录执行命令`docker build`，编译所需Docker镜像（请配置好你的网络：Docker镜像、Python源、Debian源 等）
6. 在本目录执行命令`docker compose up -d`运行镜像

---

本镜像基于`python:3.9.22-bookworm`为底，自动安装`jmcomic`库，自动解压配置`graalvm-jdk-21_linux-x64_bin.tar.gz`JVM运行环境到Docker镜像里面。

后续程序更新，只需要停止Docker容器，重复前两个步骤下载最新文件替换，然后重启Docker镜像即可

graalvm环境包[可从此下载  www.graalvm.org/downloads](https://www.graalvm.org/downloads/)