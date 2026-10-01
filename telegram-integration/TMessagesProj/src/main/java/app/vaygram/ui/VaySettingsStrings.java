package app.vaygram.ui;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

import app.vaygram.core.settings.VaySetting;

public final class VaySettingsStrings {
    private VaySettingsStrings() {}

    public static String title(VaySetting<?> setting) {
        if (setting == null) {
            return "";
        }
        switch (setting.getId()) {
            case "chat.bubble.radius": return s(R.string.vay_setting_bubble_radius);
            case "chat.message.spacing": return s(R.string.vay_setting_message_spacing);
            case "dialogs.row.height": return s(R.string.vay_setting_chat_row_height);
            case "dialogs.name.text_size": return s(R.string.vay_setting_chat_title_text_size);
            case "dialogs.message.text_size": return s(R.string.vay_setting_chat_preview_text_size);
            case "avatar.size": return s(R.string.vay_setting_avatar_size);
            case "avatar.radius": return s(R.string.vay_setting_avatar_roundness);
            case "navigation.bottom.labels": return s(R.string.vay_setting_navigation_labels);
            case "navigation.bottom.height": return s(R.string.vay_setting_bottom_bar_height);
            case "layout.compact": return s(R.string.vay_setting_compact_mode);
            case "motion.scale": return s(R.string.vay_setting_animation_scale);
            case "theme.amoled": return s(R.string.vay_setting_amoled);
            case "theme.material_you": return s(R.string.vay_setting_material_you);
            case "effects.glass.blur": return s(R.string.vay_setting_glass_blur);
            case "effects.glass.blur_radius": return s(R.string.vay_setting_glass_blur_radius);
            case "effects.glass.opacity": return s(R.string.vay_setting_glass_opacity);
            default: return setting.getTitle();
        }
    }

    public static String description(VaySetting<?> setting) {
        if (setting == null) {
            return "";
        }
        switch (setting.getId()) {
            case "chat.bubble.radius": return s(R.string.vay_desc_bubble_radius);
            case "chat.message.spacing": return s(R.string.vay_desc_message_spacing);
            case "dialogs.row.height": return s(R.string.vay_desc_chat_row_height);
            case "dialogs.name.text_size": return s(R.string.vay_desc_chat_title_text_size);
            case "dialogs.message.text_size": return s(R.string.vay_desc_chat_preview_text_size);
            case "avatar.size": return s(R.string.vay_desc_avatar_size);
            case "avatar.radius": return s(R.string.vay_desc_avatar_roundness);
            case "navigation.bottom.labels": return s(R.string.vay_desc_navigation_labels);
            case "navigation.bottom.height": return s(R.string.vay_desc_bottom_bar_height);
            case "layout.compact": return s(R.string.vay_desc_compact_mode);
            case "motion.scale": return s(R.string.vay_desc_animation_scale);
            case "theme.amoled": return s(R.string.vay_desc_amoled);
            case "theme.material_you": return s(R.string.vay_desc_material_you);
            case "effects.glass.blur": return s(R.string.vay_desc_glass_blur);
            case "effects.glass.blur_radius": return s(R.string.vay_desc_glass_blur_radius);
            case "effects.glass.opacity": return s(R.string.vay_desc_glass_opacity);
            default: return setting.getDescription();
        }
    }

    public static String category(String category) {
        if ("Chats / Bubbles".equals(category)) return s(R.string.vay_category_chats_bubbles);
        if ("Chats / Layout".equals(category)) return s(R.string.vay_category_chats_layout);
        if ("Chat List".equals(category)) return s(R.string.vay_category_chat_list);
        if ("Avatars".equals(category)) return s(R.string.vay_category_avatars);
        if ("Navigation".equals(category)) return s(R.string.vay_category_navigation);
        if ("Appearance / Layout".equals(category)) return s(R.string.vay_category_appearance_layout);
        if ("Motion".equals(category)) return s(R.string.vay_category_motion);
        if ("Appearance / Theme".equals(category)) return s(R.string.vay_category_appearance_theme);
        if ("Appearance / Effects".equals(category)) return s(R.string.vay_category_appearance_effects);
        return category;
    }

    private static String s(int res) {
        return LocaleController.getString(res);
    }
}
