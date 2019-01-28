package com.example.pvwav.myapplication;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<View> views;
    private int[] wallpapers = {
            R.drawable.wallpaper1,
            R.drawable.wallpaper3,
            R.drawable.wallpaper4,
            R.drawable.wallpaper5
    };

    Button select;
    Button set;
    ViewPager viewPager;
    int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        select = findViewById(R.id.select);
        set = findViewById(R.id.set);
        viewPager = findViewById(R.id.wallpaper_viewpager);

        views = new ArrayList();

        for (int wallpaper : wallpapers) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundColor(getResources().getColor(R.color.white));

            Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), wallpaper);

            int radio = 8;
            Bitmap result = Bitmap.createBitmap(originBitmap.getWidth() / radio, originBitmap.getHeight() / radio, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(result);
            RectF rectF = new RectF(0, 0, originBitmap.getWidth() / radio, originBitmap.getHeight() / radio);
            canvas.drawBitmap(originBitmap, null, rectF, null);

            imageView.setBackgroundColor(getResources().getColor(R.color.black));
            imageView.setImageBitmap(result);
            views.add(imageView);
        }

        WallpaperViewPagerAdapter adapter = new WallpaperViewPagerAdapter(views);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setPageTransformer(true, new ScalePageTransformer());

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWallpaper();
            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaper();
            }
        });
    }

    public void selectWallpaper() {
        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
        Intent chooser = Intent.createChooser(pickWallpaper, "chooser_wallpaper");
        startActivity(chooser);
    }

    @SuppressLint("ResourceType")
    public void setWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
        try {
            wallpaperManager.setResource(wallpapers[currentPage]);
            Toast.makeText(this, "设置壁纸成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "设置壁纸失败", Toast.LENGTH_SHORT).show();
        }
    }

    public class WallpaperViewPagerAdapter extends PagerAdapter {

        private List<View> views;

        public WallpaperViewPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = views.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(views.get(position));
        }
    }

    public class ScalePageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE=0.75f;

        @Override
        public void transformPage(View page, float position) {
            if(position<-1.0f) {
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
            else if(position<=0.0f) {
                page.setAlpha(1.0f);
                page.setTranslationX(0.0f);
                page.setScaleX(1.0f);
                page.setScaleY(1.0f);
            }
            else if(position<=1.0f) {
                page.setAlpha(1.0f-position);
                page.setTranslationX(-page.getWidth()*position);
                float scale=MIN_SCALE+(1.0f-MIN_SCALE)*(1.0f-position);
                page.setScaleX(scale);
                page.setScaleY(scale);
            }
            else {
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
        }
    }
}
