package com.bluetoothscanning;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class PulsatorLayout extends RelativeLayout {

    public static final int INFINITE = 0;

    public static final int INTERP_LINEAR = 0;
    public static final int INTERP_ACCELERATE = 1;
    public static final int INTERP_DECELERATE = 2;
    public static final int INTERP_ACCELERATE_DECELERATE = 3;

    private static final int DEFAULT_COUNT = 4;
    private static final int DEFAULT_COLOR = Color.rgb(0, 116, 193);
    private static final int DEFAULT_DURATION = 7000;
    private static final int DEFAULT_REPEAT = INFINITE;
    private static final boolean DEFAULT_START_FROM_SCRATCH = true;
    private static final int DEFAULT_INTERPOLATOR = INTERP_LINEAR;
    private final List<View> mViews = new ArrayList<>();
    private int mCount;
    private int mDuration;
    private int mRepeat;
    private boolean mStartFromScratch;
    private int mColor;
    private int mInterpolator;
    private AnimatorSet mAnimatorSet;
    private Paint mPaint;
    private float mRadius;
    private float mCenterX;
    private float mCenterY;
    private boolean mIsStarted;
    private List<Integer> regions = new ArrayList<>();
    private ArrayList<DetectedDevice> detectedDevices;
    private RelativeLayout detectedDevice;
    private HashMap<String, BluetoothDevice> bluetoothDeviceHashMap;
    private int prev_radii = -1;
    private int prev_cor = 0;
    private IDetected listener;
    private ArrayList avators = new ArrayList<>();
    private Random cor;


    private final Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animator) {
            mIsStarted = true;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mIsStarted = false;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            mIsStarted = false;
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }

    };


    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getTag() != null && listener != null) {
                BluetoothDevice device = (BluetoothDevice) view.getTag();
                listener.onSelectedDevice(device);
            }
        }
    };


    public PulsatorLayout(Context context) {
        this(context, null, 0);
    }

    public PulsatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PulsatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // get attributes
        TypedArray attr = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.Pulsator4Droid, 0, 0);

        mCount = DEFAULT_COUNT;
        mDuration = DEFAULT_DURATION;
        mRepeat = DEFAULT_REPEAT;
        mStartFromScratch = DEFAULT_START_FROM_SCRATCH;
        mColor = DEFAULT_COLOR;
        mInterpolator = DEFAULT_INTERPOLATOR;
        detectedDevices = new ArrayList<>();
        bluetoothDeviceHashMap = new HashMap<>();
        cor = new Random();

        try {
            mCount = attr.getInteger(R.styleable.Pulsator4Droid_pulse_count, DEFAULT_COUNT);
            mDuration = attr.getInteger(R.styleable.Pulsator4Droid_pulse_duration,
                    DEFAULT_DURATION);
            mRepeat = attr.getInteger(R.styleable.Pulsator4Droid_pulse_repeat, DEFAULT_REPEAT);
            mStartFromScratch = attr.getBoolean(R.styleable.Pulsator4Droid_pulse_startFromScratch,
                    DEFAULT_START_FROM_SCRATCH);
            mColor = attr.getColor(R.styleable.Pulsator4Droid_pulse_color, DEFAULT_COLOR);
            mInterpolator = attr.getInteger(R.styleable.Pulsator4Droid_pulse_interpolator,
                    DEFAULT_INTERPOLATOR);
        } finally {
            attr.recycle();
        }

        // create paint
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8f);
        mPaint.setColor(mColor);

        // create views
        build();
    }

    /**
     * Create interpolator from type.
     *
     * @param type Interpolator type as int
     * @return Interpolator object of type
     */
    private static Interpolator createInterpolator(int type) {
        switch (type) {
            case INTERP_ACCELERATE:
                return new AccelerateInterpolator();
            case INTERP_DECELERATE:
                return new DecelerateInterpolator();
            case INTERP_ACCELERATE_DECELERATE:
                return new AccelerateDecelerateInterpolator();
            default:
                return new LinearInterpolator();
        }
    }

    public ArrayList getAvators() {
        return avators;
    }

    public void setAvators(ArrayList avators) {
        this.avators = avators;
    }

    public IDetected getListener() {
        return listener;
    }

    public void setListener(IDetected listener) {
        this.listener = listener;
    }

    /**
     * Start pulse animation.
     */
    public synchronized void start() {
        if (mAnimatorSet == null || mIsStarted) {
            return;
        }

        mAnimatorSet.start();

        if (!mStartFromScratch) {
            ArrayList<Animator> animators = mAnimatorSet.getChildAnimations();
            for (Animator animator : animators) {
                ObjectAnimator objectAnimator = (ObjectAnimator) animator;

                long delay = objectAnimator.getStartDelay();
                objectAnimator.setStartDelay(0);
                objectAnimator.setCurrentPlayTime(mDuration - delay);
            }
        }
    }

    /**
     * Stop pulse animation.
     */
    public synchronized void stop() {
        if (mAnimatorSet == null || !mIsStarted) {
            return;
        }

        mAnimatorSet.end();
    }

    public synchronized boolean isStarted() {
        return (mAnimatorSet != null && mIsStarted);
    }

    /**
     * Get number of pulses.
     *
     * @return Number of pulses
     */
    public int getCount() {
        return mCount;
    }

    /**
     * Set number of pulses.
     *
     * @param count Number of pulses
     */
    public void setCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }

        if (count != mCount) {
            mCount = count;
            reset();
            invalidate();
        }
    }

    /**
     * Get pulse duration.
     *
     * @return Duration of single pulse in milliseconds
     */
    public int getDuration() {
        return mDuration;
    }

    /**
     * Set single pulse duration.
     *
     * @param millis Pulse duration in milliseconds
     */
    public void setDuration(int millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        if (millis != mDuration) {
            mDuration = millis;
            reset();
            invalidate();
        }
    }

    /**
     * Gets the current color of the pulse effect in integer
     * Defaults to Color.rgb(0, 116, 193);
     *
     * @return an integer representation of color
     */
    public int getColor() {
        return mColor;
    }

    /**
     * Sets the current color of the pulse effect in integer
     * Takes effect immediately
     * Usage: Color.parseColor("<hex-value>") or getResources().getColor(R.color.colorAccent)
     *
     * @param color : an integer representation of color
     */
    public void setColor(int color) {
        if (color != mColor) {
            this.mColor = color;

            if (mPaint != null) {
                mPaint.setColor(color);
            }
        }
    }

    /**
     * Get current interpolator type used for animating.
     *
     * @return Interpolator type as int
     */
    public int getInterpolator() {
        return mInterpolator;
    }

    /**
     * Set current interpolator used for animating.
     *
     * @param type Interpolator type as int
     */
    public void setInterpolator(int type) {
        if (type != mInterpolator) {
            mInterpolator = type;
            reset();
            invalidate();
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        mCenterX = width * 0.5f;
        mCenterY = height * 0.5f;
        mRadius = Math.min(width, height) * 0.5f;
        regions.clear();
        for (int i = 0; i < mCount; i++)
            regions.add((int) ((mRadius / mCount) * (i + 1)));

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Remove all views and animators.
     */
    private void clear() {
        // remove animators
        stop();

        // remove old views
        for (View view : mViews) {
            removeView(view);
        }
        mViews.clear();
    }

    /**
     * Build pulse views and animators.
     */
    private void build() {
        // create views and animators
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        int repeatCount = (mRepeat == INFINITE) ? ObjectAnimator.INFINITE : mRepeat;

        List<Animator> animators = new ArrayList<>();

        for (int index = 0; index < mCount; index++) {
            // setup view
            PulseView pulseView = new PulseView(getContext());
            pulseView.setScaleX(0);
            pulseView.setScaleY(0);
            pulseView.setAlpha(1);

            addView(pulseView, index, layoutParams);
            mViews.add(pulseView);

            long delay = index * mDuration / mCount;

            // setup animators
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(pulseView, "ScaleX", 0f, 1f);
            scaleXAnimator.setRepeatCount(repeatCount);
            scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleXAnimator.setStartDelay(delay);
            animators.add(scaleXAnimator);

            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(pulseView, "ScaleY", 0f, 1f);
            scaleYAnimator.setRepeatCount(repeatCount);
            scaleYAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleYAnimator.setStartDelay(delay);
            animators.add(scaleYAnimator);

            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(pulseView, "Alpha", 1f, 0f);
            alphaAnimator.setRepeatCount(repeatCount);
            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
            alphaAnimator.setStartDelay(delay);
            animators.add(alphaAnimator);
        }

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animators);
        mAnimatorSet.setInterpolator(createInterpolator(mInterpolator));
        mAnimatorSet.setDuration(mDuration);
        mAnimatorSet.addListener(mAnimatorListener);

        //AddView Bluetooth deviceDetected
        detectedDevice = new RelativeLayout(getContext());
        addView(detectedDevice, layoutParams);
    }

    public void addImageview(float x, float y, BluetoothDevice device, boolean isDetecteddevice) {

        x = x - (regions.get(0) / 2F);
        y = y - (regions.get(0) / 2F);


        if (!detectedDevices.contains(new DetectedDevice(x, y))) {
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            LinearLayout l = new LinearLayout(getContext());
            l.setGravity(Gravity.CENTER);
            l.setX(x);
            l.setY(y);
            l.setTag(device);
            l.setOrientation(LinearLayout.VERTICAL); // set orientation
            l.setLayoutParams(layoutParams);
            l.setOnClickListener(onClickListener);

            //ProfileImage
            LayoutParams imglayoutParams = new LayoutParams((int) getResources().getDimension(R.dimen.fourtyeight), (int) getResources().getDimension(R.dimen.fourtyeight));
            ImageView useravator = new ImageView(getContext());
            useravator.setLayoutParams(imglayoutParams);
            useravator.setImageDrawable(resize(getResources().getDrawable(getRandomAvators()), regions.get(0)));
            l.addView(useravator);

            //name of the device
            TextView name = new TextView(getContext());
            name.setTextColor(getResources().getColor(R.color.white));
            name.setLayoutParams(layoutParams);

            name.setText(TextUtils.isEmpty(device.getName()) ? "UNKNOWN" : device.getName());

            l.addView(name);

            detectedDevice.addView(l);

            detectedDevices.add(new DetectedDevice(x, y));
        }
    }

    /**
     * Reset views and animations.
     */
    private void reset() {
        boolean isStarted = isStarted();
        prev_radii = -1;

        clear();
        build();

        if (isStarted) {
            start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

    private Drawable resize(Drawable image, int size) {
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, size, size, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    public void clearedDetectedDevices() {
        detectedDevice.removeAllViews();
        detectedDevices.clear();
        bluetoothDeviceHashMap.clear();
        prev_radii = -1;
    }

    public void addDetecteddevice(BluetoothDevice device) {


        if (device != null && !TextUtils.isEmpty(device.getAddress()) && !bluetoothDeviceHashMap.containsKey(device.getAddress())) {
            bluetoothDeviceHashMap.put(device.getAddress(), device);
            double toDegrees = (getRandomCordinatesinPercentage() / 100F) * (2 * Math.PI);
            int radii = getRandomRadius();
            float cor_x = (float) (mCenterX + Math.cos(toDegrees) * regions.get(radii));
            float cor_y = (float) (mCenterY + Math.sin(toDegrees) * regions.get(radii));
            addImageview(cor_x, cor_y, device, true);
        }


    }

    private class PulseView extends View {


        public PulseView(Context context) {
            super(context);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
        }

    }

    private int getRandomRadius() {
        int radii = new Random().nextInt((regions.size() - 2)) + 1;

        if (prev_radii != radii) {
            prev_radii = radii;
            return radii;
        } else
            return getRandomRadius();
    }

    private int getRandomCordinatesinPercentage() {
        //Link i referred to learn is https://qiita.com/YuYuYuYoYoYo/items/8295a7db1cc818b00d15

        int percentage = cor.nextInt(75);

        if (percentage != prev_cor) {
            prev_cor = percentage;
            return percentage + 25;
        } else
            return getRandomCordinatesinPercentage();

    }

    private int getRandomAvators() {
        return (int) avators.get(new Random().nextInt(avators.size()));
    }

}
