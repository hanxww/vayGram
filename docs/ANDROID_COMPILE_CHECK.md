# Android overlay compile check

The fast core and patch smoke tests do not compile Telegram's Android classes. To catch Java/API integration errors before producing an APK, vayGram also has a compile-only Android check.

Run locally:

```bash
bash scripts/compile-android-overlay.sh
```

The script creates a fresh pinned Telegram checkout with native/media submodules, applies the vayGram overlay and runs:

```text
:TMessagesProj_App:compileAfatDebugJavaWithJavac
```

It uses dummy Telegram API values solely for BuildConfig generation and does **not** produce a distributable APK.

GitHub Actions runs the same check on pull requests that touch Android integration code. A real APK still requires the repository/user's own `VAYGRAM_API_ID` and `VAYGRAM_API_HASH`.
