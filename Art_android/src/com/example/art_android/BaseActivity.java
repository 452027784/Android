package com.example.art_android;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import roboguice.activity.RoboActivity;

import java.lang.reflect.Method;

/**
 * @class BaseActivity
 * @brief 所有Activity的基类，完成应用程序内部Activity的管理，此类为抽象类，不能直接显示
 * @author guanghua.xiao
 * @date 2013-7-25 下午4:33:42
 */
public  class BaseActivity extends RoboActivity {


    private static final String TAG = BaseActivity.class.getSimpleName();
    protected Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.getInstance().addActivity(this);
        appContext = MyApplication.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        // TODO 应用程序被切换到前台
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO 判断应用程序是否被切换到后台
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().removeActivity(this);
        appContext = null;
        super.onDestroy();
        System.gc();
    }

    protected void hideSoftInputPanel(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    public void hideInputMethodPanel() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void showSoftInputPanel(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(!imm.isActive(view)){
            imm.showSoftInput(view,InputMethodManager.SHOW_FORCED); //强制显示键盘
        }
    }

    protected void moveTaskToBack(){
        if(!moveTaskToBack(false)){
            moveTaskToBack(true);
        }
    }

    /**
     * @brief 设置指定控件获得焦点
     * @param view
     *            指定控件对象
     */
    protected void requestFocus(View view) {
        view.requestFocus();
		/*
		 * View currentFocusView = getCurrentFocus(); if(currentFocusView !=
		 * null){ currentFocusView.setFocusable(false);
		 * currentFocusView.setFocusableInTouchMode(false); }
		 * view.setFocusable(true); view.setFocusableInTouchMode(true);
		 * view.requestFocus();
		 */
    }

    /**
     * @brief 隐藏软件盘
     */
    protected void hideSoftInputMethod(EditText ed) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod(
                        "setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
                // ed.setFocusable(true);
                // ed.setFocusableInTouchMode(true);
                // ed.requestFocus();
                // ed.requestFocusFromTouch();
            } catch(NoSuchMethodException e){
                // 有的机型是setSoftInputShowOnFocus
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

}
