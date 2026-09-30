# vayGram — Chat Handoff / Durable Project Context

> This is the canonical handoff for continuing vayGram development from a fresh ChatGPT chat. A new chat should not need access to the old conversation: read this file, inspect current `main`, ROADMAP, open PRs and CI, then continue.

Last refresh: **2026-09-30**  
Repository: https://github.com/hanxww/vayGram  
Development channel: https://t.me/vayGram_app  
Current line: **0.1-dev**  
Slogan: **Customize everything. Complicate nothing.**  
Current main at refresh: `3d190d5b3908e472de44e76404d72b3a8c7e2b7d`  
Pinned Telegram Android: **12.10.5 / 7105**, commit `dc780e81ed1261c369c27870e8e0999a1eb0b600`

---

## 1. Product vision

vayGram is an alternative Android Telegram client built on the official Telegram Android codebase.

The product goal is extreme customization without making the default experience complicated.

A normal user should be able to install vayGram and use it immediately. A power user can progressively expose deeper controls through:

- Basic
- Advanced
- Insane

The client should feel deliberately designed, not like a random pile of mod toggles.

Core rule:

> **Customize everything. Complicate nothing.**

---

## 2. Architecture rule

Keep Telegram upstream changes **small, reviewable and disposable**.

Preferred layering:

```text
Telegram Android upstream
        |
        +-- tiny patch/hooks only
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

Current repository pattern:

- dependency-free Java VayCore;
- Android persistence/adapters under vay-android;
- Telegram overlay sources under `app.vaygram.*`;
- small upstream patch files under `telegram-integration/patches/`;
- pinned upstream revision in `telegram-integration/UPSTREAM`;
- bootstrap/build scripts generate a disposable Telegram worktree.

Do not copy whole Telegram classes into vayGram just to modify them.

---

## 3. Settings architecture

Settings are data-driven.

A registered setting can participate in:

- generated settings UI;
- search / aliases;
- categories;
- persistence;
- Basic / Advanced / Insane visibility;
- reset;
- modified indicators;
- live preview;
- undo / redo;
- presets;
- JSON import/export;
- Global / Account / Chat scopes;
- inherited values.

Implemented core behavior includes:

- typed settings;
- validators / normalization;
- numeric ranges;
- searchable registry;
- change history;
- preview / commit / cancel;
- migration seed API;
- persistent profiles;
- scope inheritance and explicit overrides.

Scope resolution, where the setting supports all levels:

```text
chat override
    ↓
account override
    ↓
global value
    ↓
setting default
```

Non-global screens distinguish inherited values from explicit overrides.

---

## 4. Current roadmap status

Always re-read `docs/ROADMAP.md` before starting work. At this refresh:

### M0 — Core: COMPLETE

- settings model / registry / search;
- Basic / Advanced / Insane;
- Global / Account / Chat scopes;
- undo / redo;
- live-preview listeners;
- Android persistence;
- migration seed API.

### M1 — First UI: COMPLETE

- vayGram Settings entry;
- generated categories and controls;
- settings search;
- scope-aware screens;
- modified/recent indicators;
- reset / reset-all;
- live numeric previews;
- embedded visual preview.

### M2 — First visible mods: COMPLETE

- chat bubble radius;
- message spacing;
- chat-list row height;
- avatar size;
- avatar roundness;
- bottom bar height;
- bottom bar labels;
- animation scale;
- AMOLED mode;
- Compact Mode.

### M3 — Presets / overrides: COMPLETE

- persistent named profiles;
- JSON import/export;
- per-account override UI;
- per-chat override UI.

### M4 — Initial Theme Engine: COMPLETE

- semantic token registry;
- searchable palette editor;
- Material You bridge;
- gradients;
- blur / transparency.

### M5 — Branding / build readiness: ALMOST COMPLETE

Done:

- final launcher/adaptive/monochrome vayGram icon assets;
- distinct app/account identity;
- vayGram labels;
- `0.1-dev` versioning;
- own Telegram API credential injection;
- isolated signing configuration;
- Android overlay compile CI;
- reproducible dev APK pipeline;
- checksum/build metadata;
- optional persistent CI signing;
- optional vayGram Firebase injection;
- full non-distributable APK install/launch smoke path.

Still open in ROADMAP:

- vayGram-owned Firebase configuration;
- first real installable/public `vayGram 0.1-dev` APK.

---

## 5. Theme Engine

Theme architecture is semantic-token-first.

```text
vayGram feature/UI
      ↓
VayThemeToken
      ↓
VayThemeTokenRegistry
      ↓
VayThemeBridge
      ↓
Telegram Theme key
```

Current capabilities:

- semantic color tokens;
- persistent per-token overrides;
- searchable ARGB palette editor;
- Material You dynamic colors on Android 12+;
- manual override precedence;
- AMOLED pure-black enforcement for supported dark surfaces;
- reusable gradient spec/editor;
- bottom-navigation gradient target;
- bottom-navigation blur radius / opacity controls;
- live preview and persistence.

Preferred color resolution:

```text
manual vayGram override
        ↓
Material You dynamic color
        ↓
Telegram theme color
        ↓
AMOLED enforcement on supported dark surfaces
```

See `docs/THEME_ENGINE.md`.

---

## 6. Current visible customization

The existing foundation already changes real Telegram UI. Current wired examples include:

- message bubble radius;
- message spacing;
- chat-list density;
- avatar size / shape;
- bottom-navigation size / labels;
- vayGram-controlled motion scaling;
- AMOLED surfaces;
- Compact Mode;
- semantic color palette;
- Material You;
- bottom-navigation gradients;
- bottom-navigation blur / opacity.

The settings UI also has:

- search;
- Basic / Advanced / Insane;
- Global / Account / Chat scopes;
- inheritance;
- live preview;
- undo / redo;
- reset;
- named profiles;
- JSON import/export.

---

## 7. Build identity

Current dev identity:

```text
App: vayGram / vayGram Dev
Version: 0.1-dev
Base package: app.vaygram.messenger
Dev package: app.vaygram.messenger.beta
```

Never reuse official Telegram API credentials.

vayGram reads:

- `VAYGRAM_API_ID`;
- `VAYGRAM_API_HASH`.

Secrets, signing keys and Firebase config must never be committed.

Build docs:

- `docs/BUILDING.md`
- `docs/RELEASE_CHECKLIST.md`

Successful real builds are intended to produce:

```text
vayGram-0.1-dev.apk
vayGram-0.1-dev.sha256
vayGram-0.1-dev-build-info.txt
```

---

## 8. Build / CI state

Important historical build-readiness work is already merged.

Notable merged work includes:

- PR #26 — Android overlay compile CI recreated on current main;
- PR #28 — owner-only issue command to trigger dev APK build;
- PR #29 — fail-fast build preflight/reporting;
- PR #30 — non-distributable full APK install/launch smoke;
- PR #31 — final launcher icon assets;
- PR #32 — robust Base64 secret handling;
- PR #33 — signing diagnostics;
- PR #34 — safe signing-keystore fingerprint diagnostics.

The old stale PR #22 was replaced by merged PR #26 and is closed.

At this refresh there are **no open PRs**.

Owner build-control issue: **#27**

Supported owner commands documented in `docs/BUILDING.md`:

```text
/build-dev-apk
/smoke-dev-apk
```

`/smoke-dev-apk` uses dummy Telegram API values, does not publish the APK, and validates packaging/install/first launch only.

---

## 9. Immediate engineering/release task

The engineering foundation is now ahead of the original first-stage plan.

The nearest real milestone is the **first actually tested 0.1-dev APK**.

Before calling a build public/installable, follow `docs/RELEASE_CHECKLIST.md`.

At this refresh, the remaining external/release items include:

1. configure vayGram-owned Telegram API ID/hash in CI;
2. configure a persistent vayGram dev signing key in CI;
3. create/configure a vayGram Firebase project and `google-services.json`;
4. run the real `dev-apk` workflow successfully;
5. clean-install the result;
6. verify upgrade behavior across dev builds;
7. test login/logout, chats, send/receive, media, notifications;
8. smoke-test Vay Settings, scopes, live preview, palette and gradients;
9. publish APK + SHA-256 + build metadata + GPL source link.

Do **not** mark the first APK milestone complete merely because Gradle produced an APK.

If external secrets are still unavailable, continue feature engineering without inventing them, and use the existing compile/smoke pipelines.

---

## 10. Launcher icon / visual identity

The final Android launcher assets are already in the repository.

Current icon system is deliberately flat/vector-first and **not** based on the Telegram paper plane.

Palette:

```text
background: #0D0D12
main V:     #F4F2FF
accent:     #7C5CFC
```

Foreground geometry lives in:

- `telegram-integration/TMessagesProj/src/main/res/drawable/vaygram_icon_foreground.xml`
- `telegram-integration/TMessagesProj/src/main/res/drawable/vaygram_icon_monochrome.xml`
- `telegram-integration/TMessagesProj/src/main/res/values/vaygram_icon_colors.xml`

The V mark is made from precise geometric diagonal shapes. It has no paper plane, no glass crystal, no neon rim and no 3D effects.

Visual constraints for future assets:

- flat/vector-first;
- Figma/Illustrator feel;
- strong silhouette and negative space;
- no glossy 3D;
- no dramatic glow;
- no fake material/reflection;
- no generic “AI app icon” look;
- no Telegram plane;
- restrained near-black / off-white / violet system.

---

## 11. Welcome-channel graphic direction

The Telegram welcome/devlog graphic should look like a real designer-made brand asset, not an AI illustration.

Preferred composition:

- landscape Telegram post graphic;
- near-black `#0D0D12` background;
- large geometric vayGram V mark, using the same proportions as the launcher icon;
- off-white `#F4F2FF` primary geometry;
- one restrained violet `#7C5CFC` accent;
- a small number of clean modular UI blocks/lines suggesting customization;
- strict grid;
- lots of negative space;
- optionally the word `vayGram` and the slogan if typography renders cleanly;
- no phone mockup required;
- no people;
- no 3D;
- no glass;
- no glow;
- no paper plane;
- no cyberpunk;
- no random particles;
- no “futuristic AI” visual clichés.

If text quality is uncertain, generate the graphic without text and add typography later in Figma.

---

## 12. Telegram development channel

Channel:

https://t.me/vayGram_app

Name:

```text
vayGram • Development
```

Tags:

- #devlog
- #concept
- #vayfeature
- #build
- #changelog
- #poll
- #bug

Current first devlog text being used:

```text
🟣 vayGram Devlog #001

Разработка начинается.
Первый этап — поднять актуальную кодовую базу Telegram Android и отделить будущий код vayGram от основной кодовой базы.

Статус первой стадии:
✅ поднята и закреплена кодовая база Telegram Android
✅ branding vayGram
✅ создан VayCore
✅ создан VaySettingsRegistry
✅ сделан отдельный экран vayGram Settings
✅ добавлен поиск настроек
✅ добавлены Basic / Advanced / Insane режимы
✅ добавлены Global / Account / Chat scopes
✅ добавлены первые реальные UI-параметры
✅ запущен Theme Engine
✅ добавлен live preview настроек
✅ добавлены профили и импорт/экспорт настроек
✅ добавлен Palette Editor
✅ добавлена поддержка AMOLED и Compact Mode
✅ подготовлена отдельная система сборки vayGram
✅ отделены package id, API credentials и dev signing

Следующий крупный рубеж — первая нормально протестированная vayGram 0.1-dev APK.

Customize everything. Complicate nothing.

#devlog #vaygram
```

---

## 13. Signature long-term feature — Vay Profile Studio

A major future differentiator is a block-based free-form profile editor.

Possible blocks:

- avatar;
- name;
- username;
- bio;
- phone;
- birthday;
- emoji status;
- channels/groups;
- links;
- music;
- quote;
- badges;
- gifts;
- media;
- mutual chats;
- custom text/image;
- separators.

Modes:

- Grid Layout for normal users;
- Free Layout for power users.

Potential controls:

- drag/drop;
- resize;
- arbitrary positions;
- z-index;
- alignment;
- opacity;
- custom background;
- blur;
- parallax;
- animations;
- avatar shapes/effects.

Cross-vayGram sharing is opt-in:

- Telegram profile data stays Telegram data;
- user explicitly publishes a Vay Profile;
- vayGram stores only the custom layout separately;
- other vayGram clients can fetch it;
- official Telegram clients show the normal profile.

Do not upload private settings/data without explicit opt-in.

Do not derail the 0.1-dev foundation to build this prematurely.

---

## 14. Longer-term feature families

Planned areas include:

- Chat Studio;
- Profile Studio;
- Layout Engine;
- Gesture Studio;
- Motion Engine;
- icon packs;
- Home/Header/Bottom Bar editors;
- message action customization;
- media viewer;
- Vay Player;
- voice-message controls;
- Download Manager;
- Storage Manager;
- stickers / emoji / GIF controls;
- reactions;
- translation;
- search extensions;
- folders;
- multi-account controls;
- Notification Studio;
- Quick Actions / command palette;
- automation by time/battery/system mode/account;
- accessibility;
- performance profiles;
- app lock / privacy UI;
- local encrypted settings vault;
- optional VayCloud;
- Vay Plugins later;
- Vay Labs;
- safe mode / crash recovery;
- backup / migration / versioning;
- developer tools.

Insane mode may later support a declarative VayStyles language, e.g.:

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

## 15. Project constraints

### Telegram upstream

- use a pinned upstream revision for reproducibility;
- keep Telegram changes minimal;
- isolate vayGram behavior behind adapters/bridges;
- expect upstream APIs/UI to change.

### Licensing

Telegram Android is GPL-based. Redistribution must preserve GPL compliance and source availability.

### Telegram API

Use vayGram-owned Telegram API credentials.

Do not casually add fake/ghost read state, fake typing/online behavior or self-destruct-content bypass.

### Privacy

- local by default;
- no private settings/profile upload without explicit opt-in;
- VayCloud remains optional.

---

## 16. Expected development workflow for a new ChatGPT chat

1. Read this file.
2. Read `docs/ROADMAP.md`.
3. Read `docs/BUILDING.md` and `docs/RELEASE_CHECKLIST.md` for release/build tasks.
4. Inspect latest `main`.
5. Inspect open PRs and recent CI.
6. Do not blindly reuse stale branches.
7. Create a focused branch.
8. Keep commits cohesive.
9. Keep Telegram upstream patches minimal.
10. Run relevant checks:
    - core-check;
    - upstream-overlay-check;
    - Android compile check for integration changes;
    - build/install smoke when appropriate.
11. Fix CI before merge.
12. Open PR.
13. Merge only after current checks pass.
14. Update ROADMAP/docs when milestone state changes.

Never claim an APK exists until it has actually been built.

Never expose or commit secrets.

---

## 17. Communication style

The project owner communicates in Russian and prefers concise, direct status updates.

Good format:

```text
Сделал Stage N.

✅ ...
✅ ...
✅ CI

PR #...
Следующее: ...
```

Do not become overly formal because the user swears or writes briefly.

---

## 18. Fresh-chat bootstrap prompt

Paste this into a new ChatGPT chat:

```text
Продолжаем разработку vayGram.

Репозиторий:
https://github.com/hanxww/vayGram

Сначала прочитай:
docs/CHAT_HANDOFF.md
docs/ROADMAP.md
docs/THEME_ENGINE.md
docs/BUILDING.md
docs/RELEASE_CHECKLIST.md
telegram-integration/UPSTREAM

Потом проверь:
- latest main;
- открытые PR;
- последние CI runs;
- последние merged PR;
- текущий release checklist.

Не пересказывай мне весь репозиторий — после проверки сразу продолжай разработку с ближайшей реальной незакрытой задачи.

Сохраняй архитектуру:
VayCore / Android adapters / app.vaygram Telegram overlay / минимальные upstream patches.

Перед merge обязательно прогоняй релевантные проверки. Integration-изменения должны проходить Android compile check.

Не коммить секреты и не используй официальные Telegram API credentials.

Текущий ближайший milestone: первая реально собранная и протестированная vayGram 0.1-dev APK. Если release secrets ещё не настроены, не выдумывай их — продолжай инженерные задачи, которые можно корректно сделать без секретов.

Для визуалов соблюдай текущую айдентику: плоский геометрический V, #0D0D12 / #F4F2FF / #7C5CFC, без Telegram-самолётика, 3D, glass/neon и AI-looking эффектов.
```

This prompt + this file + the repository should be sufficient to continue development without access to the old conversation.
