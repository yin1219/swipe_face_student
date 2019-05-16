package com.example.swipe_face_student;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class LeadingPage extends Activity {
    private ViewPager myViewPager; // 頁卡內容
    private List<View> list; // 存放頁卡
    private TextView dot1, dot2; // 這些點都是文字
    private Button startButton; // 按鈕，開始體驗

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.leading);
        initDot();
        initViewPager();
    }

    private void initDot() {
        dot1 = (TextView) this.findViewById(R.id.textView1); // 這些點都是文字
        dot2 = (TextView) this.findViewById(R.id.textView2);
    }

    private void initViewPager() {
        myViewPager = (ViewPager) this.findViewById(R.id.viewPager);
        list = new ArrayList<View>();

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.leading_page2, null); // 只是為了等下findviewbuid而獨立拿出來賦給view

        list.add(inflater.inflate(R.layout.leading_page1, null));
        list.add(view);
        try {
            myViewPager.setAdapter(new MyPagerAdapter(list));

            myViewPager.setOnPageChangeListener(new MyPagerChangeListener());
        } catch (NullPointerException e) {
        }
        startButton = (Button) view.findViewById(R.id.start); // 與上面對應，獲取這個按鈕

        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeadingPage.this, TrainAndTest.class); //按下按鈕後跳轉至MainActivity.java

                startActivity(intent);
                finish();

            }
        });
    }

    class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

    class MyPagerChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) { // 設置點的顏色
                case 0:
                    dot1.setTextColor(LeadingPage.this.getResources().getColor(R.color.blue));
                    dot2.setTextColor(LeadingPage.this.getResources().getColor(R.color.blue2));
                    break;

                case 1:
                    dot1.setTextColor(LeadingPage.this.getResources().getColor(R.color.blue2));
                    dot2.setTextColor(LeadingPage.this.getResources().getColor(R.color.blue));
                    break;

            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

    }
}