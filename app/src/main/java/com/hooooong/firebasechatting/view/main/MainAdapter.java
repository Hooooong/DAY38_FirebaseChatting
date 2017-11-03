package com.hooooong.firebasechatting.view.main;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hooooong.firebasechatting.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Hong on 2017-11-03.
 */

public class MainAdapter extends PagerAdapter {
    private static final int COUNT = 5;
    List<View> views;

    public MainAdapter(Context context) {
        views = new ArrayList<>();
        for(int i = 1; i<6 ; i++){
            View view = LayoutInflater.from(context).inflate(R.layout.activity_image, null);
            ImageView imageView = view.findViewById(R.id.imageView);
            switch (i){
                case 1:
                    imageView.setBackgroundResource(R.drawable.mainimage1);
                    break;
                case 2:
                    imageView.setBackgroundResource(R.drawable.mainimage2);
                    break;
                case 3:
                    imageView.setBackgroundResource(R.drawable.mainimage3);
                    break;
                case 4:
                    imageView.setBackgroundResource(R.drawable.mainimage4);
                    break;
                default:
                    imageView.setBackgroundResource(R.drawable.mainimage5);
                    break;
            }
            views.add(view);
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View)object);
    }
}
