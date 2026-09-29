#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WORKDIR="${VAYGRAM_WORKDIR:-$ROOT/.work/telegram-build}"
OUTDIR="${VAYGRAM_OUTDIR:-$ROOT/.work/artifacts}"
APK_NAME="vayGram-0.1-dev.apk"

bash "$ROOT/scripts/preflight-build.sh"

export VAYGRAM_WITH_SUBMODULES=1
bash "$ROOT/scripts/bootstrap-telegram.sh" "$WORKDIR"

DEV_KEYSTORE="${VAYGRAM_DEV_KEYSTORE:-$ROOT/.work/keys/vaygram-dev.keystore}"
KEYSTORE="$WORKDIR/TMessagesProj/config/release.keystore"
KEYSTORE_PASSWORD="${VAYGRAM_KEYSTORE_PASSWORD:-android}"
KEY_ALIAS="${VAYGRAM_KEY_ALIAS:-androidkey}"
KEY_PASSWORD="${VAYGRAM_KEY_PASSWORD:-$KEYSTORE_PASSWORD}"

if [[ ! -f "$DEV_KEYSTORE" ]]; then
  echo "[vayGram] generating a project-local development signing key..."
  mkdir -p "$(dirname "$DEV_KEYSTORE")"
  keytool -genkeypair -v     -keystore "$DEV_KEYSTORE"     -storepass "$KEYSTORE_PASSWORD"     -alias "$KEY_ALIAS"     -keypass "$KEY_PASSWORD"     -keyalg RSA     -keysize 2048     -validity 10000     -dname "CN=vayGram Dev, OU=Development, O=vayGram, C=XX" >/dev/null
else
  echo "[vayGram] reusing development signing key."
fi

cp "$DEV_KEYSTORE" "$KEYSTORE"

if [[ -n "${VAYGRAM_FIREBASE_JSON_BASE64:-}" ]]; then
  echo "[vayGram] installing Firebase configuration from environment..."
  printf '%s' "$VAYGRAM_FIREBASE_JSON_BASE64"     | base64 --decode     > "$WORKDIR/TMessagesProj/google-services.json"
fi

cat >> "$WORKDIR/local.properties" <<EOF

VAYGRAM_KEYSTORE_PASSWORD=$KEYSTORE_PASSWORD
VAYGRAM_KEY_ALIAS=$KEY_ALIAS
VAYGRAM_KEY_PASSWORD=$KEY_PASSWORD
EOF

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
bash "$ROOT/scripts/write-build-info.sh" "$OUTDIR/$APK_NAME" "$OUTDIR"

echo
echo "[vayGram] APK ready:"
echo "  $OUTDIR/$APK_NAME"
echo
echo "Package: app.vaygram.messenger.beta"
echo "Version: 0.1-dev"
