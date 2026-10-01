# vayGram 0.1-dev release checklist

This checklist is for the first installable public development APK.

Real-device procedure: see `docs/DEVICE_SMOKE.md`.

## Required before publishing

- [x] Final vayGram launcher/adaptive/monochrome icon assets
- [x] vayGram-owned Telegram API ID and API hash configured in CI
- [x] vayGram-owned persistent development signing key configured in CI
- [x] vayGram Firebase project and `google-services.json` configured
- [x] `dev-apk` workflow completes successfully
- [ ] APK installs on a clean real Android device
- [ ] APK upgrades over the previous vayGram dev build
- [ ] Login / logout works
- [ ] Chats list opens
- [ ] Message send / receive works
- [ ] Media download/upload works
- [ ] Notifications tested with Firebase enabled
- [ ] Vay Settings opens from Telegram Settings
- [ ] Live preview, palette, gradients and scope overrides smoke-tested
- [ ] First-run onboarding spotlight smoke-tested on a real device
- [ ] Vay Profile Studio entry and foundation smoke-tested
- [x] SHA-256 and build metadata produced next to the APK
- [ ] GPL source link included with the APK post

## Nice to have before #001 APK

- [ ] crash-reporting strategy decided
- [ ] changelog generated from merged vayGram stages
- [ ] known-issues section prepared
- [ ] one rollback path documented for broken dev builds
