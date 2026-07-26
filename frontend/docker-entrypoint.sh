#!/bin/sh
set -eu

BUILD_INFO="/usr/share/nginx/html/assets/build-info.js"
DEPLOY_COMMIT="/usr/share/nginx/html/assets/.deploy-commit"
APP_VERSION_FILE="/usr/share/nginx/html/assets/.app-version"

VERSION="0.1.0"
if [ -f "$APP_VERSION_FILE" ]; then
  VERSION=$(tr -d '[:space:]' < "$APP_VERSION_FILE")
fi

COMMIT="${TRCON_COMMIT_HASH:-${SOURCE_COMMIT:-${GIT_COMMIT:-${COOLIFY_SOURCE_COMMIT:-}}}}"

case "$COMMIT" in
  '$SOURCE_COMMIT' | '${SOURCE_COMMIT}') COMMIT="" ;;
esac

if [ -z "$COMMIT" ] && [ -f "$DEPLOY_COMMIT" ]; then
  COMMIT=$(tr -d '[:space:]' < "$DEPLOY_COMMIT")
fi

if [ -z "$COMMIT" ] || [ "$COMMIT" = "unknown" ]; then
  COMMIT="unknown"
else
  COMMIT=$(printf '%s' "$COMMIT" | cut -c1-12)
fi

cat > "$BUILD_INFO" <<EOF
// Generated at container start (docker-entrypoint.sh).
window.TRCON_APP_VERSION = '${VERSION}';
window.TRCON_COMMIT_HASH = '${COMMIT}';
EOF

export PORT="${PORT:-80}"
envsubst '${PORT}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf

exec "$@"
