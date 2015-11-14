package com.drownedinsound.ui.base;

import com.drownedinsound.R;
import com.drownedinsound.ui.controls.SvgAnimatePathView;
import com.drownedinsound.utils.SimpleAnimatorListener;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import timber.log.Timber;

/**
 * Created by gregmcgowan on 14/11/15.
 */
public class DisBoardsLoadingLayout extends FrameLayout {

    SvgAnimatePathView animatedLogo;

    private View contentView;

    private ContentShownListener contentShownListener;

    private View rootView;

    public DisBoardsLoadingLayout(Context context) {
        super(context);
        initialise();
    }

    public DisBoardsLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    public DisBoardsLoadingLayout(Context context, AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise();
    }


    private void initialise(){
        LayoutInflater.from(getContext()).inflate(R.layout.loading_layout,this,true);
        animatedLogo = (SvgAnimatePathView) findViewById(R.id.animated_logo);
        animatedLogo.setSvgResource(R.raw.logo);
        setVisibility(GONE);
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public void setContentShownListener(
            ContentShownListener contentShownListener) {
        this.contentShownListener = contentShownListener;
    }

    public void showAnimatedViewAndHideContent(){
        Timber.d("Board  showAnimatedLogoAndHideList ");
        if (contentView.getVisibility() == View.VISIBLE) {
            contentView.setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
            Timber.d("Loading view start animation");
            animatedLogo.startAnimation();
        } else {
            if (!animatedLogo.animationInProgress()) {
                Timber.d("start animation");
                animatedLogo.startAnimation();
            }
        }
    }

    public void hideAnimatedViewAndShowContent(){
        Timber.d("hide logo and show list");
        if (getVisibility() == View.VISIBLE) {
            Timber.d("fade out animated logo");
            animatedLogo.setAnimationListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Timber.d("Animation end");
                    ObjectAnimator showList = ObjectAnimator.ofFloat(contentView, "alpha", 0f, 1f);
                    showList.addListener(new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            contentView.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setVisibility(View.GONE);
                            contentView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            setVisibility(View.GONE);
                            contentView.setVisibility(View.VISIBLE);
                            if(contentShownListener != null) {
                                contentShownListener.onContentShown();
                            }
                        }
                    });
                    showList.start();
                }


            });
            animatedLogo.stopAnimationOnceFinished();
        } else {
            animatedLogo.stopAnimationOnceFinished();
            setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            if(contentShownListener != null) {
                contentShownListener.onContentShown();
            }
        }

    }

    public void stopAnimation(){
        animatedLogo.stopAnimation();
    }


    public static interface ContentShownListener {

        void onContentShown();
    }
}
