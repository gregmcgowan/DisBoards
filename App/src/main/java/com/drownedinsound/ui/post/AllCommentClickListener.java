package com.drownedinsound.ui.post;

import com.drownedinsound.data.generatered.BoardPostComment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;


/**
 * Created by gregmcgowan on 13/08/15.
 */
public class AllCommentClickListener  implements View.OnClickListener{

    private RelativeLayout actionLayout;

    private BoardPostComment boardPostComment;


    public AllCommentClickListener(
            RelativeLayout actionLayout,
            BoardPostComment boardPostComment) {
        this.actionLayout = actionLayout;
        this.boardPostComment = boardPostComment;
    }

    @Override
    public void onClick(View v) {
        final boolean setVisible = actionLayout.getVisibility() != View.VISIBLE;

        float[] offset = setVisible ? new float[]{0, 0.5f, 1}
                : new float[]{1, 0.5f, 0};

        actionLayout.setVisibility(View.VISIBLE);

        ObjectAnimator removeObjectAnimator = ObjectAnimator.ofFloat(
                actionLayout, "scaleY", offset);
        removeObjectAnimator.setDuration(500);
        removeObjectAnimator.setInterpolator(new AccelerateInterpolator());
        removeObjectAnimator.addListener(new Animator.AnimatorListener() {

            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                if (setVisible) {
                    actionLayout.setVisibility(View.VISIBLE);
                } else {
                    actionLayout.setVisibility(View.GONE);
                }
                boardPostComment.setActionSectionVisible(setVisible);

            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }

        });
        removeObjectAnimator.start();
    }

}
