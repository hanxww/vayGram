#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

bash "$ROOT/scripts/bootstrap-telegram.sh" "$TMP/telegram"

test -f "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java"
test -f "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/settings/VaySettingsEngine.java"
test -f "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/android/VayAndroid.java"

grep -q 'presentSettingFragment(new app.vaygram.ui.VaySettingsActivity())' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/SettingsActivity.java"

grep -q 'VayAppearance.dialogRowHeightDp()' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Cells/DialogCell.java"
grep -q 'VayAppearance.dialogAvatarSizeDp' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Cells/DialogCell.java"
grep -q 'VayAppearance.dialogMessagePaddingStartDp' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Cells/DialogCell.java"
grep -q 'VayDefaults.CHAT_BUBBLE_RADIUS' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ThemeActivity.java"

echo "[vayGram] upstream overlay smoke check passed"
