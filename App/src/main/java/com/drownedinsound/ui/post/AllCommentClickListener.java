package com.drownedinsound.ui.post;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

class AllCommentClickListener  implements View.OnClickListener{

    private ViewGroup itemViewGroup;

    private TextView thisTextView;

    private TextView replyTextView;


    AllCommentClickListener(
            ViewGroup itemViewGroup,
            TextView thisTextView,
            TextView replyTextView) {
        this.replyTextView = replyTextView;
        this.thisTextView = thisTextView;
        this.itemViewGroup = itemViewGroup;
    }

    @Override
    public void onClick(View v) {
        final boolean setVisible = thisTextView.getVisibility() != View.VISIBLE;

        float[] offset = setVisible ? new float[]{0, 0.5f, 1}
                : new float[]{1, 0.5f, 0};

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition((RecyclerView) itemViewGroup.getParent());
            if(setVisible) {
                replyTextView.setVisibility(View.VISIBLE);
                thisTextView.setVisibility(View.VISIBLE);
            } else {
                replyTextView.setVisibility(View.GONE);
                thisTextView.setVisibility(View.GONE);

            }
        } else {
            AnimatorSet showHideReplyAndThis = new AnimatorSet();
            replyTextView.setVisibility(View.VISIBLE);
            thisTextView.setVisibility(View.VISIBLE);

            ObjectAnimator thisAnimator = ObjectAnimator.ofFloat(
                    thisTextView, "scaleY", offset);
            thisAnimator.setDuration(500);
            thisAnimator.setInterpolator(new AccelerateInterpolator());
            thisAnimator.addListener(new Animator.AnimatorListener() {

                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    if (setVisible) {
                        thisTextView.setVisibility(View.VISIBLE);
                    } else {
                        thisTextView.setVisibility(View.GONE);
                    }
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });

            ObjectAnimator replyAnimator = ObjectAnimator.ofFloat(
                    replyTextView, "scaleY", offset);
            replyAnimator.setDuration(500);
            replyAnimator.setInterpolator(new AccelerateInterpolator());
            replyAnimator.addListener(new Animator.AnimatorListener() {

                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    if (setVisible) {
                        replyTextView.setVisibility(View.VISIBLE);
                    } else {
                        replyTextView.setVisibility(View.GONE);
                    }
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
            showHideReplyAndThis.playTogether(replyAnimator,thisAnimator);
            showHideReplyAndThis.start();
        }
    }

}
