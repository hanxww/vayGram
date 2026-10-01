# Known issues — vayGram 0.1-dev

These are intentionally tracked instead of being hidden from testers.

## Profile Studio

- The 20 Profile Studio blocks are fully represented in the settings model and preview, but most Vay-only blocks do not yet have a dedicated native Telegram profile renderer.
- To protect the normal Telegram profile layout, the current live integration is limited to layout-safe avatar effects such as roundness, glow and status ring.
- Free-form drag/drop/resizing/rotation and custom block authoring are not implemented yet.

## Device validation

- Clean-install, upgrade, Telegram login/logout, send/receive, media and Firebase notification flows still need a complete real-device pass.
- Bottom-navigation/content insets need explicit regression testing on profile gifts/media/settings surfaces and on devices with different navigation modes.

## Backup

- The first portable backup UI uses JSON copy/share/paste.
- File-based Android Storage Access Framework import/export is planned after the clipboard/share flow is verified on real devices.
- Per-chat overrides are intentionally excluded because portable backups must not expose chat identifiers.

## Diagnostics

- The diagnostics report is sanitized and does not include account IDs, phone numbers, chats, messages, tokens or credentials.
- Crash-session capture is not yet implemented; current crash reports still need a sanitized manual logcat excerpt.

## Release status

0.1-dev is a development build, not a release-ready stable client. Passing CI and emulator smoke does not replace the remaining real-device checks in docs/DEVICE_SMOKE.md.
