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
grep -q 'VayAppearance.chatMessageSpacingDp()' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Cells/ChatMessageCell.java"

grep -q '^APP_PACKAGE=app\.vaygram\.messenger
grep -q '^APP_VERSION_NAME=0\.1-dev
grep -q '<string name="AppName">vayGram</string>' \
  "$TMP/telegram/TMessagesProj/src/main/res/values/strings.xml"
grep -q 'android:accountType="app.vaygram.messenger"' \
  "$TMP/telegram/TMessagesProj/src/main/res/xml/auth.xml"
grep -q 'BuildConfig.VAYGRAM_API_ID' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java"
grep -q 'app.vaygram.messenger' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/ContactsController.java"

if grep -q 'public static int APP_ID = 4;' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java"; then
  echo "Official Telegram API ID leaked into vayGram build" >&2
  exit 1
fi

echo "[vayGram] upstream overlay smoke check passed"
 "$TMP/telegram/gradle.properties"
grep -q '^APP_VERSION_NAME=0.1-dev "$TMP/telegram/gradle.properties"
grep -q '<string name="AppName">vayGram</string>' \
  "$TMP/telegram/TMessagesProj/src/main/res/values/strings.xml"
grep -q 'android:accountType="app.vaygram.messenger"' \
  "$TMP/telegram/TMessagesProj/src/main/res/xml/auth.xml"
grep -q 'BuildConfig.VAYGRAM_API_ID' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java"
grep -q 'app.vaygram.messenger' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/ContactsController.java"

if grep -q 'public static int APP_ID = 4;' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java"; then
  echo "Official Telegram API ID leaked into vayGram build" >&2
  exit 1
fi

echo "[vayGram] upstream overlay smoke check passed"
 "$TMP/telegram/gradle.properties"
grep -q '<string name="AppName">vayGram</string>' \
  "$TMP/telegram/TMessagesProj/src/main/res/values/strings.xml"
grep -q 'android:accountType="app.vaygram.messenger"' \
  "$TMP/telegram/TMessagesProj/src/main/res/xml/auth.xml"
grep -q 'BuildConfig.VAYGRAM_API_ID' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java"
grep -q 'app.vaygram.messenger' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/ContactsController.java"

if grep -q 'public static int APP_ID = 4;' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java"; then
  echo "Official Telegram API ID leaked into vayGram build" >&2
  exit 1
fi

echo "[vayGram] upstream overlay smoke check passed"
 "$TMP/telegram/gradle.properties"
grep -q '^APP_VERSION_NAME=0.1-dev "$TMP/telegram/gradle.properties"
grep -q '<string name="AppName">vayGram</string>' \
  "$TMP/telegram/TMessagesProj/src/main/res/values/strings.xml"
grep -q 'android:accountType="app.vaygram.messenger"' \
  "$TMP/telegram/TMessagesProj/src/main/res/xml/auth.xml"
grep -q 'BuildConfig.VAYGRAM_API_ID' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java"
grep -q 'app.vaygram.messenger' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/ContactsController.java"

if grep -q 'public static int APP_ID = 4;' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java"; then
  echo "Official Telegram API ID leaked into vayGram build" >&2
  exit 1
fi

echo "[vayGram] upstream overlay smoke check passed"
