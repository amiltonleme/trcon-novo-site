# syntax=docker/dockerfile:1

# Contexto Coolify: Base Directory = frontend (sem .git no artifact).
# Commit vem de SOURCE_COMMIT (build-arg ou env no entrypoint).

FROM node:22-alpine AS prepare
WORKDIR /app

COPY . .

ARG SOURCE_COMMIT=
ARG GIT_COMMIT=
RUN echo "build SOURCE_COMMIT=${SOURCE_COMMIT}" && \
    node scripts/inject-build-info.mjs "${SOURCE_COMMIT:-$GIT_COMMIT}"

FROM nginx:1.27-alpine AS runtime

COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

COPY --from=prepare /app /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- "http://127.0.0.1/" | grep -qi '<html' || exit 1

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
