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
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/android/settings/VayPresetRepository.java" "persistent preset repository"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/theme/VayThemeBridge.java" "Telegram theme token bridge"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayPaletteActivity.java" "palette editor UI"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/android/theme/VayThemePaletteRepository.java" "palette persistence repository"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/android/theme/VayMaterialYouPalette.java" "Android Material You palette resolver"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/theme/VayThemeTokenRegistry.java" "semantic theme token registry"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/theme/VayThemePalette.java" "theme palette model"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/theme/VayGradientSpec.java" "gradient specification model"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/android/theme/VayGradientRepository.java" "persistent gradient repository"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/theme/VayGradientBridge.java" "Android gradient renderer bridge"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayGradientActivity.java" "gradient editor UI"

must_grep 'presentSettingFragment\(new app\.vaygram\.ui\.VaySettingsActivity\(\)\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/SettingsActivity.java" \
  "Settings entry hook"

must_grep 'ACTION_SAVE_PROFILE' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "save profile settings action"
must_grep 'TYPE_PREVIEW' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "embedded visual preview row"
must_grep 'VaySettingsListener previewListener' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "live preview listener"
must_grep 'VayThemeBridge\.color' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "semantic token preview consumer"
must_grep 'class VayPaletteActivity' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayPaletteActivity.java" \
  "palette editor activity"
must_grep 'previewThemeColor' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayPaletteActivity.java" \
  "live palette preview"
must_grep 'applyColorOverride' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/theme/VayThemeBridge.java" \
  "palette override color bridge"
must_grep 'VayThemeTokens\.CHAT_BUBBLE_OUT' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/theme/VayThemeBridge.java" \
  "chat token Telegram mapping"
must_grep 'applyColorOverride' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ActionBar/Theme.java" \
  "palette override Theme pipeline"
must_grep 'VayTelegram\.commitThemeColor' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayPaletteActivity.java" \
  "palette editor persistence hook"
must_grep 'ACTION_PALETTE_EDITOR' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "palette editor settings entry"
must_grep 'ACTION_GRADIENT_EDITOR' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "gradient editor settings entry"
must_grep 'previewNavigationGradient' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayGradientActivity.java" \
  "live gradient editor preview"
must_grep 'VayGradientBridge\.apply' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/MainTabsActivity.java" \
  "bottom navigation gradient renderer"
must_grep 'vayNavigationGradientDrawable' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/MainTabsActivity.java" \
  "bottom navigation gradient drawable"
must_grep 'THEME_MATERIAL_YOU' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/settings/VayDefaults.java" \
  "Material You settings registration"
must_grep 'refreshMaterialYou' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/theme/VayThemeBridge.java" \
  "Material You theme bridge"
must_grep 'VayMaterialYouPalette\.resolve' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayTelegram.java" \
  "Material You Android palette sync"
must_grep 'ACTION_REFRESH_MATERIAL_YOU' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayPaletteActivity.java" \
  "Material You palette editor status"
must_grep 'VayTelegram\.presets\(\)\.save' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "persistent profile save UI"

must_grep 'VayAppearance\.dialogRowHeightDp\(currentAccount, currentDialogId\)' \
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
must_grep 'GLASS_OPACITY' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/settings/VayDefaults.java" \
  "glass opacity setting"
must_grep 'glassBlurEnabled\(currentAccount\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/MainTabsActivity.java" \
  "bottom navigation glass blur toggle"
must_grep 'iBlur3SourceTabGlass\.setBlur' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/MainTabsActivity.java" \
  "bottom navigation blur radius hook"
must_grep 'tabsViewBackground\.setAlpha' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/MainTabsActivity.java" \
  "bottom navigation glass opacity hook"

must_grep 'vayApplyAmoledSurface' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ActionBar/Theme.java" \
  "AMOLED surface color hook"
must_grep 'key_chat_messagePanelBackground' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ActionBar/Theme.java" \
  "AMOLED chat composer surface"
must_grep 'Theme\.refreshThemeColors\(\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayTelegram.java" \
  "live AMOLED theme refresh"
must_grep 'setCompactModeEnabled' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayAppearance.java" \
  "compact mode derived metrics"
must_grep 'VayDefaults\.COMPACT_MODE' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayTelegram.java" \
  "compact mode Telegram bridge"

must_grep 'getResolved' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/settings/VaySettingsEngine.java" \
  "scope inheritance engine"
must_grep 'forAccount\(int account\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "account override settings UI"
must_grep 'forChat\(int account, long dialogId\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "chat override settings UI"
must_grep 'vay_chat_settings' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ChatActivity.java" \
  "chat menu settings entry"
must_grep 'chatMessageSpacingDp\(currentMessageObject\.currentAccount, currentMessageObject\.getDialogId\(\)\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Cells/ChatMessageCell.java" \
  "per-chat message spacing hook"

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

if [[ -f "$TMP/telegram/TMessagesProj/google-services.json" ]]; then
  echo "[vayGram] upstream Telegram Firebase configuration leaked into vayGram build" >&2
  exit 1
fi

echo "[vayGram] upstream overlay smoke check passed"
