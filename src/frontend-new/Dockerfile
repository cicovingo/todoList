FROM node:18-alpine AS build

WORKDIR /app

COPY package.json ./
COPY package-lock.json ./

RUN npm install

COPY . .

RUN npm run build -- --output-path ./dist/frontend-new --configuration production

FROM nginx:alpine

COPY nginx-custom.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist/frontend-new /usr/share/nginx/html

EXPOSE 80

# Nginx'i başlat
CMD ["nginx", "-g", "daemon off;"]
