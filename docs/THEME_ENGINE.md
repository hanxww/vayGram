# vayGram Theme Engine

The Theme Engine uses semantic vayGram tokens instead of letting features depend directly on Telegram theme-key names.

## Token flow

```text
Vay UI / feature
      |
      v
VayThemeToken
      |
      v
VayThemeTokenRegistry
      |
      v
VayThemeBridge
      |
      v
Telegram Theme key
```

This creates a stable vayGram-facing layer while Telegram's internal theme implementation can continue to change upstream.

## Initial token families

The first registry covers primary, secondary and elevated surfaces; primary and secondary text; accent and divider colors; incoming/outgoing chat bubbles and text; and bottom-navigation surface plus active/inactive icon colors.

Token IDs are stable strings such as `surface.primary`, `chat.bubble.out` and `navigation.icon.active`. UI code should prefer tokens over hard-coded Telegram theme keys when a semantic token exists.

## Palette overrides

The first editor is implemented.

Each semantic color token can optionally have a vayGram override. Overrides are stored by token ID, persisted locally, and cached as Telegram theme-key overrides for fast rendering.

```text
vayGram token override
        |
        v
mapped Telegram theme key
        |
        v
Telegram theme fallback
```

The palette editor supports search, per-token reset, full-palette reset, and live ARGB preview. Cancelling restores the previous override without persisting the preview.

## Material You

Material You is implemented as a generated base palette on Android 12+.

The resolver reads Android dynamic system colors and maps them into the same semantic vayGram tokens used by the manual palette editor. Both light and dark token maps are cached, so switching Telegram between light and dark themes does not require rebuilding the palette.

Resolution order is:

```text
manual vayGram token override
        |
        v
Material You dynamic token color
        |
        v
Telegram theme color
```

Material You remains opt-in. Manual token overrides always win, which lets a user follow the system palette while still replacing selected colors. AMOLED surface enforcement runs after token resolution for supported dark surfaces.

The palette editor shows when an effective color comes from Material You and can re-read the current Android dynamic colors without restarting the client.

## Next steps

Gradients, transparency and blur can now build on the same semantic theme model without coupling feature code to Telegram internals.
