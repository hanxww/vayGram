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

must_not_grep() {
  local pattern="$1"
  local path="$2"
  local label="$3"
  if grep -qE "$pattern" "$path"; then
    echo "[vayGram] forbidden pattern: $label" >&2
    echo "[vayGram] pattern: $pattern" >&2
    echo "[vayGram] file: $path" >&2
    exit 1
  fi
  echo "[vayGram] ok: $label"
}

must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" "Vay Settings UI"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayDiagnosticsActivity.java" "vayGram diagnostics and recovery UI"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayBackupActivity.java" "portable backup and restore UI"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayAboutActivity.java" "vayGram About UI"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayBackupCodec.java" "privacy-safe backup codec"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/settings/VaySettingsEngine.java" "VayCore settings engine"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/android/VayAndroid.java" "Android settings bridge"
must_grep 'RUNTIME_PREFS_NAME' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/android/VayAndroid.java" \
  "separate Safe Mode persistence"
must_grep 'setSafeMode\(boolean enabled\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayTelegram.java" \
  "Safe Mode Telegram bridge"
must_grep 'safeModeEnabled' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/theme/VayThemeBridge.java" \
  "Safe Mode theme bypass"
must_grep 'setSafeModeEnabled' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayAppearance.java" \
  "Safe Mode appearance bypass"
must_grep 'ACTION_SAFE_MODE' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayDiagnosticsActivity.java" \
  "Safe Mode recovery action"
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
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayProfileStudioActivity.java" "Vay Profile Studio UI"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayProfileAvatarFrame.java" "live Vay Profile avatar renderer"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayProfileAppearance.java" "Profile Studio Telegram bridge"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayOnboardingActivity.java" "first-run vayGram onboarding UI"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayFirstLaunchCoach.java" "contextual first-launch coach"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayCoachOverlay.java" "guided spotlight overlay"
must_file "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayOnboardingState.java" "onboarding persistence state"

must_grep 'presentSettingFragment\(new app\.vaygram\.ui\.VaySettingsActivity\(\)\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/SettingsActivity.java" \
  "Settings entry hook"
must_grep 'R\.string\.vay_settings_entry_hint' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/SettingsActivity.java" \
  "top-level standalone vayGram settings block"
must_grep 'vay_profile_studio' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ProfileActivity.java" \
  "Profile Studio profile hook"
must_grep 'VayProfileStudioActivity' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ProfileActivity.java" \
  "Profile Studio navigation"
must_grep 'VayProfileAvatarFrame' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ProfileActivity.java" \
  "Profile Studio live avatar hook"
must_grep 'updateAvatarRoundRadius\(\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ProfileActivity.java" \
  "Profile Studio live avatar refresh hook"
must_not_grep 'avatarImage\.drawAvatar = app\.vaygram' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ProfileActivity.java" \
  "Profile Studio must not blank Telegram avatar geometry"
must_not_grep 'VayProfileAppearance\.showTelegramPhone' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ProfileActivity.java" \
  "Profile Studio block defaults must not suppress native Telegram rows"
must_grep 'VayFirstLaunchCoach\.maybePresent' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/DialogsActivity.java" \
  "first-launch contextual coach hook"
must_grep 'VayOnboardingActivity\.maybePresent' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayFirstLaunchCoach.java" \
  "contextual coach handoff to onboarding"
must_grep 'new ScrollView\(context\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayOnboardingActivity.java" \
  "scroll-safe onboarding content"
must_grep 'Gravity\.BOTTOM' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayOnboardingActivity.java" \
  "always-reachable onboarding navigation"
must_grep 'setPreferredLevel\(selectedLevel\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayOnboardingActivity.java" \
  "onboarding mode selection persistence"
must_grep 'dispatchTouchEvent' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayCoachOverlay.java" \
  "spotlight target remains interactive"
must_grep 'ACTION_REPLAY_ONBOARDING' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "replay onboarding action"
must_grep 'ACTION_DIAGNOSTICS' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "diagnostics and recovery settings entry"
must_grep 'No account ids, phone numbers, chats, messages, tokens, or credentials' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayDiagnosticsActivity.java" \
  "sanitized diagnostics report boundary"
must_grep 'resetThemePalette' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayDiagnosticsActivity.java" \
  "theme recovery action"
must_grep 'telegram_data_included.*false' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayBackupCodec.java" \
  "backup Telegram-data privacy boundary"
must_grep 'chat_overrides_included.*false' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayBackupCodec.java" \
  "backup chat-id privacy boundary"
must_grep 'capturePreset' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayBackupCodec.java" \
  "portable settings backup capture"
must_grep 'Intent\.ACTION_CREATE_DOCUMENT' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayBackupActivity.java" \
  "SAF backup file export"
must_grep 'Intent\.ACTION_OPEN_DOCUMENT' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayBackupActivity.java" \
  "SAF backup file import"
must_grep 'MAX_BACKUP_BYTES' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayBackupActivity.java" \
  "bounded backup file import"
must_grep 'replaceAll' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/android/settings/VayPresetRepository.java" \
  "saved profile restore support"
must_grep 'ACTION_BACKUP' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "backup settings entry"
must_grep 'ACTION_ABOUT' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "About settings entry"
must_grep 'SOURCE_REPOSITORY' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayBuild.java" \
  "source repository About metadata"
must_grep 'TELEGRAM_BASE_VERSION = "12\.10\.5"' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayBuild.java" \
  "pinned Telegram diagnostic version"
must_grep 'TELEGRAM_BASE_VERSION_CODE = 7105' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayBuild.java" \
  "pinned Telegram diagnostic version code"
must_grep 'TELEGRAM_BASE_COMMIT = "dc780e81ed1261c369c27870e8e0999a1eb0b600"' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayBuild.java" \
  "pinned Telegram diagnostic commit"
must_grep 'VayCoachOverlay\.show' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VaySettingsActivity.java" \
  "guided spotlight tour"
must_grep 'PROFILE_LAYOUT_MODE' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/settings/VayDefaults.java" \
  "Profile Studio setting registry"
must_grep 'PROFILE_SHOW_AVATAR' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/settings/VayDefaults.java" \
  "Profile Studio avatar block"
must_grep 'PROFILE_SHOW_BUTTONS' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/settings/VayDefaults.java" \
  "Profile Studio buttons block"
must_grep 'profile\.block\.phone' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/core/settings/VayDefaults.java" \
  "Profile Studio privacy-aware phone block"
must_grep 'drawPreviewBlocks' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayProfileStudioActivity.java" \
  "Profile Studio multi-block live preview"
must_grep 'ACTION_COMPATIBILITY' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/ui/VayProfileStudioActivity.java" \
  "Profile Studio safe live-rendering explanation"
must_grep 'telegramVisibleUnlessExplicitlyHidden' \
  "$TMP/telegram/TMessagesProj/src/main/java/app/vaygram/telegram/VayProfileAppearance.java" \
  "Profile Studio future native-row compatibility guard"
must_grep 'vay_profile_show_mutual_chats' \
  "$TMP/telegram/TMessagesProj/src/main/res/values-ru/strings.xml" \
  "Profile Studio block localization merged into Telegram strings"
must_grep 'vay_settings_title' \
  "$TMP/telegram/TMessagesProj/src/main/res/values-ru/strings.xml" \
  "vayGram settings localization merged into Telegram strings"
must_grep 'vay_diagnostics_title' \
  "$TMP/telegram/TMessagesProj/src/main/res/values-ru/strings.xml" \
  "diagnostics localization merged into Telegram strings"

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
must_grep 'VayAppearance\.dialogNameTextSizeDp\(currentAccount\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Cells/DialogCell.java" \
  "dialog title text size hook"
must_grep 'VayAppearance\.dialogMessageTextSizeDp\(currentAccount\)' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/Cells/DialogCell.java" \
  "dialog preview text size hook"
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
must_grep 'contentView\.requestApplyInsets' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/MainTabsActivity.java" \
  "dynamic bottom navigation inset redispatch"
must_grep 'vayRefreshMainTabsInsets' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ProfileActivity.java" \
  "profile main-tab inset refresh"
must_grep 'MAIN_TABS_HEIGHT_WITH_MARGINS' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/SettingsActivity.java" \
  "settings dynamic main-tab inset"
must_grep 'MAIN_TABS_HEIGHT_WITH_MARGINS' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/ContactsActivity.java" \
  "contacts dynamic main-tab inset"
must_grep 'MAIN_TABS_HEIGHT_WITH_MARGINS' \
  "$TMP/telegram/TMessagesProj/src/main/java/org/telegram/ui/CallLogActivity.java" \
  "calls dynamic main-tab inset"

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

for firebase_file in \
  "$TMP/telegram/TMessagesProj/google-services.json" \
  "$TMP/telegram/TMessagesProj_App/google-services.json" \
  "$TMP/telegram/TMessagesProj_AppHuawei/google-services.json" \
  "$TMP/telegram/TMessagesProj_AppHockeyApp/google-services.json" \
  "$TMP/telegram/TMessagesProj_AppStandalone/google-services.json"; do
  if [[ -f "$firebase_file" ]]; then
    echo "[vayGram] upstream Telegram Firebase configuration leaked into vayGram build: $firebase_file" >&2
    exit 1
  fi
done

must_file "$TMP/telegram/TMessagesProj/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" "vayGram adaptive launcher icon"
must_file "$TMP/telegram/TMessagesProj/src/main/res/drawable/vaygram_icon_foreground.xml" "vayGram launcher foreground"
must_file "$TMP/telegram/TMessagesProj/src/main/res/drawable/vaygram_icon_monochrome.xml" "vayGram monochrome launcher icon"
must_grep 'vaygram_icon_foreground' \
  "$TMP/telegram/TMessagesProj/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" \
  "vayGram adaptive icon foreground"
must_grep 'vaygram_icon_monochrome' \
  "$TMP/telegram/TMessagesProj/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" \
  "vayGram themed monochrome icon"
must_grep '#7C5CFC' \
  "$TMP/telegram/TMessagesProj/src/main/res/drawable/vaygram_icon_foreground.xml" \
  "vayGram icon accent"

echo "[vayGram] upstream overlay smoke check passed"
