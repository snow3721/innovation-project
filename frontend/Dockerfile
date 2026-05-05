# 多阶段构建：Node 编译 + Nginx 运行
# 构建上下文为项目根目录，路径需加 frontend/ 前缀
FROM node:18-alpine AS builder
WORKDIR /app
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm install --registry=https://registry.npmmirror.com
COPY frontend/ .
RUN npx vite build

FROM nginx:1.24-alpine
COPY deploy/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
