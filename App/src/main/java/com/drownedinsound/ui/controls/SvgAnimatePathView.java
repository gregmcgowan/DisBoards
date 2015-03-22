/**
 * Copyright 2013 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drownedinsound.ui.controls;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.drownedinsound.R;
import com.drownedinsound.utils.SimpleAnimatorListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class SvgAnimatePathView extends View {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final SvgHelper mSvg = new SvgHelper(mPaint);
    private int mSvgResource;

    private final Object mSvgLock = new Object();
    private List<SvgHelper.SvgPath> mPaths = new ArrayList<SvgHelper.SvgPath>(0);
    private Thread mLoader;

    private float mPhase;
    private float mFadeFactor;
    private int mDuration;
    private float mParallax = 1.0f;

    private AnimatorSet showHidePathAnimatorSet;
    private AtomicBoolean animate;
    private Animator.AnimatorListener animationListener;

    public SvgAnimatePathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SvgAnimatePathView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint.setStyle(Paint.Style.STROKE);
        animate = new AtomicBoolean(false);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnimatedSVG, defStyle, 0);
        try {
            if (a != null) {
                mPaint.setStrokeWidth(a.getFloat(R.styleable.AnimatedSVG_svgStrokeWidth, 1.0f));
                mPaint.setColor(a.getColor(R.styleable.AnimatedSVG_svgStrokeColor, 0xff000000));
                mPhase = a.getFloat(R.styleable.AnimatedSVG_phase, 1.0f);
                mDuration = a.getInt(R.styleable.AnimatedSVG_duration, 4000);
                mFadeFactor = a.getFloat(R.styleable.AnimatedSVG_fadeFactor, 10.0f);
            }
        } finally {
            if (a != null) a.recycle();
        }
    }

    private void updatePathsPhaseLocked() {
        final int count = mPaths.size();
        for (int i = 0; i < count; i++) {
            SvgHelper.SvgPath svgPath = mPaths.get(i);
            svgPath.renderPath.reset();
            svgPath.measure.getSegment(0.0f, svgPath.length * mPhase, svgPath.renderPath, true);
            // Required only for Android 4.4 and earlier
            svgPath.renderPath.rLineTo(0.0f, 0.0f);
        }
    }

    public float getParallax() {
        return mParallax;
    }

    public void setParallax(float parallax) {
        mParallax = parallax;
        invalidate();
    }

    public float getPhase() {
        return mPhase;
    }

    public void setPhase(float phase) {
        mPhase = phase;
        synchronized (mSvgLock) {
            updatePathsPhaseLocked();
        }
        invalidate();
    }

    public int getSvgResource() {
        return mSvgResource;
    }

    public void setSvgResource(int svgResource) {
        mSvgResource = svgResource;
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mLoader != null) {
            try {
                mLoader.join();
            } catch (InterruptedException e) {

            }
        }

        mLoader = new Thread(new Runnable() {
            @Override
            public void run() {
                mSvg.load(getContext(), mSvgResource);
                synchronized (mSvgLock) {
                    mPaths = mSvg.getPathsForViewport(
                            w - getPaddingLeft() - getPaddingRight(),
                            h - getPaddingTop() - getPaddingBottom());
                    updatePathsPhaseLocked();
                }
            }
        }, "SVG Loader");
        mLoader.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mSvgLock) {
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            final int count = mPaths.size();
            for (int i = 0; i < count; i++) {
                SvgHelper.SvgPath svgPath = mPaths.get(i);

                // We use the fade factor to speed up the alpha animation
                int alpha = (int) (Math.min(mPhase * mFadeFactor, 1.0f) * 255.0f);
                svgPath.paint.setAlpha((int) (alpha * mParallax));

                canvas.drawPath(svgPath.renderPath, svgPath.paint);
            }
            canvas.restore();
        }
    }

    public void setAnimationListener(Animator.AnimatorListener animationListener) {
        this.animationListener = animationListener;
    }

    public void startAnimation() {
        if(showHidePathAnimatorSet == null) {
            showHidePathAnimatorSet = new AnimatorSet();

            ObjectAnimator showPathAnimation =  ObjectAnimator.ofFloat(this, "phase", 0.0f, 1.0f);
            showPathAnimation.addListener(new SimpleAnimatorListener(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    if(!animate.get()){
                        showHidePathAnimatorSet.cancel();
                    }
                }
            });
            showPathAnimation.setDuration(mDuration);

            ObjectAnimator hidePathAnimation = ObjectAnimator.ofFloat(this, "phase",1f,0);
            hidePathAnimation.setDuration(mDuration);

            showHidePathAnimatorSet.playSequentially(showPathAnimation,hidePathAnimation);
            showHidePathAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if(animationListener != null) {
                        animationListener.onAnimationStart(animation);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(animate.get()) {
                        startAnimation();
                    }
                    if(animationListener != null) {
                        animationListener.onAnimationEnd(animation);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if(animationListener != null) {
                        animationListener.onAnimationCancel(animation);
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                      if(animationListener != null) {
                          animationListener.onAnimationRepeat(animation);
                      }
                }
            });
        }
        animate.set(true);
        showHidePathAnimatorSet.start();
    }


    public void stopAnimation(){
        if(showHidePathAnimatorSet != null) {
            showHidePathAnimatorSet.cancel();
        }
    }

    public void stopAnimationOnceFinished(){
        if(showHidePathAnimatorSet != null) {
            animate.set(false);
        }
    }
}


