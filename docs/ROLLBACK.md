# vayGram dev-build rollback

Use this when a new dev APK causes a vayGram-specific regression.

1. Before installing a risky build, open **Vay Settings → Backup & restore** and copy/share a portable vayGram backup.
2. Keep the previously known-good APK artifact signed with the same persistent vayGram dev key.
3. If the new build is usable enough to open settings, try **Diagnostics and recovery** first:
   - reset the current vayGram scope;
   - reset the vayGram theme layer.
4. If the regression remains, reinstall the previous signed APK over the current install when Android permits a version-compatible downgrade. Do not uninstall if preserving Telegram session data matters.
5. If Android blocks the downgrade, do not clear app data as a routine recovery step. Capture sanitized diagnostics/logcat and fix forward with a new signed build.
6. After a fixed build is installed, restore the portable vayGram backup if needed.

Portable vayGram backups never contain Telegram sessions, chats, messages, phone numbers, account IDs or per-chat identifiers. They are not a substitute for Telegram's own account/session behavior.
