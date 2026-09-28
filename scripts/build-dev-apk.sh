#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WORKDIR="${VAYGRAM_WORKDIR:-$ROOT/.work/telegram-build}"
OUTDIR="${VAYGRAM_OUTDIR:-$ROOT/.work/artifacts}"
APK_NAME="vayGram-0.1-dev.apk"

: "${VAYGRAM_API_ID:?Set VAYGRAM_API_ID to your Telegram API ID}"
: "${VAYGRAM_API_HASH:?Set VAYGRAM_API_HASH to your Telegram API hash}"

if [[ ! "$VAYGRAM_API_ID" =~ ^[0-9]+$ ]] || [[ "$VAYGRAM_API_ID" == "0" ]]; then
  echo "VAYGRAM_API_ID must be a non-zero integer" >&2
  exit 2
fi

if [[ ! "$VAYGRAM_API_HASH" =~ ^[0-9a-fA-F]{32}$ ]]; then
  echo "VAYGRAM_API_HASH must be a 32-character hexadecimal Telegram API hash" >&2
  exit 2
fi

export VAYGRAM_WITH_SUBMODULES=1
bash "$ROOT/scripts/bootstrap-telegram.sh" "$WORKDIR"

DEV_KEYSTORE="${VAYGRAM_DEV_KEYSTORE:-$ROOT/.work/keys/vaygram-dev.keystore}"
KEYSTORE="$WORKDIR/TMessagesProj/config/release.keystore"

if [[ ! -f "$DEV_KEYSTORE" ]]; then
  echo "[vayGram] generating a project-local development signing key..."
  mkdir -p "$(dirname "$DEV_KEYSTORE")"
  keytool -genkeypair -v   -keystore "$DEV_KEYSTORE"   -storepass android   -alias androidkey   -keypass android   -keyalg RSA   -keysize 2048   -validity 10000   -dname "CN=vayGram Dev, OU=Development, O=vayGram, C=XX" >/dev/null
else
  echo "[vayGram] reusing local development signing key."
fi

cp "$DEV_KEYSTORE" "$KEYSTORE"

echo "[vayGram] building afatDebug..."
(
  cd "$WORKDIR"
  chmod +x ./gradlew
  ./gradlew :TMessagesProj_App:assembleAfatDebug --no-daemon --stacktrace
)

APK="$(find "$WORKDIR/TMessagesProj_App/build/outputs/apk" -type f -name 'app.apk' | head -n 1)"
if [[ -z "$APK" || ! -f "$APK" ]]; then
  echo "Build finished but app.apk was not found" >&2
  exit 3
fi

mkdir -p "$OUTDIR"
cp "$APK" "$OUTDIR/$APK_NAME"

echo
echo "[vayGram] APK ready:"
echo "  $OUTDIR/$APK_NAME"
echo
echo "Package: app.vaygram.messenger.beta"
echo "Version: 0.1-dev"
