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

FIREBASE_STATUS="not_configured"
if [[ -n "${VAYGRAM_FIREBASE_JSON_BASE64:-}" ]]; then
  echo "[vayGram] checking Firebase configuration from environment..."
  FIREBASE_TMP="$WORKDIR/TMessagesProj/google-services.json.vaygram-tmp"
  printf '%s' "$VAYGRAM_FIREBASE_JSON_BASE64" \
    | tr -d '[:space:]' \
    | base64 --decode \
    > "$FIREBASE_TMP"

  if python3 - "$FIREBASE_TMP" <<'PY'
import json
from pathlib import Path
import sys

target = "app.vaygram.messenger.beta"
try:
    data = json.loads(Path(sys.argv[1]).read_text(encoding="utf-8"))
except Exception:
    raise SystemExit(1)

packages = {
    client.get("client_info", {})
          .get("android_client_info", {})
          .get("package_name")
    for client in data.get("client", [])
}
raise SystemExit(0 if target in packages else 1)
PY
  then
    cp "$FIREBASE_TMP" "$WORKDIR/TMessagesProj/google-services.json"
    cp "$FIREBASE_TMP" "$WORKDIR/TMessagesProj_App/google-services.json"
    rm -f "$FIREBASE_TMP"
    FIREBASE_STATUS="configured"
    echo "[vayGram] Firebase configuration matches app.vaygram.messenger.beta and is installed for the app module."
  else
    rm -f \
      "$FIREBASE_TMP" \
      "$WORKDIR/TMessagesProj/google-services.json" \
      "$WORKDIR/TMessagesProj_App/google-services.json"
    FIREBASE_STATUS="skipped_package_mismatch"
    echo "::warning::Firebase configuration does not contain app.vaygram.messenger.beta; building without Firebase/FCM."
  fi
fi
export VAYGRAM_FIREBASE_STATUS="$FIREBASE_STATUS"

python3 - "$WORKDIR/gradle.properties" "$KEYSTORE_PASSWORD" "$KEY_ALIAS" "$KEY_PASSWORD" <<'PY'
from pathlib import Path
import re
import sys

path = Path(sys.argv[1])
store_password = sys.argv[2]
key_alias = sys.argv[3]
key_password = sys.argv[4]

text = path.read_text(encoding="utf-8")
values = {
    "RELEASE_STORE_PASSWORD": store_password,
    "RELEASE_KEY_ALIAS": key_alias,
    "RELEASE_KEY_PASSWORD": key_password,
}
for key, value in values.items():
    pattern = rf"(?m)^{re.escape(key)}=.*$"
    replacement = f"{key}={value}"
    if re.search(pattern, text):
        text = re.sub(pattern, replacement, text)
    else:
        text += "\n" + replacement
path.write_text(text, encoding="utf-8")
PY

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
