# Telegram integration points

The current overlay was designed against the current `DrKLO/Telegram` Android tree.

Known upstream locations:

- Main settings screen:
  `TMessagesProj/src/main/java/org/telegram/ui/SettingsActivity.java`
- Telegram-style settings screen examples:
  `TMessagesProj/src/main/java/org/telegram/ui/LiteModeSettingsActivity.java`
  and
  `TMessagesProj/src/main/java/org/telegram/ui/RoundVideoSettingsActivity.java`
- Main message cell:
  `TMessagesProj/src/main/java/org/telegram/ui/Cells/ChatMessageCell.java`
- Application context:
  `org.telegram.messenger.ApplicationLoader.applicationContext`

## First integration hook

The first upstream edit should be intentionally tiny: add one row/action to Telegram settings which calls:

```java
presentFragment(new app.vaygram.ui.VaySettingsActivity());
```

All UI generation, persistence and setting definitions stay in `app.vaygram.*`.

## First appearance hooks

Telegram drawing/layout code should query narrow helpers rather than knowing about VayCore:

```java
float radius = VayAppearance.chatBubbleRadiusDp();
float avatar = VayAppearance.avatarSizeDp();
```

This is the preferred merge strategy: small upstream diffs, large vayGram-owned modules.
