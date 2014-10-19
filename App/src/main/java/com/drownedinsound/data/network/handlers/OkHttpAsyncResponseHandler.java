package com.drownedinsound.data.network.handlers;

import com.drownedinsound.core.DisBoardsApp;
import com.drownedinsound.data.DatabaseHelper;
import com.drownedinsound.data.UserSessionManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by gregmcgowan on 20/07/2014.
 */
public abstract class OkHttpAsyncResponseHandler implements Callback {

    private boolean updateUI;

    @Inject
    protected DatabaseHelper databaseHelper;

    @Inject
    UserSessionManager userSessionManager;

    @Inject
    EventBus eventBus;

    public OkHttpAsyncResponseHandler(Context context) {
        if (context != null) {
            DisBoardsApp.getApplication(context).inject(this);
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        handleFailure(request, e);
    }

    @Override
    public void onResponse(Response response) throws IOException {
        int responseCode = response.code();
        if (responseCode >= 300) {
            handleFailure(response.request(), new IOException("Response Code " + responseCode));
        } else {
            try {
                handleSuccess(response, new GZIPInputStream(response.body().byteStream()));
            } catch (IOException ioe) {
                handleFailure(response.request(), ioe);
            } catch (IllegalStateException e) {
                handleFailure(response.request(), e);
            }
        }
    }

    public boolean isUpdateUI() {
        return updateUI;
    }

    public void setUpdateUI(boolean updateUI) {
        this.updateUI = updateUI;
    }

    public abstract void handleSuccess(Response response, InputStream inputStream)
            throws IOException;

    public abstract void handleFailure(Request request, Throwable throwable);
}
