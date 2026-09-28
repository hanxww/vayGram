#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

bash "$ROOT/scripts/bootstrap-telegram.sh" "$TMP/telegram"

must_file() {
  local path="$1"
  local label="$2"
  if [[ ! -f "$path" ]]; then
    echo "[vayGram] missing: $label ($path)" >&2
    exit 1
  fi
  echo "[vayGram] ok: $label"
}

must_grep() {
  local pattern="$1"
  local path="$2"
  local label="$3"
  if ! grep -qE "$pattern" "$path"; then
    echo "[vayGram] check failed: $label" >&2
    echo "[vayGram] pattern: $pattern" >&2
    echo "[vayGram] file: $path" >&2
    exit 1
  fi
  echo "[vayGram] ok: $label"
}

must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" "Vay Settings UI"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/settings/VaySettingsEngine.java" "VayCore settings engine"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/android/VayAndroid.java" "Android settings bridge"

must_grep 'presentSettingFragment\(new app\.vaygram\.ui\.VaySettingsActivity\(\)\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/SettingsActivity.java" \
  "Settings entry hook"

must_grep 'VayAppearance\.dialogRowHeightDp\(\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Cells/DialogCell.java" \
  "dialog row height hook"
must_grep 'VayAppearance\.dialogAvatarSizeDp' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Cells/DialogCell.java" \
  "dialog avatar size hook"
must_grep 'VayAppearance\.dialogMessagePaddingStartDp' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Cells/DialogCell.java" \
  "dialog message padding hook"
must_grep 'VayDefaults\.CHAT_BUBBLE_RADIUS' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ThemeActivity.java" \
  "bubble radius hook"
must_grep 'VayAppearance\.chatMessageSpacingDp\(\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Cells/ChatMessageCell.java" \
  "message spacing hook"

must_grep 'vaySetMainTabsHeight\(int height\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/DialogsActivity.java" \
  "dynamic bottom bar height hook"
must_grep 'setVayMainTabLabelVisible' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Components/glass/GlassTabView.java" \
  "bottom bar label visibility hook"
must_grep 'setVayMainTabMotionScale' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Components/glass/GlassTabView.java" \
  "bottom bar motion scale hook"
must_grep 'applyVayNavigationAppearance\(\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/MainTabsActivity.java" \
  "live bottom navigation refresh"

must_grep '^APP_PACKAGE=app\.vaygram\.messenger$' \
  "$TMP/telegram/gradle.properties" \
  "vayGram application id"
must_grep '^APP_VERSION_NAME=0\.1-dev$' \
  "$TMP/telegram/gradle.properties" \
  "vayGram dev version"
must_grep '<string name="AppName">vayGram</string>' \
  "$TMP/telegram/TMessagesProj/src/main/res/values/strings.xml" \
  "vayGram app label"
must_grep 'android:accountType="app\.vaygram\.messenger"' \
  "$TMP/telegram/TMessagesProj/src/main/res/xml/auth.xml" \
  "vayGram Android account type"
must_grep 'BuildConfig\.VAYGRAM_API_ID' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java" \
  "external Telegram API id"
must_grep 'app\.vaygram\.messenger' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/ContactsController.java" \
  "vayGram contacts identity"

if grep -q 'public static int APP_ID = 4;' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java"; then
  echo "[vayGram] official Telegram API ID leaked into vayGram build" >&2
  exit 1
fi

echo "[vayGram] upstream overlay smoke check passed"
