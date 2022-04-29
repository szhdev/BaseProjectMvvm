package com.szhdev.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by szhdev on 2021/2/8.
 */
public class DxDialog extends Dialog {


    private TextView mTitle;
    private TextView mContent;
    private TextView mSure;
    private TextView mClose;

    View.OnClickListener mCloseListener;
    View.OnClickListener mSureListener;


    private String mTitleText;
    private String mContentText;
    private String mSureText;
    private String mCloseText;


    private int mTitleTextSpSize;
    private int mContentTextSpSize;
    private int mBtnSpTextSize;


    private DxDialog(Context context) {
        this(context, R.style.dxlamp_dialog_style);
    }

    private DxDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static DxDialog create(Context context) {
        return new DxDialog(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dxlamp_dialog);


        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        int widthPixel = outMetrics.widthPixels;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = widthPixel * 75 / 100;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
        hideBottomUIMenu(getWindow().getDecorView());
       /* Dialog dialog = this;
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                uiOptions |= 0x00001000;
                dialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        });*/

        mTitle = findViewById(R.id.title);
        mContent = findViewById(R.id.content);
        mSure = findViewById(R.id.btn_sure);
        mClose = findViewById(R.id.btn_close);

        init();


    }


    private void hideBottomUIMenu(View decorView) {

        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            decorView.setSystemUiVisibility(8);
        } else if (Build.VERSION.SDK_INT >= 19) {
            int uiOptions = 4102;
            decorView.setSystemUiVisibility(uiOptions);
        }

    }

    private void init() {
        if (mCloseListener != null) {
            mClose.setOnClickListener(mCloseListener);
        } else {
            mClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        if (mSureListener != null) {
            mSure.setOnClickListener(mSureListener);
        } else {
            mSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    private static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private void initText() {

        setText(mTitle, mTitleText);
        setText(mContent, mContentText);
        setText(mSure, mSureText);
        setText(mClose, mCloseText);


        setTextSize(mTitle, mTitleTextSpSize);
        setTextSize(mContent, mContentTextSpSize);
        setTextSize(mSure, mBtnSpTextSize);
        setTextSize(mClose, mBtnSpTextSize);
    }

    private void setTextSize(TextView tv, int size) {
        if (size == 0) return;
        tv.setTextSize(sp2px(getContext(), size));
    }

    public DxDialog setTitleTextSpSize(int titleTextSpSize) {
        mTitleTextSpSize = titleTextSpSize;
        return this;
    }

    public DxDialog setContentTextSpSize(int contentTextSpSize) {
        mContentTextSpSize = contentTextSpSize;
        return this;
    }

    public DxDialog setBtnSpTextSize(int btnSpTextSize) {
        mBtnSpTextSize = btnSpTextSize;
        return this;
    }

    public DxDialog setCloseListener(View.OnClickListener closeListener) {
        mCloseListener = closeListener;
        return this;
    }

    public DxDialog setSureListener(View.OnClickListener sureListener) {
        mSureListener = sureListener;
        return this;
    }

    private void setText(TextView tv, String v) {
        if (!TextUtils.isEmpty(v)) {
            tv.setText(v);
        }
    }

    public DxDialog setTitle(String title) {

        mTitleText = title;

        return this;
    }

    public DxDialog setContent(String t) {
        mContentText = t;
        return this;
    }

    public DxDialog setSureText(String title) {
        mSureText = title;
        return this;
    }

    public DxDialog setCloseText(String title) {
        mCloseText = title;
        return this;
    }

    @Override
    protected void onStart() {
        super.onStart();

        initText();

        if (mCloseListener != null) {
            mClose.setOnClickListener(mCloseListener);
        }
        if (mSureListener != null) {
            mSure.setOnClickListener(mSureListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void show() {

        if (isShowing()) return;

        super.show();
    }

    @Override
    public void dismiss() {
        if (!isShowing()) return;
        super.dismiss();

    }
}
