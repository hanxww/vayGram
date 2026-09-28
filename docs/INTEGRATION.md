# Telegram Android integration plan

Target source location:

```text
TMessagesProj/src/main/java/app/vaygram/
```

Suggested layers:

```text
app/vaygram/core/        pure logic
app/vaygram/android/     Android persistence/lifecycle
app/vaygram/ui/          settings screens and editors
app/vaygram/hooks/       narrow Telegram integration points
app/vaygram/theme/       Vay Theme Engine
app/vaygram/profile/     Vay Profile Studio
```

Avoid large edits in Telegram classes. Prefer thin lookups such as:

```java
float radius = Vay.get().settings().get(VayDefaults.CHAT_BUBBLE_RADIUS);
```

`VaySettingsStore` is an interface on purpose; Android integration will provide persistent storage backed by an Android local store.
