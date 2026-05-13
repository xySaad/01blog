# ─── Stage 1: Build ──────────────────────────────────────────────────────────
FROM node:22-alpine AS build

WORKDIR /app

# Cache node_modules layer
COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration production

# ─── Stage 2: Serve ──────────────────────────────────────────────────────────
FROM nginx:1.27-alpine

# Remove default nginx page
RUN rm -rf /usr/share/nginx/html/*

# Angular output goes to dist/<project-name>/browser with the new @angular/build builder
COPY --from=build /app/dist/01blog/browser /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
