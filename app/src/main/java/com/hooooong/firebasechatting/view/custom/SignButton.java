package com.hooooong.firebasechatting.view.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.hooooong.firebasechatting.R;

/**
 * Created by Android Hong on 2017-11-03.
 */

public class SignButton  extends android.support.v7.widget.AppCompatButton{

    public SignButton(Context context) {
        super(context);
        setBackgroundColor(getResources().getColor(R.color.colorGray));
        setTextColor(getResources().getColor(R.color.colorWhite));
        setEnabled(false);
    }

    public SignButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(getResources().getColor(R.color.colorGray));
        setTextColor(getResources().getColor(R.color.colorWhite));
        setEnabled(false);
    }

    public void checkUseable(boolean check){
        if(check){
            setBackgroundColor(getResources().getColor(R.color.colorGreen));
            setTextColor(getResources().getColor(R.color.colorWhite));
            setEnabled(true);
        }else{
            setBackgroundColor(getResources().getColor(R.color.colorGray));
            setTextColor(getResources().getColor(R.color.colorWhite));
            setEnabled(false);
        }
    }
}
