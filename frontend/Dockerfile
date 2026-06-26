FROM nginx:alpine

# Xóa cấu hình Nginx mặc định
RUN rm -rf /usr/share/nginx/html/*

# Copy toàn bộ file tĩnh (HTML, CSS, JS) vào Nginx
COPY . /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
