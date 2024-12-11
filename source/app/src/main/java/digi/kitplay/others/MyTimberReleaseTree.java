package digi.kitplay.others;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import timber.log.Timber;

public class MyTimberReleaseTree extends Timber.Tree {
    private final FirebaseCrashlytics firebaseCrashlytics;

    public MyTimberReleaseTree(FirebaseCrashlytics firebaseCrashlytics){
        this.firebaseCrashlytics = firebaseCrashlytics;
    }



    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority != Log.VERBOSE && priority != Log.DEBUG) {
            // Sending crash report to Firebase CrashAnalytics
            firebaseCrashlytics.recordException(t);
        }
    }
}
