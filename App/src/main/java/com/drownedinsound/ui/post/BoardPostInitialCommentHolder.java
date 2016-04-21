package com.drownedinsound.ui.post;


import com.drownedinsound.ui.controls.ActiveTextView;

import android.view.View;
import android.widget.TextView;

/**
 * Created by gregmcgowan on 13/08/15.
 */
class BoardPostInitialCommentHolder extends BaseBoardPostHolder {

    public BoardPostInitialCommentHolder(View itemView) {
        super(itemView);
    }

    public TextView commentTitleTextView;

    public ActiveTextView commentContentTextView;

    public TextView commentAuthorTextView;

    public TextView commentDateTimeTextView;

    public TextView noOfCommentsTextView;
}
