package com.drownedinsound.ui.post;

import com.drownedinsound.data.model.BoardPostComment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;

/**
 * Created by gregmcgowan on 13/08/15.
 */
public class AllCommentClickListener {

    private WeakReference<RelativeLayout> actionLayoutWeakReference;

    private WeakReference<View> parentLayoutWeakReference;

    private WeakReference<BoardPostListAdapter> adapterWeakReference;

    public AllCommentClickListener(
            WeakReference<RelativeLayout> actionLayout,
            WeakReference<View> parentLayoutWeakReference,
            WeakReference<BoardPostListAdapter> adapterWeakReference) {
        this.actionLayoutWeakReference = actionLayout;
        this.parentLayoutWeakReference = parentLayoutWeakReference;
        this.adapterWeakReference = adapterWeakReference;
    }


    public void doCommentClickAction(View parentView, int position) {
        RelativeLayout actionLayout = actionLayoutWeakReference.get();
        if (actionLayout != null) {
            boolean initallyVisible = actionLayout.getVisibility() == View.VISIBLE;
            animateActionLayout(actionLayout, position, !initallyVisible);
        }
    }

    private void animateActionLayout(final RelativeLayout actionLayout,
            int position, final boolean setVisible) {
        BoardPostComment comment = null;
        BoardPostListAdapter boardPostListAdapter = adapterWeakReference
                .get();
        if (boardPostListAdapter != null) {
            comment = boardPostListAdapter.getItem(position);
        }

        if (comment != null) {
            comment.setActionSectionVisible(setVisible);
        }

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

            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }

        });
        removeObjectAnimator.start();
    }
}
