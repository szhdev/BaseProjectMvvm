package com.szhdev.base;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * @创建者 szhdev
 * @创建时间 2020 2020/2/18 16:32
 * @描述 同一封装dialog
 */

public class DxLoadingDialog extends Dialog {
    public DxLoadingDialog(Context context) {
        super(context);
    }

    public DxLoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void show() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        fullScreenImmersive(getWindow().getDecorView());
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
/*

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        int widthPixel = outMetrics.widthPixels;
        int heightPixel = outMetrics.heightPixels;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = widthPixel;
        lp.height = heightPixel;
        getWindow().setAttributes(lp);

        */
    }

    public static class Builder {

        private Context context;
        private String message;
        private boolean isShowMessage = true;
        private boolean isCancelable = false;
        private boolean isCancelOutside = false;
        private int drawable;
        private int width;
        private int height;
        private int gravity = -1;
        private OnDismissListener mOnDismissListener;
        private int tintColor;
        private int textColor;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setBackground(int drawable) {
            this.drawable = drawable;
            return this;
        }

        /**
         * 设置提示信息
         *
         * @param message
         * @return
         */

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * 设置是否显示提示信息
         *
         * @param isShowMessage
         * @return
         */
        public Builder setShowMessage(boolean isShowMessage) {
            this.isShowMessage = isShowMessage;
            return this;
        }

        public Builder setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        /**
         * 设置是否可以按返回键取消
         *
         * @param isCancelable
         * @return
         */

        public Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        /**
         * window width
         *
         * @author Yale
         * create at 2021/4/8 11:48
         */
        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        /**
         * window height
         *
         * @author Yale
         * create at 2021/4/8 11:48
         */
        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        /**
         * window gravity
         *
         * @author Yale
         * create at 2021/4/8 11:48
         */
        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            mOnDismissListener = onDismissListener;
            return this;
        }

        /**
         * 设置是否可以取消
         *
         * @param isCancelOutside
         * @return
         */
        public Builder setCancelOutside(boolean isCancelOutside) {
            this.isCancelOutside = isCancelOutside;
            return this;
        }


        public Builder setTintColor(int tintColor) {
            this.tintColor = tintColor;
            return this;
        }

        public DxLoadingDialog create() {

            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dx_dlg_loading_layout, null);
            View id = view.findViewById(R.id.dx_dlg_loading_view);
            if (drawable != 0) {
                id.setBackgroundResource(drawable);
            }
            ProgressBar pb = view.findViewById(R.id.dx_dlg_loading_progressbar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && tintColor != 0) {
                pb.setIndeterminateTintList(ColorStateList.valueOf(tintColor));
            }
            DxLoadingDialog loadingDailog = new DxLoadingDialog(context, R.style.DxDlgLoadingStyle);
            TextView msgText = (TextView) view.findViewById(R.id.dx_dlg_loading_tip_text);
            Window window = loadingDailog.getWindow();
            loadingDailog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0f;

            DisplayMetrics outMetrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
            int widthPixel = outMetrics.widthPixels;
            int heightPixel = outMetrics.heightPixels;
            if (width == 0) {
                width = widthPixel;
            }
            if (height == 0) {
                height = heightPixel;
            }
            if (gravity == -1) {
                gravity = Gravity.CENTER;
            }
            lp.width = width;
            lp.height = height;
            lp.gravity = gravity;

            window.setAttributes(lp);
            if (isShowMessage) {
                msgText.setVisibility(View.VISIBLE);
                msgText.setText(message);
            } else {
                msgText.setVisibility(View.GONE);
            }
            if (textColor != 0) msgText.setTextColor(textColor);
            loadingDailog.setContentView(view);
            loadingDailog.setCancelable(isCancelable);
            loadingDailog.setCanceledOnTouchOutside(isCancelOutside);
            if (mOnDismissListener != null) loadingDailog.setOnDismissListener(mOnDismissListener);
            return loadingDailog;

        }


    }
}
