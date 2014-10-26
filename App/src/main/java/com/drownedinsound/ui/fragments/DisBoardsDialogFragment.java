package com.drownedinsound.ui.fragments;

import com.drownedinsound.core.DisBoardsApp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Window;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class DisBoardsDialogFragment extends DialogFragment {

    @Inject
    protected EventBus eventBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisBoardsApp.getApplication(getActivity()).inject(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}
