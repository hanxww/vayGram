package app.vaygram.ui;

import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;

import app.vaygram.core.settings.VayVisibilityLevel;

public final class VayOnboardingState {
    private static final String PREFS = "vaygram_onboarding_v1";
    private static final String KEY_COMPLETED = "completed";
    private static final String KEY_LEVEL = "preferred_level";
    private static final String KEY_QUICK_TOUR_COMPLETED = "quick_tour_completed";

    private static boolean presenting;
    private static boolean quickTourPresenting;

    private VayOnboardingState() {}

    private static SharedPreferences prefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        );
    }

    public static boolean isCompleted() {
        return prefs().getBoolean(KEY_COMPLETED, false);
    }

    public static void markCompleted() {
        prefs().edit()
                .putBoolean(KEY_COMPLETED, true)
                .putBoolean(KEY_QUICK_TOUR_COMPLETED, true)
                .apply();
        endPresentation();
        endQuickTourPresentation();
    }

    public static boolean isQuickTourCompleted() {
        return prefs().getBoolean(KEY_QUICK_TOUR_COMPLETED, false);
    }

    public static void markQuickTourCompleted() {
        prefs().edit().putBoolean(KEY_QUICK_TOUR_COMPLETED, true).apply();
        endQuickTourPresentation();
    }

    public static void resetForReplay() {
        endPresentation();
    }

    public static VayVisibilityLevel preferredLevel() {
        String raw = prefs().getString(KEY_LEVEL, VayVisibilityLevel.BASIC.name());
        try {
            return VayVisibilityLevel.valueOf(raw);
        } catch (RuntimeException ignored) {
            return VayVisibilityLevel.BASIC;
        }
    }

    public static void setPreferredLevel(VayVisibilityLevel level) {
        if (level == null) {
            level = VayVisibilityLevel.BASIC;
        }
        prefs().edit().putString(KEY_LEVEL, level.name()).apply();
    }

    public static synchronized boolean beginPresentation() {
        if (presenting || isCompleted()) {
            return false;
        }
        presenting = true;
        return true;
    }

    public static synchronized void endPresentation() {
        presenting = false;
    }

    public static synchronized boolean beginQuickTourPresentation() {
        if (quickTourPresenting || isQuickTourCompleted() || isCompleted()) {
            return false;
        }
        quickTourPresenting = true;
        return true;
    }

    public static synchronized void endQuickTourPresentation() {
        quickTourPresenting = false;
    }
}
