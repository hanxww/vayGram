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
