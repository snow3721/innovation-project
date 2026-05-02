#!/bin/bash
# ========================================
# 高校创新项目系统 - 一键部署脚本
# ========================================
# 用法: bash deploy.sh [选项]
#   bash deploy.sh          # 完整部署（构建+启动）
#   bash deploy.sh start    # 启动服务
#   bash deploy.sh stop     # 停止服务
#   bash deploy.sh restart  # 重启服务
#   bash deploy.sh logs     # 查看日志
#   bash deploy.sh status   # 查看状态
# ========================================

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info()  { echo -e "${GREEN}[INFO]${NC} $1"; }
warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# 项目根目录
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
DEPLOY_DIR="$SCRIPT_DIR"

# 检查 Docker 是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装，请先安装 Docker: https://docs.docker.com/engine/install/"
    fi
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        error "Docker Compose 未安装，请先安装 Docker Compose"
    fi
    info "Docker 环境检查通过"
}

# 检查基础设施服务是否可达
check_infra() {
    info "检查基础设施服务连通性..."

    # 检查 MySQL
    if ! docker run --rm --network host mysql:8.0 mysqladmin ping -h 192.168.100.128 -P 3307 -u root -proot --silent 2>/dev/null; then
        warn "MySQL (192.168.100.128:3307) 不可达，请确认服务是否启动"
    else
        info "MySQL 连接正常"
    fi

    # 检查 MinIO
    if ! curl -sf http://192.168.100.128:9000/minio/health/live > /dev/null 2>&1; then
        warn "MinIO (192.168.100.128:9000) 不可达"
    else
        info "MinIO 连接正常"
    fi

    # 检查 RabbitMQ
    if ! curl -sf http://192.168.100.128:15672 > /dev/null 2>&1; then
        warn "RabbitMQ (192.168.100.128:5672) 管理界面不可达（不影响运行）"
    else
        info "RabbitMQ 连接正常"
    fi
}

# 完整构建与部署
deploy() {
    info "========== 开始完整部署 =========="

    check_docker
    check_infra

    # 进入部署目录
    cd "$DEPLOY_DIR"

    info "构建并启动所有服务（首次可能需要10-20分钟）..."
    docker compose up -d --build

    info "等待后端服务启动..."
    sleep 10

    # 等待后端健康检查通过
    local retry=0
    local max_retry=30
    while [ $retry -lt $max_retry ]; do
        if curl -sf http://localhost:8081/api/v1/auth/login > /dev/null 2>&1; then
            break
        fi
        retry=$((retry+1))
        info "等待后端就绪... ($retry/$max_retry)"
        sleep 5
    done

    if [ $retry -eq $max_retry ]; then
        error "后端服务启动超时，请查看日志: docker compose logs backend"
    fi

    info "========== 部署完成 =========="
    info "前端访问地址: http://<虚拟机IP>"
    info "后端API地址:  http://<虚拟机IP>:8081"
    info "接口文档地址:  http://<虚拟机IP>/doc.html"
    info ""
    info "常用命令:"
    info "  查看日志: bash deploy.sh logs"
    info "  停止服务: bash deploy.sh stop"
    info "  重启服务: bash deploy.sh restart"
}

# 启动服务
start() {
    cd "$DEPLOY_DIR"
    info "启动服务..."
    docker compose up -d
    info "服务已启动"
}

# 停止服务
stop() {
    cd "$DEPLOY_DIR"
    info "停止服务..."
    docker compose down
    info "服务已停止"
}

# 重启服务
restart() {
    cd "$DEPLOY_DIR"
    info "重启服务..."
    docker compose restart
    info "服务已重启"
}

# 查看日志
logs() {
    cd "$DEPLOY_DIR"
    docker compose logs -f --tail=100
}

# 查看状态
status() {
    cd "$DEPLOY_DIR"
    docker compose ps
    echo ""
    info "后端健康检查:"
    if curl -sf http://localhost:8081/api/v1/auth/login > /dev/null 2>&1; then
        info "后端服务运行正常"
    else
        warn "后端服务不可达"
    fi
    info "前端健康检查:"
    if curl -sf http://localhost:80 > /dev/null 2>&1; then
        info "前端服务运行正常"
    else
        warn "前端服务不可达"
    fi
}

# 主入口
case "${1:-deploy}" in
    deploy)  deploy  ;;
    start)   start   ;;
    stop)    stop    ;;
    restart) restart ;;
    logs)    logs    ;;
    status)  status  ;;
    *)
        echo "用法: bash deploy.sh [deploy|start|stop|restart|logs|status]"
        exit 1
        ;;
esac
