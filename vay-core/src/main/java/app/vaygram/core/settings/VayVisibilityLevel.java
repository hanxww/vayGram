package app.vaygram.core.settings;

public enum VayVisibilityLevel {
    BASIC(0), ADVANCED(1), INSANE(2);

    private final int rank;

    VayVisibilityLevel(int rank) {
        this.rank = rank;
    }

    public boolean isVisibleAt(VayVisibilityLevel selectedLevel) {
        return rank <= selectedLevel.rank;
    }
}
