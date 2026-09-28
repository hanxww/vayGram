package app.vaygram.theme;

import org.telegram.ui.ActionBar.Theme;

import app.vaygram.core.theme.VayThemeToken;
import app.vaygram.core.theme.VayThemeTokens;

public final class VayThemeBridge {
    private VayThemeBridge() {}

    public static int color(VayThemeToken token) {
        return Theme.getColor(telegramColorKey(token));
    }

    public static int color(VayThemeToken token, Theme.ResourcesProvider resourcesProvider) {
        return Theme.getColor(telegramColorKey(token), resourcesProvider);
    }

    public static int telegramColorKey(VayThemeToken token) {
        if (token == VayThemeTokens.SURFACE_PRIMARY) {
            return Theme.key_windowBackgroundWhite;
        }
        if (token == VayThemeTokens.SURFACE_SECONDARY) {
            return Theme.key_windowBackgroundGray;
        }
        if (token == VayThemeTokens.SURFACE_ELEVATED) {
            return Theme.key_dialogBackground;
        }
        if (token == VayThemeTokens.TEXT_PRIMARY) {
            return Theme.key_windowBackgroundWhiteBlackText;
        }
        if (token == VayThemeTokens.TEXT_SECONDARY) {
            return Theme.key_windowBackgroundWhiteGrayText;
        }
        if (token == VayThemeTokens.ACCENT_PRIMARY) {
            return Theme.key_featuredStickers_addButton;
        }
        if (token == VayThemeTokens.DIVIDER) {
            return Theme.key_divider;
        }
        if (token == VayThemeTokens.CHAT_BUBBLE_IN) {
            return Theme.key_chat_inBubble;
        }
        if (token == VayThemeTokens.CHAT_BUBBLE_OUT) {
            return Theme.key_chat_outBubble;
        }
        if (token == VayThemeTokens.CHAT_TEXT_IN) {
            return Theme.key_chat_messageTextIn;
        }
        if (token == VayThemeTokens.CHAT_TEXT_OUT) {
            return Theme.key_chat_messageTextOut;
        }
        if (token == VayThemeTokens.NAV_SURFACE) {
            return Theme.key_windowBackgroundWhite;
        }
        if (token == VayThemeTokens.NAV_ICON_ACTIVE) {
            return Theme.key_glass_tabSelected;
        }
        if (token == VayThemeTokens.NAV_ICON_INACTIVE) {
            return Theme.key_glass_tabUnselected;
        }
        throw new IllegalArgumentException("No Telegram mapping for vayGram theme token: " + token.getId());
    }
}
