#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "\${BASH_SOURCE[0]}")/.." && pwd)"
UPSTREAM_FILE="$ROOT/telegram-integration/UPSTREAM"
WORKDIR="\${1:-$ROOT/.work/telegram}"

if [[ ! -f "$UPSTREAM_FILE" ]]; then
  echo "Missing $UPSTREAM_FILE" >&2
  exit 1
fi

# shellcheck disable=SC1090
source "$UPSTREAM_FILE"

echo "[vayGram] upstream: $repo"
echo "[vayGram] commit:   $commit"
echo "[vayGram] target:   $WORKDIR"

rm -rf "$WORKDIR"
mkdir -p "$(dirname "$WORKDIR")"

git init -q "$WORKDIR"
git -C "$WORKDIR" remote add origin "$repo"
git -C "$WORKDIR" fetch -q --depth 1 origin "$commit"
git -C "$WORKDIR" checkout -q --detach FETCH_HEAD

TARGET="$WORKDIR/TMessagesProj/src/main/java/app/vaygram"
mkdir -p "$TARGET"

cp -R "$ROOT/vay-core/src/main/java/app/vaygram/core" "$TARGET/"
cp -R "$ROOT/vay-android/src/main/java/app/vaygram/android" "$TARGET/"
cp -R "$ROOT/telegram-integration/TMessagesProj/src/main/java/app/vaygram/"* "$TARGET/"

for patch in "$ROOT"/telegram-integration/patches/*.patch; do
  echo "[vayGram] applying $(basename "$patch")"
  git -C "$WORKDIR" apply --check "$patch"
  git -C "$WORKDIR" apply "$patch"
done

echo
echo "[vayGram] overlay ready."
echo "[vayGram] Telegram source: $WORKDIR"
echo "[vayGram] vayGram package: $TARGET"
echo
echo "Next: configure Telegram build credentials/api keys according to upstream README,"
echo "then open the generated checkout in Android Studio."
