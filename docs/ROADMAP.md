# vayGram 0.1-dev roadmap

## M0 — Core
- [x] setting definition model
- [x] registry
- [x] search
- [x] Basic / Advanced / Insane levels
- [x] Global / Account / Chat scopes
- [x] history + undo/redo
- [x] listeners for live preview
- [x] Android persistence adapter
- [x] numeric range metadata for generated controls
- [x] migration seed API

## M1 — First UI
- [x] Vay Settings entry in Telegram settings
- [x] generated categories
- [x] search
- [x] Basic / Advanced / Insane switcher
- [x] boolean controls
- [x] generated numeric controls
- [x] recently changed
- [x] changed-value indicators
- [x] per-setting reset
- [x] reset all customized values
- [x] Undo / Redo controls
- [x] scope-aware settings screen foundation
- [x] live numeric preview with cancel/apply
- [x] embedded visual preview host — live chat/list/navigation preview

## M2 — First visible mods
- [x] chat bubble radius — bidirectional sync with Telegram renderer
- [x] chat message spacing
- [x] chat list row height
- [x] avatar size
- [x] avatar roundness
- [x] bottom bar height / labels
- [x] animation scale — wired to vayGram-controlled main navigation motion
- [x] AMOLED surface mode — pure-black supported surfaces in dark themes
- [x] compact mode — non-destructive density overlay

## M3 — Presets
- [x] settings profiles — persistent save/apply/delete UI
- [x] import/export
- [x] per-account overrides UI — inherited values + explicit overrides
- [x] per-chat overrides UI — chat menu entry + inherited values

## M4 — Theme Engine
- [x] token registry — semantic tokens + Telegram mapping bridge
- [x] palette editor — searchable live token color overrides
- [x] Material You bridge — Android 12+ dynamic colors with manual override precedence
- [x] gradients — reusable spec/editor + live bottom-navigation gradient target
- [x] blur/transparency controls — live bottom-navigation blur radius and opacity

## M5 — Branding and builds
- [x] final vayGram icon assets
- [x] distinct application id and account type
- [x] vayGram application labels
- [x] dev version suffix
- [x] own Firebase configuration
- [x] own Telegram API credential injection (secrets not committed)
- [x] isolated development signing configuration
- [x] Android overlay compile CI
- [x] reproducible dev APK pipeline + checksum/build metadata
- [x] persistent CI signing/Firebase secret wiring
- [x] first full 0.1-dev APK built by CI

## M6 — First-run experience and device validation
- [x] Vay Profile Studio foundation and own-profile entry
- [x] first-run guided onboarding
- [x] contextual first-launch coach on the real chat-list UI
- [x] spotlight targets remain sharp and directly clickable
- [x] spotlight coach overlay with a sharp interactive target cutout
- [x] replay onboarding from Vay Settings
- [x] keep onboarding Continue/Back/Skip controls reachable on compact-height devices
- [x] persist Basic / Advanced / Insane immediately when selected
- [x] place vayGram Settings as the first standalone settings block
- [ ] clean install on a real Android device
- [ ] upgrade over the previous vayGram dev build
- [ ] Telegram login/logout smoke
- [ ] chats and message send/receive smoke
- [ ] media upload/download smoke
- [ ] Firebase notification smoke
- [ ] Vay Settings / Theme Engine / scopes smoke
- [ ] Profile Studio smoke


## M7 — Profile Studio block pack
- [x] avatar visibility block
- [x] display name block
- [x] status / last-seen block
- [x] phone block with Telegram privacy boundary
- [x] birthday block
- [x] emoji-status block
- [x] personal-channel block
- [x] groups block
- [x] links block
- [x] music block
- [x] quote block
- [x] badges block
- [x] gifts block
- [x] media block
- [x] mutual-chats block
- [x] custom-text block
- [x] custom-image block
- [x] separator block
- [x] spacer block
- [x] buttons block
- [x] account/global persistence and inheritance for all blocks
- [x] live Profile Studio preview for the block pack
- [x] EN / RU / ET UI strings

## M8 — Real-device stabilization

- [x] preserve Telegram's native profile header geometry by default
- [x] keep Vay Profile avatar ring opt-in instead of changing fresh installs
- [x] restrict current live Profile Studio integration to layout-safe avatar effects
- [x] add an in-app compatibility explanation for preview-only Profile Studio blocks
- [x] add overlay regression checks preventing Vay block defaults from hiding native Telegram profile rows
- [ ] verify the stabilized profile on a real Android device
- [ ] verify bottom-navigation/content insets across profile, gifts, media and settings surfaces
- [ ] complete regression pass for Telegram core flows after customization changes

## M9 — Diagnostics and recovery

- [x] in-app diagnostics screen
- [x] sanitized copyable bug-report summary
- [x] build/package/Android/base-Telegram metadata
- [x] current-scope customization and theme state summary
- [x] confirmed current-scope vayGram reset without touching Telegram data
- [x] confirmed palette/gradient recovery action
- [x] EN / RU / ET diagnostics UI
- [ ] add crash-session diagnostics after a real crash sample is available
- [ ] add per-surface visual regression coverage for bottom-navigation overlap


## M10 — Portable backup and publication metadata

- [x] privacy-safe portable vayGram backup schema
- [x] global settings backup and restore
- [x] current-account settings backup and restore without exporting Telegram account IDs
- [x] saved profile backup and restore
- [x] theme palette backup and restore
- [x] navigation-gradient backup and restore
- [x] explicit exclusion of Telegram sessions, chats, messages, phone numbers and per-chat IDs
- [x] copy/share/restore backup UI
- [x] EN / RU / ET backup UI
- [x] About vayGram screen
- [x] pinned Telegram-base/build metadata in About
- [x] source repository and development-channel links
- [x] file-based Android Storage Access Framework import/export
- [ ] verify SAF file import/export on a real Android device


## M11 — Safe Mode recovery

- [x] persistent Safe Mode flag stored outside the normal settings engine
- [x] one-tap Safe Mode toggle in Diagnostics and recovery
- [x] bypass vayGram theme palette and Material You overrides without deleting them
- [x] bypass gradients, glass effects, AMOLED enforcement and custom bottom-navigation metrics
- [x] bypass chat/list/avatar appearance customizations while preserving stored values
- [x] disable live Profile Studio avatar effects while Safe Mode is active
- [x] restore saved customization immediately when Safe Mode is disabled
- [x] expose Safe Mode state in sanitized diagnostics
- [ ] automatic crash-loop suggestion after crash-session diagnostics exists


## M12 — Main-tab inset stabilization

- [x] re-dispatch window insets when vayGram bottom-navigation height changes live
- [x] resize the bottom fade region with the configured navigation height
- [x] refresh Profile media/gifts/button offsets after navigation-height changes
- [x] refresh Settings bottom padding after navigation-height changes
- [x] refresh Contacts list/floating-button offsets after navigation-height changes
- [x] refresh Calls list/floating-button offsets after navigation-height changes
- [ ] real-device regression pass across profile gifts/media/settings/contacts/calls


## M12 — Chat List Studio typography

- [x] chat title text size control
- [x] message-preview text size control
- [x] Global / Account scope persistence
- [x] Safe Mode fallback to Telegram-compatible defaults
- [x] live DialogCell renderer hooks
- [x] EN / RU / ET UI strings
- [ ] verify typography extremes on a real Android device

