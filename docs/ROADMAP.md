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
- [ ] final vayGram icon assets
- [x] distinct application id and account type
- [x] vayGram application labels
- [x] dev version suffix
- [ ] own Firebase configuration
- [x] own Telegram API credential injection (secrets not committed)
- [x] isolated development signing configuration
- [ ] first installable 0.1-dev APK
