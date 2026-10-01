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

    public static final VaySetting<Integer> DIALOG_NAME_TEXT_SIZE = VaySetting
            .builder("dialogs.name.text_size", VaySettingType.INTEGER, 17)
            .title("Chat title text size")
            .description("Text size for chat names in the chat list")
            .category("Chat List")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("chat list", "title", "font", "text size", "список чатов", "шрифт", "название")
            .range(13, 22, 1)
            .validator(v -> clamp(v, 13, 22))
            .build();

    public static final VaySetting<Integer> DIALOG_MESSAGE_TEXT_SIZE = VaySetting
            .builder("dialogs.message.text_size", VaySettingType.INTEGER, 16)
            .title("Chat preview text size")
            .description("Text size for message previews in the chat list")
            .category("Chat List")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("chat list", "preview", "font", "text size", "список чатов", "шрифт", "сообщение")
            .range(12, 20, 1)
            .validator(v -> clamp(v, 12, 20))
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
            .builder("profile.avatar.status_ring", VaySettingType.BOOLEAN, false)
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

    public static final VaySetting<Boolean> PROFILE_SHOW_AVATAR = VaySetting
            .builder("profile.block.avatar", VaySettingType.BOOLEAN, true)
            .title("Show avatar")
            .description("Show the avatar block in Vay Profile")
            .category("Profile Studio / Core blocks")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "avatar", "block", "аватар")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_NAME = VaySetting
            .builder("profile.block.name", VaySettingType.BOOLEAN, true)
            .title("Show name")
            .description("Show the display name block in Vay Profile")
            .category("Profile Studio / Core blocks")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "name", "block", "имя")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_STATUS = VaySetting
            .builder("profile.block.status", VaySettingType.BOOLEAN, true)
            .title("Show status")
            .description("Show the presence or last-seen status block when Telegram exposes it")
            .category("Profile Studio / Core blocks")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "status", "last seen", "online", "статус")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_PHONE = VaySetting
            .builder("profile.block.phone", VaySettingType.BOOLEAN, false)
            .title("Show phone")
            .description("Show the phone block only when Telegram already exposes it to the viewer")
            .category("Profile Studio / Personal blocks")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "phone", "privacy", "телефон")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_BIRTHDAY = VaySetting
            .builder("profile.block.birthday", VaySettingType.BOOLEAN, false)
            .title("Show birthday")
            .description("Show the birthday block when available")
            .category("Profile Studio / Personal blocks")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "birthday", "date", "день рождения")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_EMOJI_STATUS = VaySetting
            .builder("profile.block.emoji_status", VaySettingType.BOOLEAN, true)
            .title("Show emoji status")
            .description("Show the Telegram emoji status block when available")
            .category("Profile Studio / Core blocks")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "emoji", "status", "эмодзи")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_PERSONAL_CHANNEL = VaySetting
            .builder("profile.block.personal_channel", VaySettingType.BOOLEAN, false)
            .title("Show personal channel")
            .description("Show the personal channel block when one is linked")
            .category("Profile Studio / Social blocks")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "channel", "personal", "канал")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_GROUPS = VaySetting
            .builder("profile.block.groups", VaySettingType.BOOLEAN, false)
            .title("Show groups")
            .description("Show a groups block using Telegram-visible group information")
            .category("Profile Studio / Social blocks")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "groups", "chats", "группы")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_LINKS = VaySetting
            .builder("profile.block.links", VaySettingType.BOOLEAN, false)
            .title("Show links")
            .description("Show the profile links block")
            .category("Profile Studio / Content blocks")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "links", "url", "ссылки")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_MUSIC = VaySetting
            .builder("profile.block.music", VaySettingType.BOOLEAN, false)
            .title("Show music")
            .description("Show the Vay Profile music block")
            .category("Profile Studio / Content blocks")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "music", "player", "музыка")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_QUOTE = VaySetting
            .builder("profile.block.quote", VaySettingType.BOOLEAN, false)
            .title("Show quote")
            .description("Show a quote block in Vay Profile")
            .category("Profile Studio / Content blocks")
            .visibility(VayVisibilityLevel.ADVANCED)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "quote", "text", "цитата")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_BADGES = VaySetting
            .builder("profile.block.badges", VaySettingType.BOOLEAN, true)
            .title("Show badges")
            .description("Show profile badges when available")
            .category("Profile Studio / Content blocks")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "badges", "status", "значки")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_GIFTS = VaySetting
            .builder("profile.block.gifts", VaySettingType.BOOLEAN, true)
            .title("Show gifts")
            .description("Show the Telegram gifts block when available")
            .category("Profile Studio / Content blocks")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "gifts", "telegram", "подарки")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_MEDIA = VaySetting
            .builder("profile.block.media", VaySettingType.BOOLEAN, true)
            .title("Show media")
            .description("Show the profile media block")
            .category("Profile Studio / Content blocks")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "media", "photos", "вложения")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_MUTUAL_CHATS = VaySetting
            .builder("profile.block.mutual_chats", VaySettingType.BOOLEAN, true)
            .title("Show mutual chats")
            .description("Show mutual chats when Telegram exposes them")
            .category("Profile Studio / Social blocks")
            .visibility(VayVisibilityLevel.BASIC)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "mutual", "chats", "общие чаты")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_CUSTOM_TEXT = VaySetting
            .builder("profile.block.custom_text", VaySettingType.BOOLEAN, false)
            .title("Show custom text")
            .description("Enable a custom text block in Vay Profile")
            .category("Profile Studio / Custom blocks")
            .visibility(VayVisibilityLevel.INSANE)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "custom", "text", "текст")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_CUSTOM_IMAGE = VaySetting
            .builder("profile.block.custom_image", VaySettingType.BOOLEAN, false)
            .title("Show custom image")
            .description("Enable a custom image block in Vay Profile")
            .category("Profile Studio / Custom blocks")
            .visibility(VayVisibilityLevel.INSANE)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "custom", "image", "картинка")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_SEPARATORS = VaySetting
            .builder("profile.block.separators", VaySettingType.BOOLEAN, true)
            .title("Show separators")
            .description("Allow separator blocks in Vay Profile layouts")
            .category("Profile Studio / Custom blocks")
            .visibility(VayVisibilityLevel.INSANE)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "separator", "layout", "разделитель")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_SPACER = VaySetting
            .builder("profile.block.spacer", VaySettingType.BOOLEAN, true)
            .title("Show spacer")
            .description("Allow spacer blocks in Vay Profile layouts")
            .category("Profile Studio / Custom blocks")
            .visibility(VayVisibilityLevel.INSANE)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "spacer", "layout", "отступ")
            .build();

    public static final VaySetting<Boolean> PROFILE_SHOW_BUTTONS = VaySetting
            .builder("profile.block.buttons", VaySettingType.BOOLEAN, false)
            .title("Show buttons")
            .description("Enable custom action buttons in Vay Profile")
            .category("Profile Studio / Custom blocks")
            .visibility(VayVisibilityLevel.INSANE)
            .scopes(VaySettingScope.GLOBAL, VaySettingScope.ACCOUNT)
            .tags("profile", "buttons", "actions", "кнопки")
            .build();

    public static VaySettingsRegistry createRegistry() {
        VaySettingsRegistry registry = new VaySettingsRegistry();
        registry.register(CHAT_BUBBLE_RADIUS);
        registry.register(CHAT_MESSAGE_SPACING);
        registry.register(DIALOG_ROW_HEIGHT);
        registry.register(DIALOG_NAME_TEXT_SIZE);
        registry.register(DIALOG_MESSAGE_TEXT_SIZE);
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
        registry.register(PROFILE_SHOW_AVATAR);
        registry.register(PROFILE_SHOW_NAME);
        registry.register(PROFILE_SHOW_STATUS);
        registry.register(PROFILE_SHOW_PHONE);
        registry.register(PROFILE_SHOW_BIRTHDAY);
        registry.register(PROFILE_SHOW_EMOJI_STATUS);
        registry.register(PROFILE_SHOW_PERSONAL_CHANNEL);
        registry.register(PROFILE_SHOW_GROUPS);
        registry.register(PROFILE_SHOW_LINKS);
        registry.register(PROFILE_SHOW_MUSIC);
        registry.register(PROFILE_SHOW_QUOTE);
        registry.register(PROFILE_SHOW_BADGES);
        registry.register(PROFILE_SHOW_GIFTS);
        registry.register(PROFILE_SHOW_MEDIA);
        registry.register(PROFILE_SHOW_MUTUAL_CHATS);
        registry.register(PROFILE_SHOW_CUSTOM_TEXT);
        registry.register(PROFILE_SHOW_CUSTOM_IMAGE);
        registry.register(PROFILE_SHOW_SEPARATORS);
        registry.register(PROFILE_SHOW_SPACER);
        registry.register(PROFILE_SHOW_BUTTONS);
        return registry;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
