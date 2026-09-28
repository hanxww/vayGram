package app.vaygram.core.demo;

import app.vaygram.core.settings.InMemoryVaySettingsStore;
import app.vaygram.core.settings.VayDefaults;
import app.vaygram.core.settings.VayScopeKey;
import app.vaygram.core.settings.VaySetting;
import app.vaygram.core.settings.VaySettingScope;
import app.vaygram.core.settings.VaySettingsEngine;
import app.vaygram.core.settings.VaySettingsRegistry;
import app.vaygram.core.settings.VayVisibilityLevel;

public final class Demo {
    public static void main(String[] args) {
        VaySettingsRegistry registry = VayDefaults.createRegistry();
        VaySettingsEngine engine = new VaySettingsEngine(registry, new InMemoryVaySettingsStore());

        engine.addListener(change -> System.out.println("changed: " + change));
        System.out.println("vayGram settings registered: " + registry.size());
        System.out.println("default bubble radius: " + engine.get(VayDefaults.CHAT_BUBBLE_RADIUS));

        engine.set(VayDefaults.CHAT_BUBBLE_RADIUS, 12f);
        System.out.println("new bubble radius: " + engine.get(VayDefaults.CHAT_BUBBLE_RADIUS));

        VayScopeKey chat = new VayScopeKey(VaySettingScope.CHAT, "123456");
        engine.set(VayDefaults.CHAT_BUBBLE_RADIUS, chat, 4f);
        System.out.println("chat override: " + engine.get(VayDefaults.CHAT_BUBBLE_RADIUS, chat));

        System.out.println("search 'аватар' in Advanced:");
        for (VaySetting<?> setting : registry.search("аватар", VayVisibilityLevel.ADVANCED)) {
            System.out.println(" - " + setting.getId());
        }

        engine.undo();
        System.out.println("chat after undo: " + engine.get(VayDefaults.CHAT_BUBBLE_RADIUS, chat));
        engine.redo();
        System.out.println("chat after redo: " + engine.get(VayDefaults.CHAT_BUBBLE_RADIUS, chat));

        engine.set(VayDefaults.THEME_AMOLED, true);
        System.out.println("modified global settings: " + engine.countModified(VayScopeKey.GLOBAL));
        System.out.println("amoled modified: " + engine.isModified(VayDefaults.THEME_AMOLED));
        engine.resetAll(VayScopeKey.GLOBAL);
        System.out.println("modified global settings after reset: " + engine.countModified(VayScopeKey.GLOBAL));
    }
}
