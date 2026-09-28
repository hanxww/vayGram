# Local Telegram worktree

The repository keeps vayGram-owned code separate from Telegram upstream.

To create a disposable Telegram Android checkout with the current vayGram overlay:

```bash
chmod +x tools/bootstrap-telegram.sh
./tools/bootstrap-telegram.sh ./telegram-worktree
```

The script clones `DrKLO/Telegram`, copies VayCore, the Android adapter, and the Telegram overlay under `TMessagesProj`.

It intentionally does **not** rewrite a large upstream class automatically. The only remaining entry hook is a tiny change in Telegram's `SettingsActivity` that opens:

```java
presentFragment(new app.vaygram.ui.VaySettingsActivity());
```

This separation is deliberate so updating Telegram upstream remains reviewable.
