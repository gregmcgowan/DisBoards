package com.gregmcgowan.drownedinsound.ui.fragments;

import com.actionbarsherlock.app.SherlockDialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

public class DisBoardsDialogFragment extends SherlockDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}
