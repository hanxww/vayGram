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

For automation that cannot call GitHub's workflow-dispatch API, the repository owner can also trigger the same workflow from issue #27 by commenting exactly:

```text
/build-dev-apk
```

The comment-triggered path always checks out the current `main`, is restricted to the repository owner, and reports the run result back to the control issue.

The CI development key is ephemeral unless a persistent signing setup is added later, so APKs from separate CI runs are not guaranteed to install over each other.

## Firebase

The upstream Telegram `google-services.json` is always removed from a vayGram worktree. It must never be reused because its Firebase clients belong to Telegram's package identity.

Firebase is optional for the early dev build. Without a vayGram-owned configuration, the Google Services Gradle plugin is not applied and Firebase-dependent functionality such as FCM push delivery may be unavailable.

To test a vayGram-owned Firebase project locally, point the build at its configuration:

```bash
export VAYGRAM_GOOGLE_SERVICES_JSON=/absolute/path/to/google-services.json
```

The branding step copies that file into the generated Telegram worktree after removing the upstream configuration. A vayGram-owned Firebase project is still required before a stable public release.

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


## Non-distributable APK install smoke

The release APK still requires vayGram-owned Telegram API credentials. Independently of those secrets, CI can validate the complete Android packaging/install/launch path with dummy BuildConfig credentials.

Repository owner command in issue #27:

```text
/smoke-dev-apk
```

This smoke workflow:

1. builds the full `afatDebug` APK from the pinned Telegram source;
2. uses dummy Telegram API values, so the resulting APK is **not distributable**;
3. does not upload the smoke APK as an artifact;
4. clean-installs it into an Android emulator;
5. verifies package id and `0.1-dev` version;
6. launches the app through its launcher intent and verifies the process remains alive without a crash-buffer entry.

A passing smoke check validates build packaging, clean installation and first launch only. It does not validate login, Telegram network functionality, Firebase notifications, upgrade compatibility, or the public release checklist.
