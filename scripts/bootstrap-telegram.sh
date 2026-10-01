#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
UPSTREAM_FILE="$ROOT/telegram-integration/UPSTREAM"
WORKDIR="${1:-$ROOT/.work/telegram}"

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

if [[ "${VAYGRAM_WITH_SUBMODULES:-0}" == "1" ]]; then
  echo "[vayGram] fetching Telegram submodules for a real Android build..."
  git -C "$WORKDIR" submodule update --init --recursive --depth 1
fi

TARGET="$WORKDIR/TMessagesProj/src/main/java/app/vaygram"
mkdir -p "$TARGET"

cp -R "$ROOT/vay-core/src/main/java/app/vaygram/core" "$TARGET/"
cp -R "$ROOT/vay-android/src/main/java/app/vaygram/android" "$TARGET/"
cp -R "$ROOT/telegram-integration/TMessagesProj/src/main/java/app/vaygram/"* "$TARGET/"

RESOURCE_OVERLAY="$ROOT/telegram-integration/TMessagesProj/src/main/res"
if [[ -d "$RESOURCE_OVERLAY" ]]; then
  echo "[vayGram] overlaying vayGram-owned Android resources..."
  cp -R "$RESOURCE_OVERLAY/." "$WORKDIR/TMessagesProj/src/main/res/"
fi

echo "[vayGram] merging vayGram strings into Telegram localization inputs..."
bash "$ROOT/scripts/merge-vaygram-localization.sh" "$WORKDIR"

for patch in "$ROOT"/telegram-integration/patches/*.patch; do
  echo "[vayGram] applying $(basename "$patch")"
  git -C "$WORKDIR" apply --check "$patch"
  git -C "$WORKDIR" apply "$patch"
done

echo "[vayGram] applying vayGram application identity..."
bash "$ROOT/scripts/apply-vaygram-branding.sh" "$WORKDIR"

echo
echo "[vayGram] overlay ready."
echo "[vayGram] Telegram source: $WORKDIR"
echo "[vayGram] vayGram package: $TARGET"
echo
echo "For a build-ready checkout use:"
echo "  VAYGRAM_WITH_SUBMODULES=1 bash scripts/bootstrap-telegram.sh"
echo
echo "Telegram API credentials are read from VAYGRAM_API_ID / VAYGRAM_API_HASH"
echo "or matching keys in local.properties. Official Telegram API credentials are not reused."
