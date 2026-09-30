# vayGram

**vayGram** is an experimental Telegram Android client focused on extreme customization without sacrificing everyday usability.

> Customize everything. Complicate nothing.

## Status

Early development — `0.1-dev`.

The first milestone is the vayGram customization foundation:

- VayCore settings engine
- Basic / Advanced / Insane visibility levels
- Global / account / chat scopes
- search and categories
- undo / redo and change history
- presets and live-preview foundations
- first appearance hooks for Telegram Android

Development channel: https://t.me/vayGram_app

Continuing development in a fresh ChatGPT chat? Start with [docs/CHAT_HANDOFF.md](docs/CHAT_HANDOFF.md). It contains the current architecture, implemented milestones, release blockers, open-work notes, and a copy-paste continuation prompt.

## Architecture rule

vayGram-specific code should stay isolated from Telegram upstream code whenever possible. Telegram classes should receive small bridge hooks while features live inside VayCore/VayUI modules. This is intended to make upstream Telegram updates much easier to merge.

## License

The repository is being prepared for integration with Telegram Android. Licensing files and notices will be finalized together with the upstream source import before redistribution.
