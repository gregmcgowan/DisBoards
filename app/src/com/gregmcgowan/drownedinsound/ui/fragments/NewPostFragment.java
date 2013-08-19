package com.gregmcgowan.drownedinsound.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.BoardType;

public class NewPostFragment extends DisBoardsDialogFragment {
    
    private ImageButton newPostButton;
    private EditText postTitleEditText;
    private EditText  postContentEditText;
    
    private BoardType boardType;
    
    public static NewPostFragment newInstance(Bundle passedInData){
	NewPostFragment newPostFragment  = new NewPostFragment();
	newPostFragment.setArguments(passedInData);
	return newPostFragment;
    }
    
    public NewPostFragment (){
	
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
	Dialog dialog = super.onCreateDialog(savedInstanceState);
	dialog.getWindow().setSoftInputMode(
		WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.new_board_post_layout, container,
		false);
	
	return view;
    }
    
    
    
    
    
}
