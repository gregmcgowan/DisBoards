package com.drownedinsound.ui.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import android.text.TextUtils;

/**
 * A simple dialog that will display a message and a positive and negative
 * button below it
 * 
 * @author gmcgowan
 * 
 */
public class SimpleDialogFragment extends DialogFragment {

	private static final String POSITIVE_BUTTON_KEY = "POSITIVE_BUTTON";
	private static final String NEGATIVE_BUTTON_KEY = "NEGATIVE_BUTTON";
	private static final String MESSAGE_KEY = "MESSAGE_KEY";
	private static final String TITLE_KEY = "TITLE_KEY";

	private OnClickListener onClickListener;

	public OnClickListener getOnClickListener() {
		return onClickListener;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public static SimpleDialogFragment newInstance(
			OnClickListener onClickListener, CharSequence title, CharSequence message,
			CharSequence positiveButtonText, CharSequence negativeButtonText) {
		SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
		simpleDialogFragment.setOnClickListener(onClickListener);

		Bundle argumentsBundle = new Bundle();

		argumentsBundle.putCharSequence(TITLE_KEY, title);
		argumentsBundle.putCharSequence(MESSAGE_KEY, message);
		argumentsBundle.putCharSequence(POSITIVE_BUTTON_KEY, positiveButtonText);
		argumentsBundle.putCharSequence(NEGATIVE_BUTTON_KEY, negativeButtonText);

		simpleDialogFragment.setArguments(argumentsBundle);

		return simpleDialogFragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		OnClickListenerWrapper onClickListenerWrapper = new OnClickListenerWrapper();

		Bundle arguments = getArguments();
		CharSequence title = arguments.getCharSequence(TITLE_KEY);
		CharSequence message = arguments.getCharSequence(MESSAGE_KEY);
		CharSequence positiveButtonText = arguments.getCharSequence(POSITIVE_BUTTON_KEY);
		CharSequence negativeButtonText = arguments.getCharSequence(NEGATIVE_BUTTON_KEY);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		if (!TextUtils.isEmpty(title)) {
			builder.setTitle(title);
		}

		if (!TextUtils.isEmpty(message)) {
			builder.setMessage(message);
		}

		if (!TextUtils.isEmpty(positiveButtonText)) {
			builder.setPositiveButton(positiveButtonText,
					onClickListenerWrapper);
		}

		if (!TextUtils.isEmpty(negativeButtonText)) {
			builder.setNegativeButton(negativeButtonText,
					onClickListenerWrapper);
		}

		return builder.create();
	}

	private class OnClickListenerWrapper implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (getOnClickListener() != null) {
				getOnClickListener().onClick(dialog, which);
			}
		}

	}
}
