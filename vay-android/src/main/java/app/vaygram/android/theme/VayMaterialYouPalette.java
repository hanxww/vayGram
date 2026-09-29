package app.vaygram.android.theme;

import android.content.Context;
import android.os.Build;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import app.vaygram.core.theme.VayThemeToken;
import app.vaygram.core.theme.VayThemeTokens;

public final class VayMaterialYouPalette {
    private VayMaterialYouPalette() {}

    public static final class Snapshot {
        private static final Snapshot UNSUPPORTED = new Snapshot(
                false,
                Collections.emptyMap(),
                Collections.emptyMap()
        );

        private final boolean supported;
        private final Map<String, Integer> lightColors;
        private final Map<String, Integer> darkColors;

        private Snapshot(
                boolean supported,
                Map<String, Integer> lightColors,
                Map<String, Integer> darkColors
        ) {
            this.supported = supported;
            this.lightColors = Collections.unmodifiableMap(
                    new LinkedHashMap<>(lightColors)
            );
            this.darkColors = Collections.unmodifiableMap(
                    new LinkedHashMap<>(darkColors)
            );
        }

        public boolean isSupported() {
            return supported;
        }

        public Map<String, Integer> getLightColors() {
            return lightColors;
        }

        public Map<String, Integer> getDarkColors() {
            return darkColors;
        }
    }

    public static Snapshot resolve(Context context) {
        if (context == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return Snapshot.UNSUPPORTED;
        }

        LinkedHashMap<String, Integer> light = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> dark = new LinkedHashMap<>();

        put(context, light, VayThemeTokens.SURFACE_PRIMARY, "system_neutral1_10");
        put(context, light, VayThemeTokens.SURFACE_SECONDARY, "system_neutral2_50");
        put(context, light, VayThemeTokens.SURFACE_ELEVATED, "system_neutral1_0");
        put(context, light, VayThemeTokens.TEXT_PRIMARY, "system_neutral1_900");
        put(context, light, VayThemeTokens.TEXT_SECONDARY, "system_neutral2_700");
        put(context, light, VayThemeTokens.ACCENT_PRIMARY, "system_accent1_600");
        put(context, light, VayThemeTokens.DIVIDER, "system_neutral2_200");
        put(context, light, VayThemeTokens.CHAT_BUBBLE_IN, "system_neutral2_50");
        put(context, light, VayThemeTokens.CHAT_BUBBLE_OUT, "system_accent2_100");
        put(context, light, VayThemeTokens.CHAT_TEXT_IN, "system_neutral1_900");
        put(context, light, VayThemeTokens.CHAT_TEXT_OUT, "system_accent2_900");
        put(context, light, VayThemeTokens.NAV_SURFACE, "system_neutral1_0");
        put(context, light, VayThemeTokens.NAV_ICON_ACTIVE, "system_accent1_600");
        put(context, light, VayThemeTokens.NAV_ICON_INACTIVE, "system_neutral2_600");

        put(context, dark, VayThemeTokens.SURFACE_PRIMARY, "system_neutral1_900");
        put(context, dark, VayThemeTokens.SURFACE_SECONDARY, "system_neutral2_800");
        put(context, dark, VayThemeTokens.SURFACE_ELEVATED, "system_neutral1_800");
        put(context, dark, VayThemeTokens.TEXT_PRIMARY, "system_neutral1_100");
        put(context, dark, VayThemeTokens.TEXT_SECONDARY, "system_neutral2_300");
        put(context, dark, VayThemeTokens.ACCENT_PRIMARY, "system_accent1_200");
        put(context, dark, VayThemeTokens.DIVIDER, "system_neutral2_700");
        put(context, dark, VayThemeTokens.CHAT_BUBBLE_IN, "system_neutral2_800");
        put(context, dark, VayThemeTokens.CHAT_BUBBLE_OUT, "system_accent2_700");
        put(context, dark, VayThemeTokens.CHAT_TEXT_IN, "system_neutral1_100");
        put(context, dark, VayThemeTokens.CHAT_TEXT_OUT, "system_accent2_100");
        put(context, dark, VayThemeTokens.NAV_SURFACE, "system_neutral1_900");
        put(context, dark, VayThemeTokens.NAV_ICON_ACTIVE, "system_accent1_200");
        put(context, dark, VayThemeTokens.NAV_ICON_INACTIVE, "system_neutral2_400");

        boolean supported = !light.isEmpty() && !dark.isEmpty();
        return supported ? new Snapshot(true, light, dark) : Snapshot.UNSUPPORTED;
    }

    private static void put(
            Context context,
            Map<String, Integer> target,
            VayThemeToken token,
            String androidColorName
    ) {
        int resourceId = context.getResources().getIdentifier(
                androidColorName,
                "color",
                "android"
        );
        if (resourceId == 0) {
            return;
        }
        target.put(token.getId(), context.getColor(resourceId));
    }
}
