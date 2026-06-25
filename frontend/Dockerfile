FROM nginx:alpine

# Xóa cấu hình Nginx mặc định
RUN rm -rf /usr/share/nginx/html/*

# Copy cấu hình reverse proxy
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy toàn bộ file tĩnh (HTML, CSS, JS) vào Nginx
COPY . /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
