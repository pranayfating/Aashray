package com.example.hp.maps;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class ViewPageAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> imageUrls;
    private ArrayList<String> usernames;
    private ActivityOptions options;
    private ViewPager mViewpager;
    private Map map;


    public ViewPageAdapter(Context context, ArrayList<String> imageUrls,ArrayList<String> usernames) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.usernames = usernames;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final ImageView imageView = new ImageView(context);


        Picasso.get()
                .load(imageUrls.get(position))
                .fit()
                .centerCrop()
                .into(imageView);
        container.addView(imageView);

        imageView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //this will log the page number that was click
                Log.i("TAG", "This page was clicked: " + position);
                Intent i = new Intent(context, Dealer_detail.class);
                i.putExtra("position",position);
                i.putStringArrayListExtra("usernames",usernames);
                options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,imageView,context.getString(R.string.transition_pageviewer));
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                context.startActivity(i,options.toBundle());
                }
        });
        return imageView;
    }



    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
