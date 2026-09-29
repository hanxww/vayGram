# Building vayGram 0.1-dev

vayGram is overlaid on a pinned Telegram Android source revision instead of copying the whole upstream tree into this repository.

## Requirements

- JDK 17
- Android SDK 36
- Android Build Tools 36.0.0
- Android NDK 27.2.12479018
- CMake 3.22.1
- Git with submodule support
- your own Telegram API ID and API hash

Do not commit API credentials, signing keys, Firebase configuration, or other secrets.

## Local dev APK

Export your Telegram API credentials:

```bash
export VAYGRAM_API_ID=123456
export VAYGRAM_API_HASH=0123456789abcdef0123456789abcdef
```

Then run:

```bash
bash scripts/build-dev-apk.sh
```

The script:

1. fetches the pinned Telegram Android source;
2. fetches its native/media submodules;
3. overlays VayCore, Vay Android and Telegram bridge code;
4. applies the small upstream patches;
5. applies vayGram branding;
6. generates or reuses a local dev signing key;
7. builds the `afatDebug` application.

Output:

```text
.work/artifacts/vayGram-0.1-dev.apk
```

Current dev package:

```text
app.vaygram.messenger.beta
```

The local development signing key is kept under `.work/keys/`, which is ignored by Git, so normal local rebuilds remain update-compatible.

## GitHub Actions

A manual workflow named **dev-apk** is included.

Before running it, add repository secrets:

- `VAYGRAM_API_ID`
- `VAYGRAM_API_HASH`

Then open **Actions → dev-apk → Run workflow**. The resulting APK is uploaded as a workflow artifact.

The CI development key is ephemeral unless a persistent signing setup is added later, so APKs from separate CI runs are not guaranteed to install over each other.

## Firebase

Firebase is optional for this early dev build. If `TMessagesProj/google-services.json` is absent, the Google Services Gradle plugin is not applied. This means Firebase-dependent functionality such as FCM push delivery may be unavailable in that build.

A vayGram-owned Firebase project will be added before a stable public release.

## Telegram API identity

The upstream Telegram API ID/hash are not used by vayGram. The build reads only `VAYGRAM_API_ID` and `VAYGRAM_API_HASH` from the environment or generated Telegram `local.properties`.

Official-app-only SafetyNet, store billing identity and passkey support are disabled in the vayGram dev build.


## Optional persistent CI signing

For update-compatible CI APKs, add these repository secrets:

- `VAYGRAM_SIGNING_KEYSTORE_BASE64`
- `VAYGRAM_KEYSTORE_PASSWORD`
- `VAYGRAM_KEY_ALIAS`
- `VAYGRAM_KEY_PASSWORD`

The keystore secret must contain the Base64-encoded bytes of a vayGram-owned development keystore. When these secrets are absent, the local script can still generate a disposable development key.

## Optional Firebase wiring

To enable FCM in CI builds, add:

- `VAYGRAM_FIREBASE_JSON_BASE64`

It must contain the Base64-encoded `google-services.json` for the vayGram Firebase project, including the dev application id used by the current build.

If the secret is absent, the APK still builds without the Google Services Gradle plugin, but Firebase-dependent features can be unavailable.

## Build artifacts

A successful dev build now produces three files:

```text
vayGram-0.1-dev.apk
vayGram-0.1-dev.sha256
vayGram-0.1-dev-build-info.txt
```

The metadata records the vayGram commit, pinned Telegram commit, package id, UTC build time, size and SHA-256 checksum.
