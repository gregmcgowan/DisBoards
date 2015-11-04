package com.drownedinsound.ui.post;

import com.drownedinsound.data.model.BoardPostComment;

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

    private View parentLayout;

    private BoardPostComment boardPostComment;


    public AllCommentClickListener(
            RelativeLayout actionLayout,
            View parentLayout,
            BoardPostComment boardPostComment) {
        this.actionLayout = actionLayout;
        this.parentLayout = parentLayout;
        this.boardPostComment = boardPostComment;
    }

    @Override
    public void onClick(View v) {
        final boolean setVisible = actionLayout.getVisibility() != View.VISIBLE;

        float[] offset = setVisible ? new float[]{0, 0.5f, 1}
                : new float[]{1, 0.5f, 0};

        actionLayout.setVisibility(View.VISIBLE);
        // parentLayoutWeakReference.get().bringToFront();
        //parentLayoutWeakReference.get().requestLayout();
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


    private void animateActionLayout(final RelativeLayout actionLayout, final boolean setVisible) {



    }
}
