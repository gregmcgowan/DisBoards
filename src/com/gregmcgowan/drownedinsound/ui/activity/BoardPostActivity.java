package com.gregmcgowan.drownedinsound.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.handlers.RetrieveBoardPostHandler;
import com.gregmcgowan.drownedinsound.ui.fragments.BoardPostFragment;
import com.gregmcgowan.drownedinsound.utils.FileUtils;

import de.greenrobot.event.EventBus;

/**
 * This will contain a board post fragment. A board post will be made of the
 * original post and comments. This activity will not do much apart from load
 * the fragment
 * 
 * @author Greg
 * 
 */
public class BoardPostActivity extends SherlockFragmentActivity {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardPostActivity";

    private static final String BOARD_POST_FRAGMENT_TAG = "BOARD_POST_FRAGMENT_TAG";

    private FragmentManager fragmentManager;
    private ProgressBar progressBar;

    private boolean requestBoardPost;
    private String boardPostUrl;

    private BoardPostFragment boardPostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.board_post_container);
	fragmentManager = getSupportFragmentManager();
	requestBoardPost = true;
	getSupportActionBar().setHomeButtonEnabled(true);
	initliase(savedInstanceState);
	EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	EventBus.getDefault().unregister(this);
    }

    private void initliase(Bundle savedInstanceState) {
	progressBar = (ProgressBar) findViewById(R.id.board_post_progress_bar);

	if (savedInstanceState == null) {
	    boardPostUrl = (String) getIntent().getExtras().get(
		    DisBoardsConstants.BOARD_POST_URL);
	} else {
	    boardPostUrl = (String) savedInstanceState
		    .getString(DisBoardsConstants.BOARD_POST_URL);
	}

	if (boardPostUrl == null) {
	    Log.d(TAG, "Board post url is null");
	}
    }

    @Override
    protected void onResume() {
	super.onResume();
	if (requestBoardPost) {
	    setProgressBarAndFragmentVisibility(true);
	    HttpClient
		    .requestBoardPost(
			    this,
			    boardPostUrl,
			    new RetrieveBoardPostHandler(FileUtils
				    .createTempFile(this)));
	}
    }

    @Override
    protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
    }

    public void onEventMainThread(RetrievedBoardPostEvent event) {
	BoardPost boardPost = event.getBoardPost();
	if (boardPost != null) {
	    boardPostFragment = (BoardPostFragment) fragmentManager
		    .findFragmentByTag(BOARD_POST_FRAGMENT_TAG);
	    if (boardPostFragment == null) {
		boardPostFragment = new BoardPostFragment();
		Bundle arguments = new Bundle();
		arguments.putParcelable(
			DisBoardsConstants.BOARD_POST_KEY,boardPost);
		boardPostFragment.setArguments(arguments);
	    }
	    displayFragment(boardPostFragment, BOARD_POST_FRAGMENT_TAG);
	    requestBoardPost = false;
	} else {
	    // TODO handle
	}
	setProgressBarAndFragmentVisibility(false);
    }

    private void displayFragment(Fragment fragment, String name) {
	FragmentTransaction transaction = fragmentManager.beginTransaction();
	transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	//transaction.addToBackStack(name);
	transaction.replace(R.id.board_post_fragment_holder, fragment, name);
	transaction.commit();
    }

    public void setProgressBarAndFragmentVisibility(boolean visible) {
	int progressBarVisiblity = visible ? View.VISIBLE : View.INVISIBLE;
	progressBar.setVisibility(progressBarVisiblity);
    }

}
