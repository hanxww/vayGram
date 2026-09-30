#!/usr/bin/env bash
set -euo pipefail

fail() {
  echo "[vayGram preflight] $*" >&2
  exit 2
}

need_command() {
  command -v "$1" >/dev/null 2>&1 || fail "missing required command: $1"
}

need_command git
need_command java
need_command keytool
need_command sha256sum

: "${VAYGRAM_API_ID:?Set VAYGRAM_API_ID to your Telegram API ID}"
: "${VAYGRAM_API_HASH:?Set VAYGRAM_API_HASH to your Telegram API hash}"

[[ "$VAYGRAM_API_ID" =~ ^[0-9]+$ ]] || fail "VAYGRAM_API_ID must be numeric"
[[ "$VAYGRAM_API_ID" != "0" ]] || fail "VAYGRAM_API_ID must be non-zero"
[[ "$VAYGRAM_API_HASH" =~ ^[0-9a-fA-F]{32}$ ]] || fail "VAYGRAM_API_HASH must be a 32-character hexadecimal string"

if [[ -n "${VAYGRAM_FIREBASE_JSON_BASE64:-}" ]]; then
  need_command base64
  printf '%s' "$VAYGRAM_FIREBASE_JSON_BASE64" \
    | tr -d '[:space:]' \
    | base64 --decode >/dev/null 2>&1 \
    || fail "VAYGRAM_FIREBASE_JSON_BASE64 is not valid Base64"
fi

if [[ -n "${VAYGRAM_SIGNING_KEYSTORE_BASE64:-}" ]]; then
  need_command base64
  : "${VAYGRAM_KEYSTORE_PASSWORD:?Set VAYGRAM_KEYSTORE_PASSWORD with persistent signing}"
  : "${VAYGRAM_KEY_ALIAS:?Set VAYGRAM_KEY_ALIAS with persistent signing}"
  : "${VAYGRAM_KEY_PASSWORD:?Set VAYGRAM_KEY_PASSWORD with persistent signing}"
  printf '%s' "$VAYGRAM_SIGNING_KEYSTORE_BASE64" \
    | tr -d '[:space:]' \
    | base64 --decode >/dev/null 2>&1 \
    || fail "VAYGRAM_SIGNING_KEYSTORE_BASE64 is not valid Base64"
fi

echo "[vayGram preflight] ok"
