# 1) Build stage (Node)
FROM node:20-alpine AS build
WORKDIR /app

# nejdřív závislosti
COPY package*.json ./
RUN npm ci

# zdrojáky + build
COPY . .
RUN npm run build -- --configuration production

# 2) Run stage (Nginx)
FROM nginx:alpine

# vlastní Nginx config pro SPA routing (Angular)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# POZOR: jméno projektu v dist musí sedět
# pokud se appka v dist jmenuje "todo" a Angular 17+ generuje dist/todo/browser:
COPY --from=build /app/dist/todo/browser/ /usr/share/nginx/html/

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
