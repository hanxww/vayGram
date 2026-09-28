# Bootstrap a real Telegram Android checkout

vayGram keeps its own code small and overlays it onto a pinned Telegram Android source tree.

Current upstream base:

- Telegram Android 12.10.5
- version code 7105
- commit `dc780e81ed1261c369c27870e8e0999a1eb0b600`

## Create a working checkout

From the vayGram repository:

```bash
bash scripts/bootstrap-telegram.sh
```

The generated source tree will be placed in:

```text
.work/telegram/
```

The script:

1. fetches the pinned Telegram commit;
2. copies VayCore into `TMessagesProj/src/main/java/app/vaygram/core`;
3. copies the Android adapter into `app/vaygram/android`;
4. copies the Telegram-facing vayGram UI/bridge code;
5. applies the small upstream patches.

The first patch adds a visible **vayGram** row to Telegram Settings and opens `VaySettingsActivity`.

## Why this layout

We intentionally do not duplicate hundreds of megabytes of Telegram source in the vayGram repository yet. The project stores only vayGram-owned code plus small patches. This makes it obvious which code belongs to vayGram and makes Telegram upstream updates easier to audit.

Before producing an APK, follow Telegram's upstream build instructions and provide your own Telegram API credentials and signing configuration.
