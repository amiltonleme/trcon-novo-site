# syntax=docker/dockerfile:1

# Build context = raiz do repo (Coolify: Base Directory "." ou vazio).
# Dockerfile location: frontend/Dockerfile
#
# Assim .git entra no contexto e inject-build-info resolve o commit sem depender de build-arg.

FROM node:22-alpine AS prepare
WORKDIR /app

COPY .git ./.git
COPY frontend ./frontend

WORKDIR /app/frontend

ARG SOURCE_COMMIT=
ARG GIT_COMMIT=
RUN node scripts/inject-build-info.mjs "${SOURCE_COMMIT:-$GIT_COMMIT}"

FROM nginx:1.27-alpine AS runtime
COPY --from=prepare /app/frontend /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- "http://127.0.0.1/" | grep -qi '<html' || exit 1

CMD ["nginx", "-g", "daemon off;"]
