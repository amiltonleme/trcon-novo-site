#!/bin/sh
set -eu

export SITE_API_UPSTREAM="${SITE_API_UPSTREAM:-http://trcon-site-backend:8080}"

envsubst '${SITE_API_UPSTREAM}' \
  < /etc/nginx/templates/default.conf.template \
  > /etc/nginx/conf.d/default.conf

exec "$@"
