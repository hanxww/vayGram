#!/usr/bin/env bash
set -euo pipefail

WORKDIR="${1:?usage: apply-vaygram-branding.sh <telegram-worktree>}"
PACKAGE_ID="app.vaygram.messenger"
APP_NAME="vayGram"
VERSION_NAME="0.1-dev"

python3 - "$WORKDIR" "$PACKAGE_ID" "$APP_NAME" "$VERSION_NAME" <<'PY'
from pathlib import Path
import re
import sys

root = Path(sys.argv[1])
package_id = sys.argv[2]
app_name = sys.argv[3]
version_name = sys.argv[4]

gradle_props = root / "gradle.properties"
text = gradle_props.read_text(encoding="utf-8")
text = re.sub(r"(?m)^APP_PACKAGE=.*$", f"APP_PACKAGE={package_id}", text)
text = re.sub(r"(?m)^APP_VERSION_NAME=.*$", f"APP_VERSION_NAME={version_name}", text)
gradle_props.write_text(text, encoding="utf-8")

res_root = root / "TMessagesProj" / "src" / "main" / "res"
for strings in res_root.glob("values*/strings.xml"):
    text = strings.read_text(encoding="utf-8")
    text = re.sub(
        r'(<string\s+name="AppName"[^>]*>).*?(</string>)',
        rf'\1{app_name}\2',
        text,
        count=1,
    )
    text = re.sub(
        r'(<string\s+name="AppNameBeta"[^>]*>).*?(</string>)',
        rf'\1{app_name} Dev\2',
        text,
        count=1,
    )
    strings.write_text(text, encoding="utf-8")

replacements = {
    res_root / "xml" / "auth.xml": {
        'android:accountType="org.telegram.messenger"':
            f'android:accountType="{package_id}"',
    },
    res_root / "xml" / "sync_contacts.xml": {
        'android:accountType="org.telegram.messenger"':
            f'android:accountType="{package_id}"',
    },
    res_root / "xml" / "auth_menu.xml": {
        'android:action="org.telegram.messenger.OPEN_ACCOUNT"':
            f'android:action="{package_id}.OPEN_ACCOUNT"',
        'android:targetPackage="org.telegram.messenger"':
            f'android:targetPackage="{package_id}.beta"',
    },
    res_root / "xml" / "shortcuts.xml": {
        'android:name="org.telegram.messenger.SHORTCUT_SHARE"':
            f'android:name="{package_id}.SHORTCUT_SHARE"',
    },
    res_root / "xml" / "contacts.xml": {
        "vnd.org.telegram.messenger.android.profile":
            "vnd.app.vaygram.messenger.android.profile",
        "vnd.org.telegram.messenger.android.call.video":
            "vnd.app.vaygram.messenger.android.call.video",
        "vnd.org.telegram.messenger.android.call":
            "vnd.app.vaygram.messenger.android.call",
    },
    root / "TMessagesProj" / "src" / "main" / "AndroidManifest.xml": {
        "vnd.org.telegram.messenger.android.profile":
            "vnd.app.vaygram.messenger.android.profile",
        "vnd.org.telegram.messenger.android.call.video":
            "vnd.app.vaygram.messenger.android.call.video",
        "vnd.org.telegram.messenger.android.call":
            "vnd.app.vaygram.messenger.android.call",
    },
}

for path, mapping in replacements.items():
    text = path.read_text(encoding="utf-8")
    for old, new in mapping.items():
        text = text.replace(old, new)
    path.write_text(text, encoding="utf-8")

contacts_controller = root / "TMessagesProj" / "src" / "main" / "java" / "org" / "telegram" / "messenger" / "ContactsController.java"
text = contacts_controller.read_text(encoding="utf-8")
text = text.replace(
    'new Account("" + getUserConfig().getClientUserId(), "org.telegram.messenger")',
    f'new Account("" + getUserConfig().getClientUserId(), "{package_id}")',
)
text = text.replace(
    'am.getAccountsByType("org.telegram.messenger")',
    f'am.getAccountsByType("{package_id}")',
)
text = text.replace(
    '"vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile"',
    '"vnd.android.cursor.item/vnd.app.vaygram.messenger.android.profile"',
)
text = text.replace(
    '"vnd.android.cursor.item/vnd.org.telegram.messenger.android.call.video"',
    '"vnd.android.cursor.item/vnd.app.vaygram.messenger.android.call.video"',
)
text = text.replace(
    '"vnd.android.cursor.item/vnd.org.telegram.messenger.android.call"',
    '"vnd.android.cursor.item/vnd.app.vaygram.messenger.android.call"',
)
text = text.replace('"Telegram Profile"', '"vayGram Profile"')
text = text.replace('"Telegram Voice Call"', '"vayGram Voice Call"')
text = text.replace('"Telegram Video Call"', '"vayGram Video Call"')
contacts_controller.write_text(text, encoding="utf-8")

print(f"[vayGram] branded checkout as {app_name} ({package_id}, {version_name})")
PY

GOOGLE_SERVICES_TARGETS=(
  "$WORKDIR/TMessagesProj/google-services.json"
  "$WORKDIR/TMessagesProj_App/google-services.json"
  "$WORKDIR/TMessagesProj_AppHuawei/google-services.json"
  "$WORKDIR/TMessagesProj_AppHockeyApp/google-services.json"
  "$WORKDIR/TMessagesProj_AppStandalone/google-services.json"
)
rm -f "${GOOGLE_SERVICES_TARGETS[@]}"

if [[ -n "${VAYGRAM_GOOGLE_SERVICES_JSON:-}" ]]; then
  if [[ ! -f "$VAYGRAM_GOOGLE_SERVICES_JSON" ]]; then
    echo "VAYGRAM_GOOGLE_SERVICES_JSON does not exist: $VAYGRAM_GOOGLE_SERVICES_JSON" >&2
    exit 2
  fi
  cp "$VAYGRAM_GOOGLE_SERVICES_JSON" "$WORKDIR/TMessagesProj_App/google-services.json"
  echo "[vayGram] using vayGram Firebase configuration for the app module."
else
  echo "[vayGram] removed upstream Telegram Firebase configurations; Firebase disabled."
fi
