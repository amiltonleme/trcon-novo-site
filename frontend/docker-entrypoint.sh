#!/bin/sh
set -eu

BUILD_INFO="/usr/share/nginx/html/assets/build-info.js"
COMMIT="${SOURCE_COMMIT:-${GIT_COMMIT:-${COOLIFY_SOURCE_COMMIT:-}}}"
VERSION="0.1.0"

if [ -f "$BUILD_INFO" ]; then
  EXTRACTED=$(sed -n "s/.*TRCON_APP_VERSION = '\([^']*\)'.*/\1/p" "$BUILD_INFO" | head -n 1)
  if [ -n "$EXTRACTED" ]; then
    VERSION="$EXTRACTED"
  fi
fi

if [ -n "$COMMIT" ] && [ "$COMMIT" != "unknown" ]; then
  SHORT=$(printf '%s' "$COMMIT" | cut -c1-12)
  cat > "$BUILD_INFO" <<EOF
// Generated at container start from deployment commit.
window.TRCON_APP_VERSION = '${VERSION}';
window.TRCON_COMMIT_HASH = '${SHORT}';
EOF
fi

exec "$@"
