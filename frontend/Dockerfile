# syntax=docker/dockerfile:1

# Injeta versão + hash Git em assets/build-info.js antes de servir o site estático.
FROM node:22-alpine AS prepare
WORKDIR /app

ARG SOURCE_COMMIT=
ARG GIT_COMMIT=
ENV SOURCE_COMMIT=${SOURCE_COMMIT} \
    GIT_COMMIT=${GIT_COMMIT}

COPY . .
RUN node scripts/inject-build-info.mjs

FROM nginx:1.27-alpine AS runtime
COPY --from=prepare /app /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- "http://127.0.0.1/" | grep -qi '<html' || exit 1

CMD ["nginx", "-g", "daemon off;"]
