# vayGram — Chat handoff / continuation context

Last synchronized with the repository: **2026-09-30**.

This file exists so development can continue in a brand-new ChatGPT chat without relying on the previous conversation history.

## 1. Project identity

- Project: **vayGram**
- Repository: `https://github.com/hanxww/vayGram`
- Development channel: `https://t.me/vayGram_app`
- Current dev line: **0.1-dev**
- Product slogan: **Customize everything. Complicate nothing.**
- Base: official Telegram Android source, kept as a pinned upstream overlay rather than copied wholesale into this repository.
- Current pinned Telegram upstream:
  - version: **12.10.5**
  - version code: **7105**
  - commit: `dc780e81ed1261c369c27870e8e0999a1eb0b600`

## 2. Product goal

vayGram is an alternative Android Telegram client focused on extreme customization without making the default experience complicated.

The intended UX model is layered:

- **Basic** — normal useful customization;
- **Advanced** — deeper layout/behavior controls;
- **Insane** — very granular power-user controls.

The app should work sensibly out of the box. Advanced systems should stay out of the way until the user wants them.

Long-term feature families include:

- Vay Settings
- Vay Theme Engine
- Layout Engine
- Chat Studio
- Profile Studio
- Navigation editor
- Gesture Studio
- Motion Engine
- Vay Player
- Download Manager
- Notifications Studio
- Profiles / Presets
- VayStyles
- VayCloud
- Vay Plugins
- Vay Labs
- accessibility and performance profiles
- safe mode / crash recovery
- backup / migration / versioning
- developer tools

## 3. Architecture rules

The most important engineering rule is:

> Keep vayGram-owned code isolated from Telegram upstream wherever possible.

Preferred structure:

```text
Telegram Android
      |
      +-- tiny hooks / adapters
              |
              v
          vayGram code
          |-- VayCore
          |-- Vay Android bridge
          |-- Vay UI
          |-- Theme Engine
          |-- future feature modules
```

Do not spread vayGram logic through Telegram classes if a narrow adapter can be used instead.

Current repository structure follows this approach:

- `vay-core/` — dependency-free Java core
- `vay-android/` — Android-specific persistence/adapters
- `telegram-integration/` — Telegram-facing vayGram code and small upstream patches
- `scripts/` — bootstrap/build/check tooling
- `docs/` — architecture, roadmap, build/release docs

The upstream integration model is:

```text
Pinned Telegram Android
        +
VayCore / VayAndroid / Vay UI
        +
small Telegram patches
        +
branding transform
        =
vayGram worktree
```

## 4. Vay Settings model

Settings are data-driven.

A registered setting defines things such as:

- stable ID
- type
- default value
- title / description
- category
- Basic / Advanced / Insane visibility
- supported scopes
- tags / search aliases
- numeric range metadata
- validation / normalization

The framework then provides or is intended to provide common behavior automatically:

- generated UI
- search
- persistence
- modified markers
- reset
- recent changes
- undo / redo
- live preview
- presets
- import / export
- scope inheritance
- future backup/migration tooling

Supported scopes:

- **GLOBAL**
- **ACCOUNT**
- **CHAT**

Current inheritance behavior where the setting supports the scopes:

```text
chat override
    ↓
account override
    ↓
global value
    ↓
setting default
```

Non-global screens visibly distinguish explicit overrides from inherited values.

## 5. Current implemented state

The repository `main` is the source of truth. Always read `docs/ROADMAP.md` before continuing.

### M0 — Core: complete

Implemented:

- setting definition model
- registry
- search
- Basic / Advanced / Insane levels
- Global / Account / Chat scopes
- history + undo/redo
- live-preview listeners
- Android persistence
- generated numeric metadata
- migration seed API

### M1 — First UI: complete

Implemented:

- vayGram Settings entry inside Telegram Settings
- generated categories
- settings search
- Basic / Advanced / Insane switcher
- boolean controls
- numeric controls
- recently changed
- changed-value indicators
- per-setting reset
- reset all
- undo / redo
- scope-aware settings UI
- numeric live preview with rollback/apply
- embedded visual preview host

The embedded preview reflects effective values for:

- chat list density
- avatar size/roundness
- bubble radius
- message spacing
- AMOLED
- compact mode
- bottom navigation
- theme effects

### M2 — First visible mods: complete

Implemented real Telegram rendering hooks for:

- chat bubble radius
- message spacing
- chat-list row height
- avatar size
- avatar roundness
- bottom bar height
- bottom bar labels
- navigation animation scale
- AMOLED surfaces
- compact mode

Compact mode is intentionally non-destructive: it derives tighter effective values without overwriting the user's detailed settings.

### M3 — Presets/scopes: complete

Implemented:

- persistent settings profiles
- apply/delete saved profiles
- JSON import/export
- per-account overrides
- per-chat overrides
- chat menu entry for vayGram Chat Settings
- inherited values with explicit override semantics

### M4 — Theme Engine: complete for the first milestone

Implemented:

- semantic theme-token registry
- Telegram theme-key mapping bridge
- persistent manual palette overrides
- searchable palette editor
- live ARGB preview
- Material You bridge on Android 12+
- manual override > Material You > Telegram fallback precedence
- AMOLED enforcement after color resolution
- glass blur
- glass blur radius
- glass opacity/transparency
- reusable gradient model
- persistent gradient editor
- live bottom-navigation gradient target

The semantic-token approach is important: new vayGram features should depend on tokens such as:

- `surface.primary`
- `chat.bubble.out`
- `navigation.icon.active`

rather than directly coupling feature code to Telegram theme-key names.

See `docs/THEME_ENGINE.md`.

### M5 — Branding/builds: mostly complete

Implemented:

- distinct application identity
- vayGram labels
- dev version suffix
- vayGram Telegram API credential injection
- isolated development signing
- reproducible dev APK pipeline
- APK SHA-256/build metadata
- optional persistent CI signing secret wiring
- optional Firebase config secret wiring

Current package identity:

```text
base package: app.vaygram.messenger
dev package:  app.vaygram.messenger.beta
version:      0.1-dev
```

Still pending before the first public installable APK:

- final vayGram launcher/adaptive/monochrome icon assets
- vayGram-owned Telegram API ID/hash configured in CI
- vayGram-owned persistent dev signing key configured in CI
- vayGram Firebase project / `google-services.json`
- successful full `dev-apk` workflow run
- real-device smoke tests
- first public `vayGram 0.1-dev` APK

See:

- `docs/BUILDING.md`
- `docs/RELEASE_CHECKLIST.md`

## 6. Important open work: PR #22

At the time this handoff was written, **PR #22 is still open and not merged**:

- title: `ci: compile Android overlay on integration changes`
- branch: `dev/1.7-android-compile-ci`

It adds a real Android Java compile check on top of the existing fast checks.

Its latest branch-head checks are green:

- core-check ✅
- upstream-overlay-check ✅
- android-compile-check ✅

However, `main` advanced significantly after that branch was opened, including build-readiness work.

Therefore a new chat should **not blindly merge PR #22**. First inspect it against current `main`. Prefer one of:

1. rebase/update it cleanly if practical; or
2. create a fresh branch from current `main` and port the compile-check changes; then close/supersede #22.

The compile verification itself is valuable and should be preserved before the first APK.

## 7. Build / release rules

Never commit secrets.

Required Telegram credentials are supplied through:

- `VAYGRAM_API_ID`
- `VAYGRAM_API_HASH`

Optional persistent CI signing uses repository secrets such as:

- `VAYGRAM_SIGNING_KEYSTORE_BASE64`
- `VAYGRAM_KEYSTORE_PASSWORD`
- `VAYGRAM_KEY_ALIAS`
- `VAYGRAM_KEY_PASSWORD`

Optional Firebase CI wiring uses:

- `VAYGRAM_FIREBASE_JSON_BASE64`

Local dev build command:

```bash
bash scripts/build-dev-apk.sh
```

Expected outputs after a successful build:

```text
vayGram-0.1-dev.apk
vayGram-0.1-dev.sha256
vayGram-0.1-dev-build-info.txt
```

Before publishing the first APK, follow `docs/RELEASE_CHECKLIST.md`.

## 8. Legal / Telegram constraints

vayGram is based on Telegram Android source.

Important constraints:

- use a vayGram-owned Telegram `api_id` / `api_hash`;
- preserve GPL redistribution/source obligations;
- keep source availability clear when distributing APKs;
- do not implement fake read/typing/online states intended to deceive Telegram;
- do not bypass self-destruct / protected-content behavior;
- do not silently upload private settings or user data to VayCloud;
- any VayCloud feature must be explicit opt-in.

If Telegram API terms need to be quoted or relied upon for a current decision, verify the current terms on the web because they may change.

## 9. Signature future feature: Vay Profile Studio

A major long-term differentiator is **Vay Profile Studio**.

The idea is a block-based, freely arranged profile designer.

Possible blocks:

- avatar
- name
- username
- bio
- phone
- birthday
- emoji status
- channel
- groups
- links
- music
- quote
- badges
- gifts
- media
- mutual chats
- custom text/image
- separators

Editing modes:

- Grid Layout for normal users
- Free Layout for power users

Expected capabilities:

- drag/drop
- resize
- arbitrary positions
- z-index
- alignment
- opacity
- custom backgrounds
- blur
- parallax
- animations
- avatar shapes/effects
- profile entrance effects
- presets / clone / import / export / share

The long-term cross-client concept is opt-in **VayProfile Cloud**:

- Telegram's standard profile data remains Telegram data;
- arbitrary vayGram layout metadata is stored separately only after explicit publish;
- other vayGram users can fetch the custom layout by Telegram user ID;
- official Telegram clients continue to see the normal Telegram profile;
- viewers can choose Author design / My global style / Simplified / Telegram default.

Do not upload private client settings as part of this system.

## 10. Icon / visual identity context

Final icon assets are still pending and are a release blocker.

Several previous concepts were rejected because they looked like:

- a Telegram paper-plane restyle;
- obvious AI-generated glossy app art;
- generic futuristic 3D/glass/neon imagery.

The correct direction is:

- **not a Telegram plane**
- custom **V monogram**
- flat/vector-first
- clean geometric construction
- strong silhouette
- works at 24–48 px
- reproducible in Figma/Illustrator/SVG
- Android adaptive-icon friendly
- monochrome/themed-icon compatible
- no 3D crystal
- no glow/rim lighting
- no complex reflections
- no generic AI-futuristic treatment
- restrained near-black / indigo / off-white / controlled violet palette

Best process: make the mark work in black/white first, then add restrained color.

A promising direction:

- matte near-black background around `#0D0D12`
- custom V built from two precise diagonal strokes
- controlled negative-space cut
- off-white/lavender main glyph
- one restrained violet accent such as `#7C5CFC`
- no shadow

If generating/editing the icon in ChatGPT, use the image-generation tool and explicitly request a flat vector logo with no 3D/glow/glass effects.

## 11. Development channel / public communication

Telegram development channel:

`https://t.me/vayGram_app`

Useful post tags:

- `#devlog`
- `#concept`
- `#vayfeature`
- `#build`
- `#changelog`
- `#poll`
- `#bug`

The public positioning is:

> vayGram — Telegram, который ты настраиваешь под себя.

The first devlog originally described the foundation stage. The actual repository is now much further along, so future devlogs should use `docs/ROADMAP.md` and merged PRs as the factual source rather than the original checklist.

## 12. How ChatGPT should continue this project

When the user says things like:

- `дальше`
- `продолжай`
- `разрабатывай дальше`

do actual repository work rather than only proposing ideas.

Preferred workflow:

1. read current `main`;
2. read `docs/ROADMAP.md`;
3. inspect open PRs and current CI;
4. identify the nearest unfinished milestone;
5. create a focused branch from current `main`;
6. implement the feature mainly in vayGram-owned modules;
7. keep Telegram upstream patches narrow;
8. update tests/check scripts and roadmap;
9. open a PR;
10. inspect CI;
11. fix failures;
12. merge when checks are green and the change is safe.

Do not claim an APK was built unless a full APK build actually completed.

Do not claim CI is green without checking it.

Do not assume a stale open branch is safe to merge after `main` has moved.

## 13. Recommended next engineering priorities

The immediate sequence from the current repository state should be:

1. **Finish/port PR #22 Android compile verification onto current main.**
   The compile check has already succeeded on its existing head, but the branch is stale relative to current main.
2. **Run the compile verification against current main after the port.**
3. **Finalize icon assets** for launcher/adaptive/monochrome use.
4. **Configure user-owned external secrets** for Telegram API, persistent dev signing and Firebase. These cannot be invented or committed by ChatGPT.
5. **Run the first real dev APK workflow.**
6. **Fix compile/runtime issues uncovered by the full build.**
7. **Perform the release checklist on a real Android device.**
8. Only then mark **first installable 0.1-dev APK** complete.

After the first installable APK, begin the next feature milestone rather than expanding the foundation indefinitely. Good candidates are Chat Studio or the first Profile Studio foundation.

## 14. Minimal prompt for a new ChatGPT chat

Paste this into a new chat:

```text
Продолжаем разработку vayGram.

Репозиторий:
https://github.com/hanxww/vayGram

Сначала обязательно прочитай:
- docs/CHAT_HANDOFF.md
- docs/ROADMAP.md
- docs/ARCHITECTURE.md
- docs/BUILDING.md
- docs/RELEASE_CHECKLIST.md
- docs/THEME_ENGINE.md

Проверь текущее состояние main, открытые PR и CI перед любыми изменениями.

Дальше продолжай разработку сам по ближайшему незакрытому этапу. Работай прямо с GitHub: отдельная ветка → код → проверки → PR → исправление CI → merge, если всё зелёное.

Главное архитектурное правило: vayGram-код держать отдельно, а изменения Telegram upstream делать минимальными hook/adapter-патчами.

Не говори, что что-то собрано или протестировано, если реально не проверил.
```

That prompt plus this repository file is intended to be sufficient to resume development without the original conversation.
