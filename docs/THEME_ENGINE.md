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

## Next steps

The palette editor will store optional color overrides by token ID. The bridge will then resolve an override first and fall back to the mapped Telegram color when no vayGram override exists. Later additions can expose Material You, gradients, opacity and other visual tokens without coupling feature code to Telegram internals.
