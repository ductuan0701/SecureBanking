FROM nginx:alpine

# Copy cấu hình Nginx nội bộ
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy toàn bộ file tĩnh (HTML, CSS, JS) vào Nginx
COPY . /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
