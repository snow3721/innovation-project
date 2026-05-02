# 多阶段构建：Node 编译 + Nginx 运行
FROM node:18-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install --registry=https://registry.npmmirror.com
COPY . .
RUN npm run build

FROM nginx:1.24-alpine
COPY deploy/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
