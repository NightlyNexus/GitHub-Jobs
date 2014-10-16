package com.nightlynexus.githubjobs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class ColorfulView extends View {

    private static final int ANIMATION_DURATION = 700;
    private static final int MAX_WEIGHT = 10;

    public ColorfulView(Context context) {
        super(context);
    }

    public ColorfulView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorfulView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            final Animation animation = new Animation() {

                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
                    params.weight = (1 - interpolatedTime) * MAX_WEIGHT;
                    setLayoutParams(params);
                }
            };
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            animation.setDuration(ANIMATION_DURATION);
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
                    params.weight = MAX_WEIGHT;
                    setLayoutParams(params);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            startAnimation(animation);
        } else {
            clearAnimation();
        }
    }
}
