#!/usr/bin/env bash
set -euo pipefail

APK="${1:?usage: check-apk-install-smoke.sh <apk>}"
PACKAGE_ID="${VAYGRAM_SMOKE_PACKAGE:-app.vaygram.messenger.beta}"
VERSION_NAME="${VAYGRAM_SMOKE_VERSION:-0.1-dev}"

if [[ ! -f "$APK" ]]; then
  echo "[vayGram smoke] APK not found: $APK" >&2
  exit 2
fi

command -v adb >/dev/null 2>&1 || {
  echo "[vayGram smoke] adb is required" >&2
  exit 2
}

adb wait-for-device

echo "[vayGram smoke] ensuring a clean install..."
adb uninstall "$PACKAGE_ID" >/dev/null 2>&1 || true
adb install "$APK"

PACKAGE_PATH="$(adb shell pm path "$PACKAGE_ID" | tr -d '\r')"
if [[ "$PACKAGE_PATH" != package:* ]]; then
  echo "[vayGram smoke] package was not installed: $PACKAGE_ID" >&2
  exit 3
fi

PACKAGE_DUMP="$(adb shell dumpsys package "$PACKAGE_ID" | tr -d '\r')"
INSTALLED_VERSION="$(
  printf '%s\n' "$PACKAGE_DUMP" \
    | sed -n 's/^[[:space:]]*versionName=//p' \
    | head -n 1
)"
if [[ "$INSTALLED_VERSION" != "$VERSION_NAME" ]]; then
  echo "[vayGram smoke] unexpected versionName: $INSTALLED_VERSION" >&2
  exit 3
fi

adb logcat -c
adb shell am force-stop "$PACKAGE_ID"

echo "[vayGram smoke] launching package through its launcher intent..."
adb shell monkey -p "$PACKAGE_ID" -c android.intent.category.LAUNCHER 1

sleep 5

PID="$(adb shell pidof "$PACKAGE_ID" 2>/dev/null | tr -d '\r' | xargs || true)"
if [[ -z "$PID" ]]; then
  echo "[vayGram smoke] app process is not running after launch" >&2
  adb logcat -d -t 300 >&2 || true
  exit 4
fi

if adb logcat -b crash -d | grep -Fq "$PACKAGE_ID"; then
  echo "[vayGram smoke] crash log contains $PACKAGE_ID after launch" >&2
  adb logcat -b crash -d >&2 || true
  exit 5
fi

echo "[vayGram smoke] clean install and launcher smoke passed."
echo "[vayGram smoke] package=$PACKAGE_ID version=$INSTALLED_VERSION pid=$PID"
