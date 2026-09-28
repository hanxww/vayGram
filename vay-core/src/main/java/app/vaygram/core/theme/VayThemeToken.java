package app.vaygram.core.theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class VayThemeToken {
    private final String id;
    private final VayThemeTokenType type;
    private final String title;
    private final String category;
    private final List<String> tags;

    private VayThemeToken(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id");
        this.type = Objects.requireNonNull(builder.type, "type");
        this.title = Objects.requireNonNull(builder.title, "title");
        this.category = Objects.requireNonNull(builder.category, "category");
        this.tags = Collections.unmodifiableList(new ArrayList<>(builder.tags));
    }

    public static Builder builder(String id, VayThemeTokenType type) {
        return new Builder(id, type);
    }

    public String getId() {
        return id;
    }

    public VayThemeTokenType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean matches(String rawQuery) {
        String query = rawQuery == null ? "" : rawQuery.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            return true;
        }
        if (id.toLowerCase(Locale.ROOT).contains(query)
                || title.toLowerCase(Locale.ROOT).contains(query)
                || category.toLowerCase(Locale.ROOT).contains(query)) {
            return true;
        }
        for (String tag : tags) {
            if (tag.toLowerCase(Locale.ROOT).contains(query)) {
                return true;
            }
        }
        return false;
    }

    public static final class Builder {
        private final String id;
        private final VayThemeTokenType type;
        private String title;
        private String category = "General";
        private final List<String> tags = new ArrayList<>();

        private Builder(String id, VayThemeTokenType type) {
            this.id = Objects.requireNonNull(id, "id");
            this.type = Objects.requireNonNull(type, "type");
            this.title = id;
        }

        public Builder title(String title) {
            this.title = Objects.requireNonNull(title, "title");
            return this;
        }

        public Builder category(String category) {
            this.category = Objects.requireNonNull(category, "category");
            return this;
        }

        public Builder tags(String... values) {
            if (values != null) {
                Collections.addAll(tags, values);
            }
            return this;
        }

        public VayThemeToken build() {
            return new VayThemeToken(this);
        }
    }
}
