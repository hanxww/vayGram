#!/usr/bin/env bash
set -euo pipefail

UPSTREAM_URL="${UPSTREAM_URL:-https://github.com/DrKLO/Telegram.git}"
UPSTREAM_REF="${UPSTREAM_REF:-master}"
WORKDIR="${1:-telegram-worktree}"

if [ -e "$WORKDIR" ]; then
  echo "Refusing to overwrite existing path: $WORKDIR" >&2
  exit 1
fi

echo "[vayGram] cloning Telegram Android..."
git clone --depth 1 --branch "$UPSTREAM_REF" "$UPSTREAM_URL" "$WORKDIR"

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TARGET="$WORKDIR/TMessagesProj/src/main/java/app/vaygram"

mkdir -p "$TARGET/core" "$TARGET/android"

echo "[vayGram] copying VayCore..."
cp -R "$ROOT/vay-core/src/main/java/app/vaygram/core/." "$TARGET/core/"

echo "[vayGram] copying Android bridge..."
cp -R "$ROOT/vay-android/src/main/java/app/vaygram/android/." "$TARGET/android/"

echo "[vayGram] copying Telegram overlay..."
cp -R "$ROOT/telegram-integration/TMessagesProj/." "$WORKDIR/TMessagesProj/"

cat <<'EOF'

[vayGram] overlay installed.

Next manual integration hook:
  In Telegram's SettingsActivity, add an item which opens:

    presentFragment(new app.vaygram.ui.VaySettingsActivity());

Then configure Telegram API credentials according to Telegram's build instructions
and build the desired TMessagesProj app target.

The first rendering hooks should call app.vaygram.telegram.VayAppearance rather
than reading VayCore directly.
EOF
