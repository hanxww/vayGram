# Telegram integration overlay

This directory contains vayGram-owned code and small patches designed to be overlaid on the pinned Telegram Android source tree.

The goal is to keep upstream changes reviewable: large features live under `app.vaygram.*`, while Telegram classes receive only narrow hooks.

## Current integration

Implemented:

- vayGram entry in Telegram Settings
- persistent Vay Settings storage
- search and Basic / Advanced / Insane modes
- live numeric preview with undo/reset support
- bubble radius integration
- chat-list row height
- avatar size and roundness
- additional message spacing
- separate vayGram package/account identity
- external Telegram API credential injection
- dev branding transform
- local and GitHub Actions dev APK pipelines

## Overlay flow

```text
Pinned Telegram Android
        +
VayCore / VayAndroid / Vay UI
        +
small upstream patches
        +
vayGram branding transform
        =
vayGram worktree
```

Use `scripts/bootstrap-telegram.sh` for a source worktree or `scripts/build-dev-apk.sh` for a full local dev build.

See `docs/BUILDING.md`.
