package com.example.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.DateFormat;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SimpleFace extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        /* provide your watch face implementation */
        return new Engine();
    }
    static final String COLON_STRING = ":";
    private static final Typeface BOLD_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    private class Engine extends CanvasWatchFaceService.Engine {

        static final int MSG_UPDATE_TIME = 0;
        private static final long INTERACTIVE_UPDATE_RATE_MS = 500;


        Calendar mCalendar;

        // device features
        boolean mLowBitAmbient;


        // graphic objects
        Bitmap mBackgroundBitmap,artclear;
        Bitmap mBackgroundScaledBitmap;
        Paint mHourPaint;
        Paint mMinutePaint;
        private boolean mRegisteredTimeZoneReceiver=false;
        private boolean mBurnInProtection;
        Bitmap mGrayBackgroundBitmap;
        private float mXOffset,mYOffset;

        Paint mDatePaint;
        Paint mSecondPaint;
        Paint mTempaint;
        Paint mColonPaint,LinePaint;
        float mColonWidth,mLinehieght,mpadding;
        Date mDate;
        SimpleDateFormat mDayOfWeekFormat;
        java.text.DateFormat mDateFormat;


        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }



        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler
                                    .sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            if (mBackgroundScaledBitmap == null
                    || mBackgroundScaledBitmap.getWidth() != width
                    || mBackgroundScaledBitmap.getHeight() != height) {
                if(!isInAmbientMode())
                mBackgroundScaledBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                        width, height, true /* filter */);
                else
                    mBackgroundScaledBitmap = Bitmap.createScaledBitmap(mGrayBackgroundBitmap,
                            width, height, true /* filter */);

            }
            super.onSurfaceChanged(holder, format, width, height);


        }

        private Paint createTextPaint(int defaultInteractiveColor) {
            return createTextPaint(defaultInteractiveColor, NORMAL_TYPEFACE);
        }
        private Paint createTextPaint(int defaultInteractiveColor, Typeface typeface) {
            Paint paint = new Paint();
            paint.setColor(defaultInteractiveColor);
            paint.setTypeface(typeface);

            paint.setAntiAlias(true);
            return paint;
        }
        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            /* initialize your watch face */
            Resources resources = SimpleFace.this.getResources();
            Drawable backgroundDrawable = resources.getDrawable(R.drawable.bg, null);
            mBackgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
            mGrayBackgroundBitmap=((BitmapDrawable)resources.getDrawable(R.drawable.bgg,null)).getBitmap();
            artclear=((BitmapDrawable)resources.getDrawable(R.drawable.ic_clear,null)).getBitmap();
            // create graphic styles

            mHourPaint = createTextPaint(Color.WHITE,BOLD_TYPEFACE);
            mMinutePaint=createTextPaint(Color.WHITE);
            mDatePaint = createTextPaint(Color.WHITE);
            mYOffset=resources.getDimension(R.dimen.digital_y_offset);

            mSecondPaint = createTextPaint(Color.WHITE);
            mTempaint = createTextPaint(Color.WHITE);
            mColonPaint = createTextPaint(Color.WHITE);
            mLinehieght=resources.getDimension(R.dimen.digital_line_height);
            mpadding=resources.getDimension(R.dimen.content_padding_start);
            LinePaint=new Paint();
            LinePaint.setColor(Color.WHITE);
            LinePaint.setStrokeWidth(1f);



mDate=new Date();
            // allocate a Calendar to calculate local time using the UTC time and time zone
            mCalendar = Calendar.getInstance();
            setWatchFaceStyle(new WatchFaceStyle.Builder(SimpleFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle
                            .BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());
            initFormats();

            
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            /* get device features (burn-in, low-bit ambient) */
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION,
                    false);
        }
        private void initFormats() {
            mDayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            mDayOfWeekFormat.setCalendar(mCalendar);
            mDateFormat = DateFormat.getDateFormat(SimpleFace.this);
            mDateFormat.setCalendar(mCalendar);
        }

        String getdate()
        {
          String day=mDayOfWeekFormat.format(mDate).substring(0,3)+",";
            String date=mDateFormat.format(mDate);


            return day+date;
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            /* the time changed */
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            /* the wearable switched between modes */
            if (mLowBitAmbient) {
                boolean antiAlias = !inAmbientMode;
                mHourPaint.setAntiAlias(antiAlias);
                mMinutePaint.setAntiAlias(antiAlias);


            }
            invalidate();
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */
            mCalendar.setTimeInMillis(System.currentTimeMillis());

            // Constant to help calculate clock hand rotations
            final float TWO_PI = (float) Math.PI * 2f;

            int width = bounds.width();
            int height = bounds.height();
            if (isInAmbientMode() && (mLowBitAmbient || mBurnInProtection)) {
                canvas.drawColor(Color.BLACK);
            } else if (isInAmbientMode()) {
                canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);
            } else {
                canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);
            }

          //  canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);

            // Find the center. Ignore the window insets so that, on round watches
            // with a "chin", the watch face is centered on the entire screen, not
            // just the usable portion.

            float x=mXOffset;
            float y=mYOffset;

            int min= mCalendar.get(Calendar.MINUTE);
            int hour=mCalendar.get(Calendar.HOUR_OF_DAY);
            String high="26"+"\u00B0";
            String low="16"+"\u00B0";
            String str_hour,str_minute;
            if(hour<10)
             str_hour="0"+hour;
            else
             str_hour=""+hour;
            if(min<10)
                str_minute="0"+min;
            else
                str_minute=""+min;

            canvas.drawText(str_hour,x,y,mHourPaint);
            x+=mHourPaint.measureText(str_hour);
            canvas.drawText(COLON_STRING,x,y,mColonPaint);
            x+=mColonPaint.measureText(COLON_STRING);
            canvas.drawText(str_minute,x,y,mMinutePaint);
            y+=mLinehieght;

            if (getPeekCardPosition().isEmpty() && !isInAmbientMode()) {
                // Day of week

                canvas.drawText(getdate(),mXOffset,y,mDatePaint);
                y+=mLinehieght;
                canvas.drawLine(mXOffset+mpadding*3,y,mXOffset+7*mpadding,y,LinePaint);
                y+=mLinehieght-20f;
                canvas.drawBitmap(artclear,mXOffset-2*mpadding,y,null);
                x=mXOffset-2*mpadding+90;
                y+=mLinehieght+20f;
                canvas.drawText(high,x,y,mTempaint);
                x+=mTempaint.measureText(high)+20;
                canvas.drawText(low,x,y,mTempaint);

            }




//            // Compute rotations and lengths for the clock hands.
//            float seconds = mCalendar.get(Calendar.SECOND) +
//                    mCalendar.get(Calendar.MILLISECOND) / 1000f;
//            float secRot = seconds / 60f * TWO_PI;
//            float minutes = mCalendar.get(Calendar.MINUTE) + seconds / 60f;
//            float minRot = minutes / 60f * TWO_PI;
//            float hours = mCalendar.get(Calendar.HOUR) + minutes / 60f;
//            float hrRot = hours / 12f * TWO_PI;
//
//            float secLength = centerX - 20;
//            float minLength = centerX - 40;
//            float hrLength = centerX - 80;
//
//            // Only draw the second hand in interactive mode.
////            if (!isInAmbientMode()) {
////                float secX = (float) Math.sin(secRot) * secLength;
////                float secY = (float) -Math.cos(secRot) * secLength;
////                canvas.drawLine(centerX, centerY, centerX + secX, centerY +
////                        secY, mSecondPaint);
////            }
//
//            // Draw the minute and hour hands.
//            float minX = (float) Math.sin(minRot) * minLength;
//            float minY = (float) -Math.cos(minRot) * minLength;
//            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY,
//                    mMinutePaint);
//            float hrX = (float) Math.sin(hrRot) * hrLength;
//            float hrY = (float) -Math.cos(hrRot) * hrLength;
//            canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY,
//                    mHourPaint);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {

            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = SimpleFace.this.getResources();
            boolean isRound = insets.isRound();
            mXOffset = resources.getDimension(isRound
                    ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);
            float textSize = resources.getDimension(isRound
                    ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);
            float amPmSize = resources.getDimension(isRound
                    ? R.dimen.digital_am_pm_size_round : R.dimen.digital_am_pm_size);

            mDatePaint.setTextSize(resources.getDimension(R.dimen.digital_date_text_size));
            mHourPaint.setTextSize(textSize);
            mMinutePaint.setTextSize(textSize);
            mSecondPaint.setTextSize(textSize);
            mTempaint.setTextSize(amPmSize);
            mColonPaint.setTextSize(textSize);

            mColonWidth = mColonPaint.measureText(COLON_STRING);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            /* the watch face became visible or invisible */

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible and
            // whether we're in ambient mode, so we may need to start or stop the timer
            updateTimer();
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        private void registerReceiver() {

            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            SimpleFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }


        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            SimpleFace.this.unregisterReceiver(mTimeZoneReceiver);
        }




    }








}
