# vayGram — Chat Handoff / Current Project Context

> Purpose: this file is the durable context for continuing vayGram development from a fresh ChatGPT chat without relying on the previous conversation.

Last context refresh: 2026-09-30  
Repository: https://github.com/hanxww/vayGram  
Development channel: https://t.me/vayGram_app  
Current line: **0.1-dev**  
Slogan: **Customize everything. Complicate nothing.**

---

## 1. Product vision

vayGram is an alternative Android Telegram client built on the official Telegram Android codebase.

The main idea is extreme customization without making the default experience complicated.

A normal user should be able to install vayGram and use it immediately. A power user should be able to expose progressively deeper controls through:

- Basic
- Advanced
- Insane

Long term, the client should allow customization of almost every visible/interactive part of Telegram while keeping vayGram-owned code isolated from Telegram upstream as much as possible.

The product should feel hand-designed and deliberate, not like a pile of random mod toggles.

---

## 2. Architecture rule

Keep Telegram upstream changes **small and reviewable**.

Preferred layering:

```text
Telegram upstream
      |
      +-- small hooks / patches
      |
      v
app.vaygram.*
      |
      +-- VayCore
      +-- Android adapters
      +-- Vay UI
      +-- Theme Engine
      +-- feature modules
```

Original conceptual structure:

```text
vay/
├── core/
│   ├── settings/
│   ├── storage/
│   ├── profiles/
│   ├── rules/
│   └── backup/
├── ui/
│   ├── settings/
│   ├── preview/
│   ├── picker/
│   └── components/
├── features/
│   ├── chats/
│   ├── messages/
│   ├── media/
│   ├── player/
│   ├── downloads/
│   ├── navigation/
│   └── gestures/
├── theme/
│   ├── tokens/
│   ├── engine/
│   ├── presets/
│   └── gradients/
└── hooks/
    └── telegram/
```

In the current repository this is implemented as dependency-free VayCore, Android-specific adapters, Telegram overlay code under `app.vaygram.*`, and a set of small upstream patch files.

Do not move large Telegram classes into vayGram just to modify them.

---

## 3. Settings architecture

Settings are data-driven.

A registered setting should be able to automatically participate in:

- settings UI
- search
- categories
- persistence
- reset
- modified indicators
- live preview
- undo/redo
- presets
- import/export
- Global / Account / Chat scopes
- inherited values

Core concepts already implemented include:

- typed settings
- validation / normalization
- numeric metadata/ranges
- Basic / Advanced / Insane visibility
- searchable registry
- Global / Account / Chat scopes
- inheritance
- explicit overrides
- change history
- undo / redo
- preview / commit / cancel
- persistent settings profiles
- JSON preset import/export

Scope resolution, where supported:

```text
chat override
    ↓
account override
    ↓
global value
    ↓
setting default
```

Non-global screens show inherited values separately from explicit overrides.

---

## 4. Current ROADMAP status

As of current `main`, the repository roadmap reports:

### M0 — Core: COMPLETE

- setting definition model
- registry
- search
- Basic / Advanced / Insane
- Global / Account / Chat scopes
- history + undo/redo
- live-preview listeners
- Android persistence
- numeric range metadata
- migration seed API

### M1 — First UI: COMPLETE

- Vay Settings entry in Telegram settings
- generated categories
- search
- Basic / Advanced / Insane switcher
- boolean/numeric controls
- recently changed
- changed-value indicators
- per-setting reset
- reset all
- undo/redo controls
- scope-aware screens
- live numeric preview
- embedded visual preview host

### M2 — First visible mods: COMPLETE

- chat bubble radius
- message spacing
- chat-list row height
- avatar size
- avatar roundness
- bottom bar height
- bottom bar labels
- animation scale for vayGram-controlled main navigation
- AMOLED surface mode
- compact mode

### M3 — Presets / overrides: COMPLETE

- persistent settings profiles
- import/export
- per-account overrides UI
- per-chat overrides UI

### M4 — Theme Engine: COMPLETE for the initial milestone

- semantic token registry
- searchable palette editor
- Material You bridge
- gradients
- blur/transparency controls

### M5 — Branding / builds: PARTIAL

Done:

- distinct application id/account type
- vayGram labels
- dev version suffix
- own Telegram API credential injection
- isolated dev signing setup
- reproducible dev APK pipeline
- checksum/build metadata
- optional persistent CI signing/Firebase secret wiring

Still open:

- final vayGram icon assets
- vayGram-owned Firebase configuration
- first installable public `0.1-dev` APK

Always re-read `docs/ROADMAP.md` before starting new work, because this handoff may become stale.

---

## 5. Theme Engine already implemented

The Theme Engine is no longer just an idea.

Current pieces include:

- semantic theme token registry
- Telegram theme-key bridge
- persistent token color overrides
- searchable palette editor
- ARGB live editing
- manual override precedence
- Material You dynamic colors on supported Android versions
- AMOLED surface enforcement
- reusable gradient model/editor
- bottom-navigation gradient target
- blur radius / opacity controls for bottom-navigation glass

Theme color precedence is intended to remain conceptually:

```text
manual vayGram token override
        ↓
Material You value
        ↓
Telegram theme color
        ↓
AMOLED enforcement for supported dark surfaces
```

When extending Theme Engine, prefer semantic Vay tokens over direct Telegram theme-key access.

See:

- `docs/THEME_ENGINE.md`
- theme code under VayCore / Vay Android / Telegram overlay
- current Theme-related patches

---

## 6. Branding / build identity

Current dev identity:

```text
App: vayGram / vayGram Dev
Version: 0.1-dev
Base package: app.vaygram.messenger
Dev package: app.vaygram.messenger.beta
```

Do not reuse official Telegram API credentials.

Builds take vayGram-owned credentials from:

- `VAYGRAM_API_ID`
- `VAYGRAM_API_HASH`

Secrets, signing keys and Firebase files must not be committed.

Build docs:

- `docs/BUILDING.md`
- `docs/RELEASE_CHECKLIST.md`

The local/CI build pipeline can produce:

```text
vayGram-0.1-dev.apk
vayGram-0.1-dev.sha256
vayGram-0.1-dev-build-info.txt
```

---

## 7. IMPORTANT: immediate unfinished engineering task

At the time this handoff was written, **PR #22 is still open**:

https://github.com/hanxww/vayGram/pull/22

Title:

```text
ci: compile Android overlay on integration changes
```

Purpose:

- create a real Android compile validation layer
- fetch pinned Telegram Android + submodules
- apply the full vayGram overlay
- apply branding
- use dummy API values only for BuildConfig generation
- run:
  `:TMessagesProj_App:compileAfatDebugJavaWithJavac`
- do NOT publish an APK from this check

PR #22 head:

```text
dev/1.7-android-compile-ci
6654f74d0cec552c3107594ec1bb453172ca776d
```

All three workflows on that head passed:

- core-check ✅
- upstream-overlay-check ✅
- android-compile-check ✅

However, the PR is currently not mergeable against latest `main`, because newer work landed after the branch was created.

**First task in a new development chat should be:**

1. inspect latest `main`;
2. inspect PR #22;
3. recreate/rebase the Android compile-check changes onto current `main` in a clean branch;
4. run all checks;
5. merge only after the compile check succeeds against current `main`.

Do not simply merge stale PR #22 without reconciling it with latest main.

---

## 8. Latest build-readiness work already merged

PR #23 is merged:

https://github.com/hanxww/vayGram/pull/23

It hardened the first APK pipeline with:

- build preflight
- API credential validation
- persistent CI signing support
- optional Firebase config injection
- APK SHA-256
- build metadata
- shell syntax CI
- expanded build docs
- release checklist

This means the main blocker for the first public APK is no longer basic scripting. The project needs final external credentials/assets plus a successful up-to-date full compile/build/test cycle.

---

## 9. First APK release checklist

Before calling anything the first public `0.1-dev` APK, the repository checklist currently expects:

- final launcher/adaptive/monochrome icon
- vayGram-owned Telegram API ID/hash in CI
- persistent vayGram development signing key
- vayGram Firebase project/config
- successful dev-apk workflow
- clean install test
- upgrade-over-previous-build test
- login/logout
- chats list
- send/receive messages
- media upload/download
- notifications with Firebase
- Vay Settings entry
- smoke-test live preview / palette / gradients / scope overrides
- publish checksum + build metadata
- include GPL source link

Do not mark the APK milestone complete just because Gradle produced a file.

---

## 10. Visual identity / icon direction

The user rejected earlier AI-looking icon attempts.

Important constraints:

- do NOT use a Telegram paper-plane as the main symbol
- any obvious plane makes it look like “Telegram in another style”
- avoid glossy 3D, glass crystals, neon rims, dramatic lighting and generic AI-app aesthetics
- prefer flat/vector-first geometry
- should look like something designed in Figma/Illustrator
- one strong silhouette
- must survive 24–48 px
- should work as both app icon and Telegram channel avatar
- unique **V monogram** is the preferred direction
- restrained near-black/indigo + off-white/light lavender/violet palette
- ideally design monochrome first, then add color
- Android adaptive/themed/monochrome icon support matters

A good starting visual direction:

```text
background: #0D0D12
glyph: custom geometric V
main glyph: #F4F2FF
controlled accent: #7C5CFC
no shadow
no glass
no 3D
no glow
```

If generating an icon with ChatGPT, use image generation and explicitly demand flat vector / no 3D / no glass / no glow / no paper plane.

---

## 11. Signature long-term feature: Vay Profile Studio

A key product differentiator planned for later is a block-based free-form profile editor.

Potential blocks:

- avatar
- name
- username
- bio
- phone
- birthday
- emoji status
- channel/groups
- links
- music
- quote
- badges
- gifts
- media
- mutual chats
- custom text/image
- separators

Modes:

- Grid Layout for normal users
- Free Layout for power users

Possible editing features:

- drag/drop
- resize
- arbitrary positions
- z-index
- alignment
- opacity
- background
- blur
- parallax
- animations
- avatar shapes/effects

The cross-vayGram sharing concept is opt-in:

- standard Telegram profile data remains Telegram data
- a user explicitly publishes a Vay Profile
- vayGram stores only the custom layout separately
- other vayGram users can fetch the published layout
- official Telegram clients still show the normal Telegram profile

Do not upload private settings/data to VayCloud without explicit opt-in.

This is later-stage work; do not derail the 0.1-dev foundation to build it prematurely.

---

## 12. Other planned feature families

Long-term product map includes:

- Vay Settings
- Vay Theme Engine
- Chat Studio
- Profile Studio
- Layout Engine
- Gesture Studio
- Motion Engine
- icon packs
- Home/Header/Bottom Bar editors
- message action customization
- media viewer
- Vay Player
- voice-message controls
- Download Manager
- Storage Manager
- sticker/emoji/GIF controls
- reactions
- translation
- global search extensions
- folders
- multi-account
- Notification Studio
- Quick Actions / command palette
- automation/profiles by time/battery/system mode/account
- accessibility controls
- performance profiles
- app lock / privacy UI
- local encrypted settings vault
- optional VayCloud
- plugins later
- Vay Labs
- safe mode / crash recovery
- backup / migration / versioning
- developer tools

Insane mode may eventually include a declarative VayStyles language such as:

```text
message.outgoing {
    radius: 12;
    padding-x: 14;
}

profile.avatar {
    size: 128;
    shape: squircle;
}
```

---

## 13. Project rules / constraints

### Upstream

- prefer pinned Telegram Android revision for reproducibility
- keep upstream modifications small
- put vayGram behavior behind adapters/bridges
- expect Telegram upstream to change; avoid unnecessary coupling

### Licensing / distribution

Telegram Android is GPL-based. For redistribution, keep GPL compliance/source availability in the release process.

### Telegram API

Use vayGram-owned Telegram API credentials. Before adding questionable protocol behavior, review current Telegram API terms.

Project rule: do not implement fake/ghost read, fake typing/online state, or self-destruct-content bypass as casual “privacy features”.

### Privacy

- no private setting/profile upload without explicit opt-in
- VayCloud remains optional
- local settings should remain local by default

---

## 14. Development workflow expected from ChatGPT

When continuing development in a new chat:

1. Read this file.
2. Read `docs/ROADMAP.md`.
3. Inspect latest `main`.
4. Inspect open PRs and current CI status.
5. Do not trust old branch state blindly.
6. Create a focused branch.
7. Make small cohesive commits.
8. Keep Telegram patch files minimal.
9. Run:
   - VayCore checks
   - upstream overlay checks
   - Android compile check when integration code changes
10. Fix CI before merge.
11. Open PR.
12. Merge only when current checks pass.
13. Update ROADMAP/docs when a milestone changes.

Do not claim an APK exists until the build actually produced one.

Do not expose or commit secrets.

---

## 15. Communication style

The project owner communicates in Russian and prefers concise, direct development updates.

Avoid long formal project-management prose unless needed.

Good status format:

```text
Сделал Stage N.

✅ ...
✅ ...
✅ CI

PR #...
Следующее: ...
```

Profanity from the user is normal; no need to become defensive/formal.

---

## 16. Telegram development channel

Channel:

https://t.me/vayGram_app

Name:

```text
vayGram • Development
```

Suggested tags:

- #devlog
- #concept
- #vayfeature
- #build
- #changelog
- #poll
- #bug

Slogan:

```text
Customize everything. Complicate nothing.
```

---

## 17. New-chat bootstrap prompt

The user can start a fresh ChatGPT chat with:

```text
Продолжаем разработку vayGram.

Репозиторий:
https://github.com/hanxww/vayGram

Сначала прочитай:
docs/CHAT_HANDOFF.md
docs/ROADMAP.md
docs/BUILDING.md
docs/RELEASE_CHECKLIST.md

Потом проверь latest main, открытые PR и CI.

Продолжай разработку сам, не пересказывай мне весь репозиторий.
Сохраняй архитектуру: VayCore / Android adapters / app.vaygram Telegram overlay / минимальные upstream patches.
Перед merge прогоняй core-check, upstream-overlay-check и Android compile check для integration-изменений.

Первым делом разберись с открытым PR #22: его Android compile-check уже проходил на старом head, но ветку нужно аккуратно перенести/пересобрать поверх текущего main и снова прогнать CI.

После этого двигайся к первой реально протестированной vayGram 0.1-dev APK по docs/RELEASE_CHECKLIST.md.
```

This prompt + this file should be enough to continue without access to the old chat.
