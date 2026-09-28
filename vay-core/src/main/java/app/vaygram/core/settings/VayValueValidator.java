package app.vaygram.core.settings;

public interface VayValueValidator<T> {
    T normalize(T value);
}
