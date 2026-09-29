package app.vaygram.core.demo;

import app.vaygram.core.settings.InMemoryVaySettingsStore;
import app.vaygram.core.settings.VayDefaults;
import app.vaygram.core.settings.VayScopeKey;
import app.vaygram.core.settings.VaySetting;
import app.vaygram.core.settings.VaySettingScope;
import app.vaygram.core.settings.VaySettingsEngine;
import app.vaygram.core.settings.VaySettingsPreset;
import app.vaygram.core.settings.VaySettingsRegistry;
import app.vaygram.core.settings.VayVisibilityLevel;
import app.vaygram.core.theme.VayGradientSpec;
import app.vaygram.core.theme.VayThemeGradients;
import app.vaygram.core.theme.VayThemePalette;
import app.vaygram.core.theme.VayThemeTokenRegistry;
import app.vaygram.core.theme.VayThemeTokens;

public final class Demo {
    public static void main(String[] args) {
        VaySettingsRegistry registry = VayDefaults.createRegistry();
        VaySettingsEngine engine = new VaySettingsEngine(registry, new InMemoryVaySettingsStore());
        VayThemeTokenRegistry themeTokens = VayThemeTokens.createRegistry();
        if (themeTokens.size() < 10
                || themeTokens.find("chat.bubble.out") != VayThemeTokens.CHAT_BUBBLE_OUT
                || themeTokens.search("navigation").size() < 3) {
            throw new IllegalStateException("Theme token registry smoke test failed");
        }

        VayThemePalette palette = new VayThemePalette(themeTokens);
        palette.setColor(VayThemeTokens.CHAT_BUBBLE_OUT, 0xff7c5cff);
        if (!palette.hasColorOverride(VayThemeTokens.CHAT_BUBBLE_OUT)
                || palette.getColorOverride(VayThemeTokens.CHAT_BUBBLE_OUT) != 0xff7c5cff
                || palette.countOverrides() != 1) {
            throw new IllegalStateException("Theme palette override smoke test failed");
        }
        palette.reset(VayThemeTokens.CHAT_BUBBLE_OUT);
        if (palette.countOverrides() != 0) {
            throw new IllegalStateException("Theme palette reset smoke test failed");
        }

        VayGradientSpec gradient = VayThemeGradients.navigationDefaults(
                0xff101014,
                0xff7c5cff
        );
        if (gradient.isEnabled()
                || gradient.withAngleDegrees(73).getAngleDegrees() != 90
                || gradient.withStartColor(0xff111111).getEndColor() != 0xff7c5cff
                || !VayThemeGradients.NAVIGATION_BOTTOM.equals(gradient.getTargetId())) {
            throw new IllegalStateException("Theme gradient smoke test failed");
        }

        engine.seedIfAbsent(VayDefaults.CHAT_BUBBLE_RADIUS, VayScopeKey.GLOBAL, 17f);
        if (!engine.hasStoredValue(VayDefaults.CHAT_BUBBLE_RADIUS, VayScopeKey.GLOBAL)) {
            throw new IllegalStateException("seedIfAbsent failed");
        }

        engine.addListener(change -> System.out.println("changed: " + change));
        System.out.println("vayGram settings registered: " + registry.size());
        System.out.println("default bubble radius: " + engine.get(VayDefaults.CHAT_BUBBLE_RADIUS));
        if (engine.get(VayDefaults.NAV_HEIGHT) != 56) {
            throw new IllegalStateException("Bottom navigation default must match Telegram's 56dp baseline");
        }
        if (engine.get(VayDefaults.THEME_MATERIAL_YOU)) {
            throw new IllegalStateException("Material You must remain opt-in by default");
        }
        if (registry.find("theme.material_you") != VayDefaults.THEME_MATERIAL_YOU) {
            throw new IllegalStateException("Material You setting is missing from the registry");
        }
        if (engine.get(VayDefaults.GLASS_OPACITY) != 1f) {
            throw new IllegalStateException("Glass opacity must default to fully opaque");
        }
        engine.set(VayDefaults.GLASS_OPACITY, 0.1f);
        if (engine.get(VayDefaults.GLASS_OPACITY) != 0.20f) {
            throw new IllegalStateException("Glass opacity range normalization failed");
        }
        engine.reset(VayDefaults.GLASS_OPACITY, VayScopeKey.GLOBAL);

        engine.set(VayDefaults.CHAT_BUBBLE_RADIUS, 12f);
        System.out.println("new bubble radius: " + engine.get(VayDefaults.CHAT_BUBBLE_RADIUS));

        VayScopeKey account = new VayScopeKey(VaySettingScope.ACCOUNT, "account-1");
        VayScopeKey chat = new VayScopeKey(VaySettingScope.CHAT, "account-1:123456");

        engine.set(VayDefaults.CHAT_BUBBLE_RADIUS, account, 7f);
        if (engine.getResolved(VayDefaults.CHAT_BUBBLE_RADIUS, chat, account) != 7f) {
            throw new IllegalStateException("Chat scope did not inherit account override");
        }

        engine.set(VayDefaults.CHAT_BUBBLE_RADIUS, chat, 4f);
        if (engine.getResolved(VayDefaults.CHAT_BUBBLE_RADIUS, chat, account) != 4f) {
            throw new IllegalStateException("Chat override did not win over account override");
        }
        if (engine.countCustomized(chat) != 1) {
            throw new IllegalStateException("Explicit chat override was not counted");
        }
        System.out.println("chat override: " + engine.getResolved(VayDefaults.CHAT_BUBBLE_RADIUS, chat, account));

        System.out.println("search 'аватар' in Advanced:");
        for (VaySetting<?> setting : registry.search("аватар", VayVisibilityLevel.ADVANCED)) {
            System.out.println(" - " + setting.getId());
        }

        engine.undo();
        System.out.println("chat after undo: " + engine.getResolved(VayDefaults.CHAT_BUBBLE_RADIUS, chat, account));
        engine.redo();
        System.out.println("chat after redo: " + engine.getResolved(VayDefaults.CHAT_BUBBLE_RADIUS, chat, account));

        engine.clearStoredValue(VayDefaults.CHAT_BUBBLE_RADIUS, chat);
        if (engine.hasStoredValue(VayDefaults.CHAT_BUBBLE_RADIUS, chat)
                || engine.getResolved(VayDefaults.CHAT_BUBBLE_RADIUS, chat, account) != 7f) {
            throw new IllegalStateException("Clearing chat override did not restore account inheritance");
        }

        VaySettingsPreset preset = engine.capturePreset("demo", VayScopeKey.GLOBAL, true);
        engine.set(VayDefaults.CHAT_BUBBLE_RADIUS, 30f);
        int applied = engine.applyPreset(preset, VayScopeKey.GLOBAL);
        if (applied != 1 || engine.get(VayDefaults.CHAT_BUBBLE_RADIUS) != 12f) {
            throw new IllegalStateException("Preset round-trip smoke test failed");
        }
        System.out.println("preset applied: " + applied + " setting");
        float beforePreview = engine.get(VayDefaults.CHAT_BUBBLE_RADIUS);
        engine.preview(VayDefaults.CHAT_BUBBLE_RADIUS, VayScopeKey.GLOBAL, 8f);
        if (engine.get(VayDefaults.CHAT_BUBBLE_RADIUS) != 8f) {
            throw new IllegalStateException("Live preview did not apply");
        }
        engine.cancelPreview(VayDefaults.CHAT_BUBBLE_RADIUS, VayScopeKey.GLOBAL, beforePreview);
        if (engine.get(VayDefaults.CHAT_BUBBLE_RADIUS) != beforePreview) {
            throw new IllegalStateException("Live preview cancel failed");
        }

        engine.preview(VayDefaults.CHAT_BUBBLE_RADIUS, VayScopeKey.GLOBAL, 9f);
        engine.commitPreview(VayDefaults.CHAT_BUBBLE_RADIUS, VayScopeKey.GLOBAL, beforePreview, 9f);
        if (engine.get(VayDefaults.CHAT_BUBBLE_RADIUS) != 9f) {
            throw new IllegalStateException("Live preview commit failed");
        }
        System.out.println("live preview smoke test passed");

        engine.set(VayDefaults.THEME_AMOLED, true);
        if (!engine.get(VayDefaults.THEME_AMOLED)) {
            throw new IllegalStateException("AMOLED setting did not persist");
        }

        engine.set(VayDefaults.DIALOG_ROW_HEIGHT, 82);
        engine.set(VayDefaults.NAV_HEIGHT, 74);
        engine.set(VayDefaults.COMPACT_MODE, true);
        if (!engine.get(VayDefaults.COMPACT_MODE)
                || engine.get(VayDefaults.DIALOG_ROW_HEIGHT) != 82
                || engine.get(VayDefaults.NAV_HEIGHT) != 74) {
            throw new IllegalStateException("Compact mode must preserve underlying custom values");
        }
        System.out.println("modified global settings: " + engine.countModified(VayScopeKey.GLOBAL));
        System.out.println("amoled modified: " + engine.isModified(VayDefaults.THEME_AMOLED));
        engine.resetAll(VayScopeKey.GLOBAL);
        System.out.println("modified global settings after reset: " + engine.countModified(VayScopeKey.GLOBAL));
    }
}
