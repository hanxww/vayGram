#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
APK="${1:?usage: write-build-info.sh <apk> [output-dir]}"
OUTDIR="${2:-$(dirname "$APK")}"

[[ -f "$APK" ]] || {
  echo "APK not found: $APK" >&2
  exit 1
}

UPSTREAM_FILE="$ROOT/telegram-integration/UPSTREAM"
# shellcheck disable=SC1090
source "$UPSTREAM_FILE"

mkdir -p "$OUTDIR"

SHA256="$(sha256sum "$APK" | awk '{print $1}')"
SIZE_BYTES="$(wc -c < "$APK" | tr -d ' ')"
BUILT_AT="$(date -u +"%Y-%m-%dT%H:%M:%SZ")"

cat > "$OUTDIR/vayGram-0.1-dev-build-info.txt" <<EOF
name=vayGram
version=0.1-dev
package=app.vaygram.messenger.beta
apk=$(basename "$APK")
sha256=$SHA256
size_bytes=$SIZE_BYTES
built_at_utc=$BUILT_AT
vaygram_commit=${GITHUB_SHA:-local}
telegram_repo=$repo
telegram_commit=$commit
EOF

printf '%s  %s\n' "$SHA256" "$(basename "$APK")" > "$OUTDIR/vayGram-0.1-dev.sha256"

echo "[vayGram] build metadata written to $OUTDIR"
