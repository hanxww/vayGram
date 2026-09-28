#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "\${BASH_SOURCE[0]}")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

"$ROOT/scripts/bootstrap-telegram.sh" "$TMP/telegram"

test -f "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java"
test -f "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/settings/VaySettingsEngine.java"
test -f "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/android/VayAndroid.java"

grep -q 'presentSettingFragment(new app.vaygram.ui.VaySettingsActivity())'   "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/SettingsActivity.java"

echo "[vayGram] upstream overlay smoke check passed"
