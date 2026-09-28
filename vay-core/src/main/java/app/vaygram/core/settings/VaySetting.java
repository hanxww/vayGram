package app.vaygram.core.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class VaySetting<T> {
    private final String id;
    private final String title;
    private final String description;
    private final String category;
    private final VaySettingType type;
    private final T defaultValue;
    private final VayVisibilityLevel visibilityLevel;
    private final Set<VaySettingScope> scopes;
    private final List<String> tags;
    private final VayValueValidator<T> validator;
    private final Double minValue;
    private final Double maxValue;
    private final Double stepValue;

    private VaySetting(Builder<T> builder) {
        this.id = requireText(builder.id, "id");
        this.title = requireText(builder.title, "title");
        this.description = builder.description == null ? "" : builder.description;
        this.category = requireText(builder.category, "category");
        this.type = Objects.requireNonNull(builder.type, "type");
        this.defaultValue = Objects.requireNonNull(builder.defaultValue, "defaultValue");
        this.visibilityLevel = Objects.requireNonNull(builder.visibilityLevel, "visibilityLevel");
        this.scopes = Collections.unmodifiableSet(EnumSet.copyOf(builder.scopes));
        this.tags = Collections.unmodifiableList(new ArrayList<>(builder.tags));
        this.validator = builder.validator;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
        this.stepValue = builder.stepValue;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be empty");
        }
        return value.trim();
    }

    public static <T> Builder<T> builder(String id, VaySettingType type, T defaultValue) {
        return new Builder<>(id, type, defaultValue);
    }

    public T normalize(T value) {
        if (value == null) return defaultValue;
        return validator == null ? value : validator.normalize(value);
    }

    public boolean matches(String rawQuery) {
        if (rawQuery == null || rawQuery.trim().isEmpty()) return true;
        String query = rawQuery.trim().toLowerCase(Locale.ROOT);
        if (id.toLowerCase(Locale.ROOT).contains(query)
                || title.toLowerCase(Locale.ROOT).contains(query)
                || description.toLowerCase(Locale.ROOT).contains(query)
                || category.toLowerCase(Locale.ROOT).contains(query)) {
            return true;
        }
        for (String tag : tags) {
            if (tag.toLowerCase(Locale.ROOT).contains(query)) return true;
        }
        return false;
    }

    public boolean hasNumericRange() {
        return minValue != null && maxValue != null && stepValue != null;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public VaySettingType getType() { return type; }
    public T getDefaultValue() { return defaultValue; }
    public VayVisibilityLevel getVisibilityLevel() { return visibilityLevel; }
    public Set<VaySettingScope> getScopes() { return scopes; }
    public List<String> getTags() { return tags; }
    public Double getMinValue() { return minValue; }
    public Double getMaxValue() { return maxValue; }
    public Double getStepValue() { return stepValue; }

    public static final class Builder<T> {
        private final String id;
        private final VaySettingType type;
        private final T defaultValue;
        private String title;
        private String description = "";
        private String category = "General";
        private VayVisibilityLevel visibilityLevel = VayVisibilityLevel.BASIC;
        private Set<VaySettingScope> scopes = EnumSet.of(VaySettingScope.GLOBAL);
        private final List<String> tags = new ArrayList<>();
        private VayValueValidator<T> validator;
        private Double minValue;
        private Double maxValue;
        private Double stepValue;

        private Builder(String id, VaySettingType type, T defaultValue) {
            this.id = id;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        public Builder<T> title(String title) { this.title = title; return this; }
        public Builder<T> description(String description) { this.description = description; return this; }
        public Builder<T> category(String category) { this.category = category; return this; }
        public Builder<T> visibility(VayVisibilityLevel level) { this.visibilityLevel = level; return this; }

        public Builder<T> scopes(VaySettingScope first, VaySettingScope... rest) {
            EnumSet<VaySettingScope> set = EnumSet.of(first);
            if (rest != null) Collections.addAll(set, rest);
            this.scopes = set;
            return this;
        }

        public Builder<T> tags(String... values) {
            if (values != null) Collections.addAll(tags, values);
            return this;
        }

        public Builder<T> validator(VayValueValidator<T> validator) {
            this.validator = validator;
            return this;
        }

        public Builder<T> range(double min, double max, double step) {
            if (type != VaySettingType.INTEGER && type != VaySettingType.FLOAT) {
                throw new IllegalStateException("range() is only valid for INTEGER/FLOAT settings");
            }
            if (Double.isNaN(min) || Double.isNaN(max) || Double.isNaN(step)
                    || min >= max || step <= 0d) {
                throw new IllegalArgumentException("Invalid numeric range");
            }
            this.minValue = min;
            this.maxValue = max;
            this.stepValue = step;
            return this;
        }

        public VaySetting<T> build() {
            if (title == null) title = id;
            return new VaySetting<>(this);
        }
    }
}
