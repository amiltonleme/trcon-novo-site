# syntax=docker/dockerfile:1

FROM node:22-alpine AS prepare
WORKDIR /app

COPY . .

ARG SOURCE_COMMIT
ARG GIT_COMMIT
RUN chmod +x scripts/docker-build.sh && ./scripts/docker-build.sh

FROM nginx:1.27-alpine AS runtime

RUN apk add --no-cache gettext

COPY docker-entrypoint.sh /docker-entrypoint.sh
COPY nginx.conf.template /etc/nginx/templates/default.conf.template
RUN chmod +x /docker-entrypoint.sh

COPY --from=prepare /app /usr/share/nginx/html

ENV PORT=80

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- "http://127.0.0.1:80/" | grep -qi '<html' || exit 1

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
