package app.vaygram.core.theme;

public final class VayThemeTokens {
    private VayThemeTokens() {}

    public static final VayThemeToken SURFACE_PRIMARY = color(
            "surface.primary", "Primary surface", "Surfaces",
            "background", "window", "основной фон"
    );
    public static final VayThemeToken SURFACE_SECONDARY = color(
            "surface.secondary", "Secondary surface", "Surfaces",
            "secondary background", "gray", "вторичный фон"
    );
    public static final VayThemeToken SURFACE_ELEVATED = color(
            "surface.elevated", "Elevated surface", "Surfaces",
            "dialog", "sheet", "card", "диалог"
    );
    public static final VayThemeToken TEXT_PRIMARY = color(
            "text.primary", "Primary text", "Typography",
            "text", "title", "текст"
    );
    public static final VayThemeToken TEXT_SECONDARY = color(
            "text.secondary", "Secondary text", "Typography",
            "subtitle", "muted", "secondary", "подпись"
    );
    public static final VayThemeToken ACCENT_PRIMARY = color(
            "accent.primary", "Primary accent", "Accent",
            "accent", "link", "button", "акцент"
    );
    public static final VayThemeToken DIVIDER = color(
            "surface.divider", "Divider", "Surfaces",
            "separator", "line", "разделитель"
    );

    public static final VayThemeToken CHAT_BUBBLE_IN = color(
            "chat.bubble.in", "Incoming bubble", "Chat",
            "incoming", "bubble", "входящие"
    );
    public static final VayThemeToken CHAT_BUBBLE_OUT = color(
            "chat.bubble.out", "Outgoing bubble", "Chat",
            "outgoing", "bubble", "исходящие"
    );
    public static final VayThemeToken CHAT_TEXT_IN = color(
            "chat.text.in", "Incoming message text", "Chat",
            "incoming", "message text", "входящий текст"
    );
    public static final VayThemeToken CHAT_TEXT_OUT = color(
            "chat.text.out", "Outgoing message text", "Chat",
            "outgoing", "message text", "исходящий текст"
    );

    public static final VayThemeToken NAV_SURFACE = color(
            "navigation.surface", "Navigation surface", "Navigation",
            "bottom bar", "navigation", "панель"
    );
    public static final VayThemeToken NAV_ICON_ACTIVE = color(
            "navigation.icon.active", "Active navigation icon", "Navigation",
            "selected", "active", "navigation", "активная"
    );
    public static final VayThemeToken NAV_ICON_INACTIVE = color(
            "navigation.icon.inactive", "Inactive navigation icon", "Navigation",
            "unselected", "inactive", "navigation", "неактивная"
    );

    public static VayThemeTokenRegistry createRegistry() {
        VayThemeTokenRegistry registry = new VayThemeTokenRegistry();
        registry.register(SURFACE_PRIMARY);
        registry.register(SURFACE_SECONDARY);
        registry.register(SURFACE_ELEVATED);
        registry.register(TEXT_PRIMARY);
        registry.register(TEXT_SECONDARY);
        registry.register(ACCENT_PRIMARY);
        registry.register(DIVIDER);
        registry.register(CHAT_BUBBLE_IN);
        registry.register(CHAT_BUBBLE_OUT);
        registry.register(CHAT_TEXT_IN);
        registry.register(CHAT_TEXT_OUT);
        registry.register(NAV_SURFACE);
        registry.register(NAV_ICON_ACTIVE);
        registry.register(NAV_ICON_INACTIVE);
        return registry;
    }

    private static VayThemeToken color(
            String id,
            String title,
            String category,
            String... tags
    ) {
        return VayThemeToken.builder(id, VayThemeTokenType.COLOR)
                .title(title)
                .category(category)
                .tags(tags)
                .build();
    }
}
