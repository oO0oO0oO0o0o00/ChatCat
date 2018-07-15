package ac0888bfc.chatcat;

import android.app.Application;
import android.util.Log;

public final class DebuggableApplication extends Application implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultHandler;

    public DebuggableApplication() {
        super();
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            Log.e("unc", Log.getStackTraceString(e));
            defaultHandler.uncaughtException(t, e);
        } catch (Throwable tr) {
        }
    }
}
