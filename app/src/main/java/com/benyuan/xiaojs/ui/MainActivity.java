package com.benyuan.xiaojs.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.util.UIUtils;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.img);

    }

    public void btnClicked(View view) {

        Glide.with(this).load("https://github.com/bumptech/glide/raw/master/static/glide_logo.png").into(imageView);

        boolean isTablet = UIUtils.isTablet(this);

        Logger.d(isTablet);



    }
}
