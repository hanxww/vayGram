package app.vaygram.ui;

import android.view.View;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;

public final class VayFirstLaunchCoach {
    private static final int STEPS = 2;

    private VayFirstLaunchCoach() {}

    public static void maybePresent(
            BaseFragment host,
            View menuTarget,
            View composeTarget
    ) {
        if (host == null
                || host.getParentActivity() == null
                || host.getVisibleDialog() != null
                || VayOnboardingState.isCompleted()) {
            return;
        }

        if (VayOnboardingState.isQuickTourCompleted()) {
            VayOnboardingActivity.maybePresent(host);
            return;
        }

        if (!VayOnboardingState.beginQuickTourPresentation()) {
            return;
        }

        AndroidUtilities.runOnUIThread(
                () -> showStep(host, menuTarget, composeTarget, 0),
                180
        );
    }

    private static void showStep(
            BaseFragment host,
            View menuTarget,
            View composeTarget,
            int step
    ) {
        if (host.getParentActivity() == null) {
            VayOnboardingState.endQuickTourPresentation();
            return;
        }

        if (step >= STEPS) {
            finishQuickTour(host);
            return;
        }

        View target = step == 0 ? menuTarget : composeTarget;
        if (target == null || !target.isShown() || target.getWidth() == 0 || target.getHeight() == 0) {
            showStep(host, menuTarget, composeTarget, step + 1);
            return;
        }

        int titleRes = step == 0
                ? R.string.vay_first_launch_menu_title
                : R.string.vay_first_launch_compose_title;
        int bodyRes = step == 0
                ? R.string.vay_first_launch_menu_body
                : R.string.vay_first_launch_compose_body;

        VayCoachOverlay.show(
                host.getParentActivity(),
                target,
                LocaleController.formatString(R.string.vay_onboarding_step, step + 1, STEPS),
                LocaleController.getString(titleRes),
                LocaleController.getString(bodyRes),
                LocaleController.getString(
                        step == STEPS - 1
                                ? R.string.vay_coach_done
                                : R.string.vay_coach_next
                ),
                LocaleController.getString(R.string.vay_onboarding_skip),
                new VayCoachOverlay.Callback() {
                    @Override
                    public void onNext() {
                        showStep(host, menuTarget, composeTarget, step + 1);
                    }

                    @Override
                    public void onSkip() {
                        skipAllOnboarding();
                    }

                    @Override
                    public void onTarget() {
                        VayOnboardingState.markQuickTourCompleted();
                    }
                }
        );
    }

    private static void finishQuickTour(BaseFragment host) {
        VayOnboardingState.markQuickTourCompleted();
        AndroidUtilities.runOnUIThread(() -> {
            if (host.getParentActivity() != null && host.getVisibleDialog() == null) {
                VayOnboardingActivity.maybePresent(host);
            }
        }, 220);
    }

    private static void skipAllOnboarding() {
        VayOnboardingState.markCompleted();
    }
}
