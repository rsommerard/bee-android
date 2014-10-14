package com.apisense.bee.backend.user;

import android.os.AsyncTask;
import android.util.Log;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import fr.inria.bsense.APISENSE;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */

public class SignInTask extends AsyncTask<String, Void, String> {
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTasksCallbacks listener;
    private final String DEFAULT_URL = "http://beta.apisense.io/hive";
    public SignInTask(AsyncTasksCallbacks listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        // params[0] == login
        // params[1] == password
        // params[2] == URL hive (optionnal)
        if (params[0].isEmpty() || params[1].isEmpty()) {
            Log.e(this.TAG, "login or password is empty");
            return "error";
        }

        try {
            if (!params[2].isEmpty())
                APISENSE.apisServerService().setCentralHost(params[2]);
            else
                APISENSE.apisServerService().setCentralHost(this.DEFAULT_URL);
            APISENSE.apisServerService().connect(params[0], params[1]);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        if (!APISENSE.apisServerService().isConnected())
            return "error";
        else {
            try {
                APISENSE.apisServerService().updateUserAccount();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "success";
        }

    }

    @Override
    protected void onPostExecute(final String response) {
        this.listener.onTaskCompleted(response);
    }

    @Override
    protected void onCancelled() {
        this.listener.onTaskCanceled();
    }
}