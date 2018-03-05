package com.zhjirui.okhttp.Utils;

import android.os.Handler;

import java.text.SimpleDateFormat;

/**
 * Created by 18513 on 2017/12/14.
 */

public class Tasktimer {

    private long mTime;
    private long mNextTime;
    private SimpleDateFormat mTimeFormat;
    private Handler handler;
    private OnTimeListener onTimeListener;
    private Runnable runnable;

    public Tasktimer(Handler handler, OnTimeListener onTimeListener) {
        this.handler = handler;
        this.onTimeListener = onTimeListener;
        mTimeFormat = new SimpleDateFormat("hh:mm:ss");
    }

    /**
     * 初始化时分秒
     *
     * @param _time_h
     * @param _time_m
     * @param _time_s
     */
    public void initTime(long _time_h, long _time_m, long _time_s) {
        mTime = mNextTime = _time_h * 3600 + _time_m * 60 + _time_s;
    }

    /**
     * 设置时间格式
     * @param pattern 计时格式
     */
    public void setTimeFormat(String pattern) {
        mTimeFormat = new SimpleDateFormat(pattern);
    }

    /**
     * 开启计时器
     */
    public void startTimer() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mNextTime <= 0) {
                    if (mNextTime == 0) {
                        cancel();
                    }
                    mNextTime = 0;
                    updateTimeText();
                    return;
                }
                mNextTime--;
                updateTimeText();

                handler.postDelayed(this,1000);
            }
        };
        if (handler != null) {
            handler.post(runnable);
        }
        updateTimeText();
    }

    /**
     * 取消计时器
     */
    public void cancel() {
        if (handler != null && runnable != null)
            handler.removeCallbacks(runnable);
    }

    /**
     * 更新时间
     */
    private void updateTimeText() {
        if (onTimeListener != null)
            onTimeListener.onTime(FormatMiss(mNextTime));
    }

    /**
     * 时分秒的转换
     * @param miss
     * @return
     */
    public String FormatMiss(long miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + "时" + mm + "分" + ss + "秒";
    }

    public interface OnTimeListener {
        void onTime(String time);
    }

}

