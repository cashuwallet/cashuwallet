package com.cashuwallet.android;

import android.os.AsyncTask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class BackgroundTask extends AsyncTask<Void, Void, Boolean> {

    public static void run(ExecutorService exec, Callable<Boolean> main) {
        new BackgroundTask(main, null, true).executeOnExecutor(exec);
    }

    public static void run(ExecutorService exec, Callable<Boolean> main, Continuation<Boolean> cont) {
        new BackgroundTask(main, cont, true).executeOnExecutor(exec);
    }

    public static void run(ExecutorService exec, Callable<Boolean> main, Continuation<Boolean> cont, boolean requiresNetwork) {
        new BackgroundTask(main, cont, requiresNetwork).executeOnExecutor(exec);
    }

    private final Callable<Boolean> main;
    private final Continuation<Boolean> cont;
    private boolean requiresNetwork;

    private BackgroundTask(Callable<Boolean> main, Continuation<Boolean> cont, boolean requiresNetwork) {
        this.main = main;
        this.cont = cont;
        this.requiresNetwork = requiresNetwork;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success = false;
        do {
            if (MainApplication.app().shuttingDown()) break;
            if (requiresNetwork) {
                if (!MainApplication.app().networkAvailable()) break;
            }
            try {
                success = main.call();
            } catch (Exception e) {
                success = false;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        } while (!success);
        return success;
    }

    @Override
    protected final void onPostExecute(Boolean result) {
        if (cont != null) cont.cont(result);
    }

}
