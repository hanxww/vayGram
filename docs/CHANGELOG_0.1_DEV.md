# vayGram 0.1-dev changelog

This is the rolling changelog for the first public development build. It describes vayGram-owned behavior; Telegram upstream remains based on Android 12.10.5 / 7105.

## Customization

- Vay Settings with Basic / Advanced / Insane complexity levels.
- Global, account and chat scopes with inheritance.
- Search, recent changes, reset, Undo / Redo and live preview.
- Chat bubble radius and message spacing.
- Chat-list row height, avatar size and avatar roundness.
- Bottom-navigation height and labels.
- vayGram-controlled animation scale.
- Compact mode and AMOLED surfaces.

## Theme Engine

- Semantic vayGram theme tokens mapped into Telegram.
- Palette editor with persistent overrides.
- Material You dynamic colors on Android 12+.
- Bottom-navigation gradients.
- Glass blur radius and opacity controls.

## Profiles and onboarding

- First-run guided onboarding with an interactive spotlight.
- Vay Profile Studio foundation and 20 block settings.
- Layout-safe live avatar effects on the native own-profile screen.
- Profile Studio compatibility guardrails to avoid breaking Telegram's structural profile layout.

## Reliability and portability

- Reproducible signed dev APK pipeline and install/launch smoke workflow.
- Sanitized diagnostics and recovery screen.
- Portable vayGram backup/restore for global/current-account settings, saved profiles, palette overrides and navigation gradient.
- Backup deliberately excludes Telegram sessions, chats, messages, phone numbers, account IDs and per-chat overrides.
- About vayGram screen with client/base version, source repository and development channel.

## Languages

vayGram-owned UI currently ships English, Russian and Estonian resources.

## Before public #001

Real-device validation is still required for login/logout, messaging, media, notifications, upgrade compatibility, bottom-navigation insets and the stabilized Profile Studio.
