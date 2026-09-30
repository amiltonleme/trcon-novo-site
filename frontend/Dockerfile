FROM node:22-alpine AS site-build
WORKDIR /site
COPY . .
ARG SOURCE_COMMIT
RUN node scripts/stamp-build-info.mjs "$SOURCE_COMMIT"

FROM nginx:1.27-alpine
RUN apk add --no-cache gettext
COPY nginx.conf.template /etc/nginx/templates/default.conf.template
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh
COPY --from=site-build /site /usr/share/nginx/html
ENV SITE_API_UPSTREAM=http://trcon-site-backend:8080
EXPOSE 80
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
