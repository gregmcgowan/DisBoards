package com.drownedinsound.data.network.handlers;

import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.data.database.DatabaseHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by gregmcgowan on 20/07/2014.
 */
public abstract class ResponseHandler implements Callback {

    @Inject
    UserSessionManager userSessionManager;

    @Inject
    EventBus eventBus;

    private int uiID;

    private boolean updateUI;

    public ResponseHandler() {

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
                String encodingHeader = response.header("Content-Encoding");
                boolean gzipped = encodingHeader != null && encodingHeader.contains("gzip");
                if (gzipped) {
                    handleSuccess(response, new GZIPInputStream(response.body().byteStream()));
                } else {
                    handleSuccess(response, response.body().byteStream());
                }

            } catch (IOException ioe) {
                handleFailure(response.request(), ioe);
            } catch (IllegalStateException e) {
                handleFailure(response.request(), e);
            } finally {
                //response.body().close();
            }
        }
    }

    public int getUiID() {
        return uiID;
    }

    protected void setUiID(int uiID) {
        this.uiID = uiID;
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
