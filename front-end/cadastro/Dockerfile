FROM node:22-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm ci || npm install

COPY . .

ARG API_URL=/api

RUN sed -i "s|apiUrl: 'http://localhost:8080'|apiUrl: '${API_URL}'|" src/environments/environment.ts

RUN npm run build

RUN mkdir -p /app/nginx-dist && \
    cp -r "$(dirname "$(find dist -name index.html | head -n 1)")"/* /app/nginx-dist/

FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/nginx-dist /usr/share/nginx/html

EXPOSE 80
