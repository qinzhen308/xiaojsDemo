package cn.xiaojs.xma.ui.message.im.chatkit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMConstants;

/**
 * Created by wli on 16/2/29.
 * 图片详情页，聊天时点击图片则会跳转到此页面
 */
public class LCIMImageActivity extends Activity {

  private ImageView imageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.lcim_chat_image_brower_layout);
    imageView = (ImageView) findViewById(R.id.imageView);
    Intent intent = getIntent();
    String path = intent.getStringExtra(LCIMConstants.IMAGE_LOCAL_PATH);
    String url = intent.getStringExtra(LCIMConstants.IMAGE_URL);
    if (TextUtils.isEmpty(path)) {
      Glide.with(this).load(url).into(imageView);
      //Picasso.with(this).load(url).into(imageView);
    } else {
      Glide.with(this).load(new File(path)).into(imageView);
      //Picasso.with(this).load(new File(path)).into(imageView);
    }

  }
}
