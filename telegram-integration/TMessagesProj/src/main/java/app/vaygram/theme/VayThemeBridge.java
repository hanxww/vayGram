package app.vaygram.theme;

import android.util.SparseIntArray;

import org.telegram.ui.ActionBar.Theme;

import java.util.Map;

import app.vaygram.core.theme.VayThemePalette;
import app.vaygram.core.theme.VayThemeToken;
import app.vaygram.core.theme.VayThemeTokens;

public final class VayThemeBridge {
    private static final VayThemeToken[] MAPPED_TOKENS = {
            VayThemeTokens.SURFACE_PRIMARY,
            VayThemeTokens.SURFACE_SECONDARY,
            VayThemeTokens.SURFACE_ELEVATED,
            VayThemeTokens.TEXT_PRIMARY,
            VayThemeTokens.TEXT_SECONDARY,
            VayThemeTokens.ACCENT_PRIMARY,
            VayThemeTokens.DIVIDER,
            VayThemeTokens.CHAT_BUBBLE_IN,
            VayThemeTokens.CHAT_BUBBLE_OUT,
            VayThemeTokens.CHAT_TEXT_IN,
            VayThemeTokens.CHAT_TEXT_OUT,
            VayThemeTokens.NAV_SURFACE,
            VayThemeTokens.NAV_ICON_ACTIVE,
            VayThemeTokens.NAV_ICON_INACTIVE
    };

    private static volatile SparseIntArray colorOverrides = new SparseIntArray();
    private static volatile SparseIntArray materialLightColors = new SparseIntArray();
    private static volatile SparseIntArray materialDarkColors = new SparseIntArray();
    private static volatile boolean materialYouEnabled;

    private VayThemeBridge() {}

    public static int color(VayThemeToken token) {
        int key = telegramColorKey(token);
        return applyColorOverride(key, Theme.getColor(key));
    }

    public static int color(VayThemeToken token, Theme.ResourcesProvider resourcesProvider) {
        int key = telegramColorKey(token);
        return applyColorOverride(key, Theme.getColor(key, resourcesProvider));
    }

    public static void refreshPalette(VayThemePalette palette) {
        SparseIntArray next = new SparseIntArray();
        Map<String, Integer> overrides = palette.snapshotColors();
        for (VayThemeToken token : MAPPED_TOKENS) {
            Integer value = overrides.get(token.getId());
            if (value != null) {
                next.put(telegramColorKey(token), value);
            }
        }
        colorOverrides = next;
    }

    public static void refreshMaterialYou(
            Map<String, Integer> lightColors,
            Map<String, Integer> darkColors,
            boolean enabled
    ) {
        materialLightColors = mapTokenColors(lightColors);
        materialDarkColors = mapTokenColors(darkColors);
        materialYouEnabled = enabled
                && materialLightColors.size() > 0
                && materialDarkColors.size() > 0;
    }

    public static boolean isMaterialYouEnabled() {
        return materialYouEnabled;
    }

    public static int applyColorOverride(int telegramColorKey, int fallbackColor) {
        SparseIntArray manual = colorOverrides;
        int manualIndex = manual.indexOfKey(telegramColorKey);
        if (manualIndex >= 0) {
            return manual.valueAt(manualIndex);
        }

        if (materialYouEnabled) {
            SparseIntArray material = Theme.isCurrentThemeDark()
                    ? materialDarkColors
                    : materialLightColors;
            int materialIndex = material.indexOfKey(telegramColorKey);
            if (materialIndex >= 0) {
                return material.valueAt(materialIndex);
            }
        }

        return fallbackColor;
    }

    private static SparseIntArray mapTokenColors(Map<String, Integer> colors) {
        SparseIntArray result = new SparseIntArray();
        if (colors == null || colors.isEmpty()) {
            return result;
        }

        for (VayThemeToken token : MAPPED_TOKENS) {
            Integer color = colors.get(token.getId());
            if (color != null) {
                result.put(telegramColorKey(token), color);
            }
        }
        return result;
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
            return Theme.key_glass_targetMainTabs;
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
