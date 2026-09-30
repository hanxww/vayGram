package app.vaygram.core.settings;

public final class VayDefaults {
    private VayDefaults() {}

    public static final VaySetting<Float> CHAT_BUBBLE_RADIUS = VaySetting
            .builder("chat.bubble.radius", VaySettingType.FLOAT, 17f)
            .title("Bubble radius")
            .description("Corner radius of message bubbles")
            .category("Chats / Bubbles")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT, VaySettingScope.CHAT)
            .tags("bubble", "radius", "chat", "скругление", "сообщения")
            .range(0, 40, 1)
            .validator(v -> clamp(v, 0f, 40f))
            .build();

    public static final VaySetting<Float> CHAT_MESSAGE_SPACING = VaySetting
            .builder("chat.message.spacing", VaySettingType.FLOAT, 0f)
            .title("Message spacing")
            .description("Additional vertical spacing between messages")
            .category("Chats / Layout")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT, VaySettingScope.CHAT)
            .tags("spacing", "density", "чат", "отступ")
            .range(0, 24, 1)
            .validator(v -> clamp(v, 0f, 24f))
            .build();

    public static final VaySetting<Integer> DIALOG_ROW_HEIGHT = VaySetting
            .builder("dialogs.row.height", VaySettingType.INTEGER, 70)
            .title("Chat row height")
            .description("Height of each row in the chat list")
            .category("Chat List")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("chat list", "compact", "density", "список чатов")
            .range(48, 112, 1)
            .validator(v -> clamp(v, 48, 112))
            .build();

    public static final VaySetting<Float> AVATAR_SIZE = VaySetting
            .builder("avatar.size", VaySettingType.FLOAT, 54f)
            .title("Avatar size")
            .description("Default avatar size")
            .category("Avatars")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT, VaySettingScope.CHAT)
            .tags("avatar", "аватар", "size", "размер")
            .range(28, 96, 1)
            .validator(v -> clamp(v, 28f, 96f))
            .build();

    public static final VaySetting<Float> AVATAR_RADIUS = VaySetting
            .builder("avatar.radius", VaySettingType.FLOAT, 50f)
            .title("Avatar roundness")
            .description("Avatar corner radius as a percentage")
            .category("Avatars")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT, VaySettingScope.CHAT)
            .tags("avatar", "shape", "round", "аватар", "форма")
            .range(0, 50, 1)
            .validator(v -> clamp(v, 0f, 50f))
            .build();

    public static final VaySetting<Boolean> NAV_SHOW_LABELS = VaySetting
            .builder("navigation.bottom.labels", VaySettingType.BOOLEAN, true)
            .title("Navigation labels")
            .description("Show text labels under bottom navigation icons")
            .category("Navigation")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("bottom bar", "labels", "navigation", "навигация")
            .build();

    public static final VaySetting<Integer> NAV_HEIGHT = VaySetting
            .builder("navigation.bottom.height", VaySettingType.INTEGER, 56)
            .title("Bottom bar height")
            .description("Height of the bottom navigation bar")
            .category("Navigation")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("bottom bar", "height", "navigation", "панель")
            .range(48, 88, 1)
            .validator(v -> clamp(v, 48, 88))
            .build();

    public static final VaySetting<Boolean> COMPACT_MODE = VaySetting
            .builder("layout.compact", VaySettingType.BOOLEAN, false)
            .title("Compact mode")
            .description("Temporarily tighten supported layouts without overwriting your custom values")
            .category("Appearance / Layout")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL)
            .tags("compact", "density", "small", "layout", "компакт", "плотность")
            .build();

    public static final VaySetting<Float> MOTION_SCALE = VaySetting
            .builder("motion.scale", VaySettingType.FLOAT, 1f)
            .title("Animation scale")
            .description("Multiplier for vayGram-controlled UI animation durations")
            .category("Motion")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL)
            .tags("animation", "speed", "motion", "анимация")
            .range(0, 2, 0.05)
            .validator(v -> clamp(v, 0f, 2f))
            .build();

    public static final VaySetting<Boolean> THEME_AMOLED = VaySetting
            .builder("theme.amoled", VaySettingType.BOOLEAN, false)
            .title("AMOLED surfaces")
            .description("Use pure black for supported dark surfaces")
            .category("Appearance / Theme")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("amoled", "black", "dark", "черный")
            .build();

    public static final VaySetting<Boolean> THEME_MATERIAL_YOU = VaySetting
            .builder("theme.material_you", VaySettingType.BOOLEAN, false)
            .title("Material You palette")
            .description("Use Android dynamic system colors as the base vayGram palette")
            .category("Appearance / Theme")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("material you", "dynamic color", "wallpaper", "system palette", "цвета системы")
            .build();

    public static final VaySetting<Boolean> GLASS_BLUR = VaySetting
            .builder("effects.glass.blur", VaySettingType.BOOLEAN, false)
            .title("Glass blur")
            .description("Enable vayGram glass-style blur where supported")
            .category("Appearance / Effects")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("glass", "blur", "стекло", "блюр")
            .build();

    public static final VaySetting<Integer> GLASS_BLUR_RADIUS = VaySetting
            .builder("effects.glass.blur_radius", VaySettingType.INTEGER, 18)
            .title("Glass blur radius")
            .description("Blur radius for Vay glass surfaces")
            .category("Appearance / Effects")
            .visibility(VayVisibilityLevel.INSANE)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("glass", "blur", "radius", "блюр")
            .range(0, 64, 1)
            .validator(v -> clamp(v, 0, 64))
            .build();

    public static final VaySetting<Float> GLASS_OPACITY = VaySetting
            .builder("effects.glass.opacity", VaySettingType.FLOAT, 1f)
            .title("Glass opacity")
            .description("Opacity of supported vayGram glass surfaces")
            .category("Appearance / Effects")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("glass", "opacity", "transparency", "alpha", "прозрачность")
            .range(0.20, 1.0, 0.05)
            .validator(v -> clamp(v, 0.20f, 1f))
            .build();

    public static final VaySetting<Boolean> PROFILE_AUTHOR_STYLE = VaySetting
            .builder("profile.remote.author_style", VaySettingType.BOOLEAN, true)
            .title("Show author profile style")
            .description("Allow published Vay Profile layouts to be rendered")
            .category("Profile")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "vay profile", "author style", "профиль")
            .build();

    public static final VaySetting<String> PROFILE_LAYOUT_MODE = VaySetting
            .builder("profile.layout.mode", VaySettingType.ENUM, "grid")
            .title("Profile layout mode")
            .description("Choose grid or free-form Vay Profile layout")
            .category("Profile Studio")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "layout", "grid", "free", "профиль", "макет")
            .validator(v -> "free".equalsIgnoreCase(v) ? "free" : "grid")
            .build();

    public static final VaySetting<Integer> PROFILE_AVATAR_SIZE = VaySetting
            .builder("profile.avatar.size", VaySettingType.INTEGER, 112)
            .title("Profile avatar size")
            .description("Avatar size in the Vay Profile header")
            .category("Profile Studio / Avatar")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "avatar", "size", "аватар", "размер")
            .range(64, 196, 2)
            .validator(v -> clamp(v, 64, 196))
            .build();

    public static final VaySetting<Float> PROFILE_AVATAR_RADIUS = VaySetting
            .builder("profile.avatar.radius", VaySettingType.FLOAT, 50f)
            .title("Profile avatar roundness")
            .description("Avatar corner radius as a percentage")
            .category("Profile Studio / Avatar")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "avatar", "shape", "round", "форма")
            .range(0, 50, 1)
            .validator(v -> clamp(v, 0f, 50f))
            .build();

    public static final VaySetting<Boolean> PROFILE_AVATAR_GLOW = VaySetting
            .builder("profile.avatar.glow", VaySettingType.BOOLEAN, false)
            .title("Avatar glow")
            .description("Draw a soft accent glow around the profile avatar")
            .category("Profile Studio / Avatar")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "avatar", "glow", "свечение")
            .build();

    public static final VaySetting<Boolean> PROFILE_STATUS_RING = VaySetting
            .builder("profile.avatar.status_ring", VaySettingType.BOOLEAN, true)
            .title("Status ring")
            .description("Show a Vay status ring around the profile avatar")
            .category("Profile Studio / Avatar")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "avatar", "status", "ring", "кольцо")
            .build();

    public static final VaySetting<Integer> PROFILE_BACKGROUND_BLUR = VaySetting
            .builder("profile.background.blur", VaySettingType.INTEGER, 0)
            .title("Profile background blur")
            .description("Blur strength for Vay Profile background layers")
            .category("Profile Studio / Effects")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "background", "blur", "фон", "блюр")
            .range(0, 64, 1)
            .validator(v -> clamp(v, 0, 64))
            .build();

    public static final VaySetting<Boolean> PROFILE_PARALLAX = VaySetting
            .builder("profile.background.parallax", VaySettingType.BOOLEAN, false)
            .title("Profile parallax")
            .description("Enable subtle motion depth for Vay Profile backgrounds")
            .category("Profile Studio / Effects")
            .visibility(VayVisibilityLevel.INSANE)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "parallax", "motion", "параллакс")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_USERNAME = VaySetting
            .builder("profile.block.username", VaySettingType.BOOLEAN, true)
            .title("Show username")
            .description("Show the username block in Vay Profile")
            .category("Profile Studio / Blocks")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "username", "block", "имя")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_BIO = VaySetting
            .builder("profile.block.bio", VaySettingType.BOOLEAN, true)
            .title("Show bio")
            .description("Show the bio block in Vay Profile")
            .category("Profile Studio / Blocks")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "bio", "block", "описание")
            .build();

    public static VaySettingsRegistry createRegistry() {
        VaySettingsRegistry registry = new VaySettingsRegistry();
        registry.register(CHAT_BUBBLE_RADIUS);
        registry.register(CHAT_MESSAGE_SPACING);
        registry.register(DIALOG_ROW_HEIGHT);
        registry.register(AVATAR_SIZE);
        registry.register(AVATAR_RADIUS);
        registry.register(NAV_SHOW_LABELS);
        registry.register(NAV_HEIGHT);
        registry.register(COMPACT_MODE);
        registry.register(MOTION_SCALE);
        registry.register(THEME_AMOLED);
        registry.register(THEME_MATERIAL_YOU);
        registry.register(GLASS_BLUR);
        registry.register(GLASS_BLUR_RADIUS);
        registry.register(GLASS_OPACITY);
        registry.register(PROFILE_AUTHOR_STYLE);
        registry.register(PROFILE_LAYOUT_MODE);
        registry.register(PROFILE_AVATAR_SIZE);
        registry.register(PROFILE_AVATAR_RADIUS);
        registry.register(PROFILE_AVATAR_GLOW);
        registry.register(PROFILE_STATUS_RING);
        registry.register(PROFILE_BACKGROUND_BLUR);
        registry.register(PROFILE_PARALLAX);
        registry.register(PROFILE_SHOW_USERNAME);
        registry.register(PROFILE_SHOW_BIO);
        return registry;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
