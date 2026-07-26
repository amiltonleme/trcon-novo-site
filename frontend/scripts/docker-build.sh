#!/bin/sh
set -eu

echo "build SOURCE_COMMIT=${SOURCE_COMMIT:-} GIT_COMMIT=${GIT_COMMIT:-}"

COMMIT="${SOURCE_COMMIT:-}"
if [ -z "$COMMIT" ]; then
  COMMIT="${GIT_COMMIT:-}"
fi
if [ -z "$COMMIT" ]; then
  COMMIT="unknown"
fi

printf '%s' "$COMMIT" > assets/.deploy-commit
node -p "require('./package.json').version" > assets/.app-version
node scripts/inject-build-info.mjs "$COMMIT"

echo "Build info stamped: $COMMIT"
