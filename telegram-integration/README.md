# Telegram integration overlay

This directory contains vayGram source files that are designed to be copied into the Telegram Android tree.

Target:

```text
TMessagesProj/src/main/java/app/vaygram/
```

The overlay deliberately avoids copying or modifying large Telegram classes. Integration should happen through small hooks.

## Current stage

Implemented:

- Android persistent settings store
- Telegram bootstrap bridge
- generated Vay Settings screen
- search
- Basic / Advanced / Insane mode switcher
- boolean controls
- generated numeric slider dialogs

Still required before the first real APK:

- add one row to Telegram's main Settings screen that opens `VaySettingsActivity`
- place VayCore/VayAndroid sources under the Telegram module
- add the first rendering hooks (bubble radius, avatar size, etc.)
- add localization resources and vayGram branding
