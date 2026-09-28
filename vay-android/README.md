# vay-android

Android-specific adapters for VayCore.

The first adapter persists Vay settings through Android `SharedPreferences` while VayCore itself remains dependency-free.

When vayGram is overlaid into Telegram Android, this module is intended to live under:

```text
TMessagesProj/src/main/java/app/vaygram/android/
```

Initialization:

```java
VayAndroid.initialize(ApplicationLoader.applicationContext);
```

After initialization:

```java
float radius = VayAndroid.settings().get(VayDefaults.CHAT_BUBBLE_RADIUS);
```
