# 高校创新项目系统 - 部署指南

## 环境要求

| 组件 | 要求 | 说明 |
|------|------|------|
| 操作系统 | Linux (CentOS 7+ / Ubuntu 18.04+) | 虚拟机系统 |
| Docker | 20.10+ | 容器运行环境 |
| Docker Compose | v2.0+ | 容器编排工具 |
| 内存 | ≥ 2GB | 后端 JVM 默认 512MB |
| 磁盘 | ≥ 10GB | 镜像 + 日志 + 上传文件 |

## 基础设施服务（已在 192.168.100.128 上运行）

| 服务 | 地址 | 账号/密码 |
|------|------|-----------|
| MySQL 8.0 | 192.168.100.128:3307 | root / root |
| MongoDB | 192.168.100.128:27017 | 无认证 |
| MinIO | 192.168.100.128:9000 | minioadmin / minioadmin |
| RabbitMQ | 192.168.100.128:5672 | guest / guest |

## 快速部署

### 1. 将项目上传到虚拟机

```bash
# 方式一：Git 克隆（推荐）
git clone https://github.com/snow3721/innovation-project.git
cd innovation-project

# 方式二：SCP 上传
# 在本地执行：
scp -r d:/innovation user@<虚拟机IP>:/home/user/innovation-project
```

### 2. 安装 Docker（如未安装）

```bash
# Ubuntu
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# CentOS
yum install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
systemctl enable docker && systemctl start docker

# 退出重新登录使 docker 组生效
```

### 3. 一键部署

```bash
cd /path/to/innovation-project/deploy

# 赋予执行权限
chmod +x deploy.sh

# 完整部署（构建镜像 + 启动服务）
bash deploy.sh
```

首次部署需要编译后端 Maven 依赖，大约 10-20 分钟。

### 4. 访问验证

| 地址 | 说明 |
|------|------|
| `http://<虚拟机IP>` | 前端页面 |
| `http://<虚拟机IP>:8081` | 后端 API 直连 |
| `http://<虚拟机IP>/doc.html` | Knife4j 接口文档 |

默认管理员账号：`admin` / `admin123`

## 常用运维命令

```bash
cd /path/to/innovation-project/deploy

bash deploy.sh start     # 启动服务
bash deploy.sh stop      # 停止服务
bash deploy.sh restart   # 重启服务
bash deploy.sh logs      # 查看实时日志
bash deploy.sh status    # 查看服务状态
```

## 手动操作（不使用部署脚本）

```bash
cd /path/to/innovation-project/deploy

# 构建并启动
docker compose up -d --build

# 查看日志
docker compose logs -f backend
docker compose logs -f frontend

# 停止
docker compose down

# 仅重启后端（代码更新后）
docker compose up -d --build backend

# 仅重启前端
docker compose up -d --build frontend
```

## 配置修改

### 修改数据库连接

编辑 `deploy/docker-compose.yml` 中的 `environment` 部分：

```yaml
environment:
  - SPRING_DATASOURCE_URL=jdbc:mysql://<你的MySQL地址>:3307/innovation_project?...
  - SPRING_DATASOURCE_USERNAME=root
  - SPRING_DATASOURCE_PASSWORD=root
```

### 修改后端 JVM 内存

编辑 `backend/Dockerfile` 中的 ENTRYPOINT：

```dockerfile
ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", "app.jar"]
```

### 修改 Nginx 配置

编辑 `deploy/nginx/default.conf`，修改后重建前端容器：

```bash
docker compose up -d --build frontend
```

## 不使用 Docker 的部署方式

### 后端手动部署

```bash
# 1. 安装 JDK 11
sudo apt install openjdk-11-jdk   # Ubuntu
sudo yum install java-11-openjdk   # CentOS

# 2. 编译
cd backend
mvn clean package -DskipTests

# 3. 运行
nohup java -jar target/innovation-project-1.0.0.jar \
  --spring.datasource.url=jdbc:mysql://192.168.100.128:3307/innovation_project?... \
  > backend.log 2>&1 &
```

### 前端手动部署

```bash
# 1. 安装 Node.js 18
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# 2. 编译
cd frontend
npm install --registry=https://registry.npmmirror.com
npm run build

# 3. 安装 Nginx
sudo apt install nginx   # Ubuntu
sudo yum install nginx   # CentOS

# 4. 配置 Nginx
sudo cp deploy/nginx/default.conf /etc/nginx/conf.d/innovation.conf
# 修改 proxy_pass 为 http://127.0.0.1:8081

# 5. 部署静态文件
sudo cp -r dist/* /usr/share/nginx/html/

# 6. 启动 Nginx
sudo systemctl enable nginx
sudo systemctl start nginx
```

## 故障排查

### 后端启动失败

```bash
# 查看后端日志
docker compose logs backend

# 常见原因：
# 1. MySQL 连接失败 → 检查 192.168.100.128:3307 是否可达
# 2. MinIO 连接失败 → 检查 192.168.100.128:9000 是否可达
# 3. 端口冲突 → 修改 docker-compose.yml 中的 ports 映射
```

### 前端访问 502

```bash
# 检查后端是否正常运行
curl http://localhost:8081/api/v1/auth/login

# 如果后端未就绪，前端 Nginx 代理会返回 502
# 等待后端完全启动（约 30-60 秒）
```

### 数据库未初始化

```bash
# 手动导入 schema
mysql -h 192.168.100.128 -P 3307 -u root -proot < backend/src/main/resources/schema.sql
```
