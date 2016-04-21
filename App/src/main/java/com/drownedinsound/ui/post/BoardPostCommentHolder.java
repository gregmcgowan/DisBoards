package com.drownedinsound.ui.post;

import com.drownedinsound.ui.controls.ActiveTextView;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by gregmcgowan on 13/08/15.
 */
class BoardPostCommentHolder extends BaseBoardPostHolder{

    public BoardPostCommentHolder(View itemView) {
        super(itemView);
    }

    public TextView commentTitleTextView;

    public ActiveTextView commentContentTextView;

    public TextView commentAuthorTextView;

    public TextView commentDateTimeTextView;

    public TextView commentThisSectionTextView;

    public LinearLayout whitespaceLayout;

    public RelativeLayout actionRelativeLayout;

    public TextView thisTextView;

    public TextView replyTextView;

    public LinearLayout commentSection;
}
