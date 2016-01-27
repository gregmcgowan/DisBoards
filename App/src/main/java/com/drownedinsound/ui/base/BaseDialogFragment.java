package com.drownedinsound.ui.base;

import com.drownedinsound.core.DisBoardsApp;
import com.drownedinsound.data.network.DisApiClient;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Window;


public class BaseDialogFragment extends DialogFragment {

    protected DisApiClient disApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisBoardsApp.getApplication(getActivity()).inject(this);

        disApiClient = DisBoardsApp.getApplication(getActivity()).getDisApiClient();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}
