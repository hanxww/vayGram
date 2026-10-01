# vayGram real-device smoke test

Use this checklist for a real Android phone or tablet. Do not paste API credentials, signing passwords, Firebase files, tokens, or other secrets into issues, logs, or chat.

## Build under test

Record before testing:

- vayGram commit SHA
- GitHub Actions run ID
- artifact name
- package id
- app version
- APK SHA-256
- Android device model
- Android version

Expected dev identity:

```text
package: app.vaygram.messenger.beta
version: 0.1-dev
```

## 1. Clean install

1. Download the APK artifact from the successful `dev-apk` run.
2. Verify the published SHA-256 before installation.
3. Remove an older vayGram dev install only for the clean-install pass.
4. Install the APK.
5. Launch it from the launcher.
6. Confirm there is no immediate crash, blank activity, or restart loop.

Pass when the launcher icon, label, package, version, and first activity are correct.

## 2. First-run onboarding

On a fresh app-data state:

- confirm the chat list first shows a short contextual coach over the real Telegram UI;
- confirm everything except the highlighted control is dimmed;
- confirm the highlighted control stays sharp, bright, and directly tappable;
- tap the highlighted control and confirm its real Telegram action runs while the coach disappears cleanly;
- repeat with Next instead of tapping the target and confirm the next highlighted control is positioned correctly;
- confirm Skip ends the onboarding without showing another tutorial immediately;
- complete Telegram intro/login entry until the main chat list appears;
- confirm the vayGram tutorial starts from the main app experience rather than interrupting login;
- confirm the spotlight dims the rest of the UI while the target stays sharp;
- confirm the target remains clickable;
- confirm Next / Skip / Done work;
- confirm the tour survives required scrolling and different screen positions;
- confirm the selected Basic / Advanced / Insane level persists;
- complete the tutorial, restart the app, and confirm it does not auto-run again;
- replay it manually from Vay Settings.

## 3. Telegram core smoke

Use a test account where possible.

- login
- logout and login again
- chat list opens
- open a private chat
- open a group/channel
- send a text message
- receive a text message
- reply
- forward a normal message
- send an image
- send a short video
- download received media
- upload media
- play voice/audio where available
- verify links and attachments open normally

Do not test or implement bypasses for self-destruct or protected-content restrictions.

## 4. Firebase / notifications

With vayGram in the background:

- receive a Telegram message;
- confirm a push notification arrives;
- tap it and confirm the correct chat opens;
- repeat after force-stopping/reopening the app if appropriate for the Android version;
- confirm no notification uses Telegram's upstream package identity.

## 5. Vay Settings

Verify:

- Vay Settings entry opens;
- search works;
- Basic / Advanced / Insane filtering works;
- Global / Account / Chat scopes open;
- inherited values are visually distinct from explicit overrides;
- per-setting reset works;
- reset-all works;
- Undo / Redo works;
- Recently Changed updates;
- import/export works with non-secret settings data;
- saved profiles can be created/applied/deleted.

## 6. Visible customization

Change each item and verify the real Telegram surface updates:

- chat bubble radius
- message spacing
- chat list row height
- avatar size
- avatar roundness
- bottom navigation height
- bottom navigation labels
- animation scale
- AMOLED mode
- compact mode

Restart the app and verify committed values persist.

## 7. Theme Engine

Verify:

- semantic palette override
- per-token reset
- Material You enable/disable on Android 12+
- AMOLED surface enforcement in a dark theme
- bottom-navigation gradient
- gradient direction
- glass opacity
- blur on supported Android versions
- live preview cancel restores the previous value
- live preview apply persists the new value

## 8. Vay Profile Studio

On the account owner's own profile:

- before changing anything, confirm the native Telegram avatar/name/status header has no blank reserved area, clipping, or shifted text;
- confirm native Telegram rows such as phone, bio, username, birthday, personal channel, music and media are not hidden merely because a Vay block defaults to off;
- change avatar roundness and confirm the real Telegram profile avatar updates without breaking expand/collapse;
- enable avatar glow/status ring and confirm they render without covering or suppressing the avatar;
- disable Vay preview blocks and confirm the current build keeps Telegram's structural profile layout intact;
- open the Telegram compatibility row and confirm the safe-rendering explanation matches current behavior;
- confirm the Vay Profile Studio entry is visible and opens;
- confirm profile-specific controls are kept inside Profile Studio rather than duplicated in ordinary Vay Settings;
- change available foundation controls;
- leave and reopen the profile;
- restart the app and verify persisted state;
- confirm ordinary Telegram profile actions still work.

## 9. Diagnostics and recovery

- open Diagnostics and recovery from Vay Settings;
- confirm vayGram version, Telegram base, package and Android SDK are shown;
- confirm current scope, customization level and non-sensitive counts are correct;
- copy the diagnostics report and confirm it contains no phone number, Telegram user/account id, chat/message content, token, API hash, signing data or credential;
- change a disposable vayGram setting in the current scope, use Reset current scope, and confirm Telegram account/session data remains intact;
- create a disposable palette override/gradient, use Reset theme layer, and confirm only vayGram theme-layer customizations are cleared.

## 10. Main-tab inset regression

With bottom navigation visible:

- set bottom-navigation height to the minimum, default and maximum values without leaving the current screen;
- on the own-profile tab, open Gifts and Media and verify the grid, action button and bottom content are not trapped behind the navigation pill;
- on Settings, verify the last row can scroll fully above the navigation pill after each height change;
- on Contacts, verify the list, empty state and floating action button keep the correct bottom clearance;
- on Calls, verify the list, empty state and floating action button keep the correct bottom clearance;
- switch between gesture navigation and three-button navigation when available, then repeat the profile/settings checks;
- enable/disable Safe Mode and confirm bottom-navigation insets immediately return to default/custom values without reopening the tab.

## 11. Safe Mode recovery

- create several visible vayGram customizations: palette override, gradient, non-default bottom-bar height, chat/list sizing and Profile Studio avatar effect;
- open **Diagnostics and recovery** and enable **Safe Mode**;
- confirm Telegram remains logged in and no settings are deleted;
- confirm theme overrides, gradients/glass, custom layout metrics and Profile Studio avatar effects are bypassed;
- restart vayGram and confirm Safe Mode remains enabled;
- disable Safe Mode and confirm the previously saved vayGram customizations become active again;
- copy diagnostics and confirm it reports the Safe Mode state without exposing Telegram account/chat data.

## 12. Backup / restore and About

- open **Backup & restore** from Vay Settings;
- copy a portable backup and verify it contains vayGram settings/theme data but no Telegram session, chat, phone-number or account-id data;
- change several global/account settings, palette colors and the navigation gradient, then restore the copied backup;
- confirm global settings return to their backed-up values;
- confirm account settings are mapped to the currently selected Telegram account;
- confirm saved vayGram profiles are restored;
- confirm Telegram login/session/chat data remains untouched;
- share the backup through Android's share sheet and confirm no crash;
- save the backup as a `.json` file through Android's system file picker;
- change several vayGram settings, then restore from that file and confirm the confirmation dialog appears before applying;
- select an invalid/non-vayGram JSON file and confirm it is rejected without changing settings;
- cancel both file pickers and confirm the screen stays usable;
- open **About vayGram**;
- confirm vayGram version, pinned Telegram base/version code and base commit are correct;
- confirm source-repository and development-channel links open;
- confirm build information copies successfully.

## 13. Upgrade compatibility

After the clean-install pass, keep the tested build installed.

For the next CI build signed by the persistent vayGram dev key:

1. install it over the existing build without uninstalling;
2. confirm Android accepts the upgrade;
3. confirm Telegram account/session data remains intact;
4. confirm vayGram settings/profiles remain intact;
5. confirm onboarding completion state remains intact;
6. repeat a short chat/send/receive/Vay Settings smoke.

## 14. Crash capture

If anything crashes, record:

- exact action immediately before the crash;
- device and Android version;
- vayGram commit and Actions run ID;
- whether it reproduces after relaunch;
- a minimal relevant logcat excerpt with personal message content, phone numbers, tokens, and account identifiers removed.

A real-device smoke pass does not make the build release-ready by itself. All remaining items in `docs/RELEASE_CHECKLIST.md` still apply.
