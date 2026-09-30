#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WORKDIR="${VAYGRAM_COMPILE_WORKDIR:-$ROOT/.work/telegram-compile}"

# These values exist only so BuildConfig generation can compile.
# This script never produces or publishes a usable vayGram APK.
export VAYGRAM_API_ID="${VAYGRAM_API_ID:-123456}"
export VAYGRAM_API_HASH="${VAYGRAM_API_HASH:-00000000000000000000000000000000}"
export VAYGRAM_WITH_SUBMODULES=1

bash "$ROOT/scripts/bootstrap-telegram.sh" "$WORKDIR"

KEYSTORE="$WORKDIR/TMessagesProj/config/release.keystore"
if [[ ! -f "$KEYSTORE" ]]; then
  echo "[vayGram] generating disposable compile-check signing key..."
  keytool -genkeypair \
    -keystore "$KEYSTORE" \
    -storepass android \
    -alias androidkey \
    -keypass android \
    -keyalg RSA \
    -keysize 2048 \
    -validity 30 \
    -dname "CN=vayGram Compile Check, O=vayGram, C=XX" >/dev/null 2>&1
fi

echo "[vayGram] compiling Android overlay (no APK)..."
(
  cd "$WORKDIR"
  chmod +x ./gradlew
  ./gradlew \
    :TMessagesProj_App:compileAfatDebugJavaWithJavac \
    --no-daemon \
    --stacktrace
)

echo "[vayGram] Android overlay compile check passed."
