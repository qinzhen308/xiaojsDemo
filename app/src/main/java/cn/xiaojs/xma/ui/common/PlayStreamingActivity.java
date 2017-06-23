package cn.xiaojs.xma.ui.common;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.main.PlayFragment;
import cn.xiaojs.xma.ui.classroom.page.VideoPlayFragment;

/**
 * Created by maxiaobao on 2017/5/24.
 */

public class PlayStreamingActivity extends FragmentActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_streaming);

        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            VideoPlayFragment playFragment = new VideoPlayFragment();

            LibDoc doc = (LibDoc) getIntent().getSerializableExtra(Constants.KEY_LIB_DOC);

            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.KEY_LIB_DOC, doc);
            bundle.putBoolean(Constants.KEY_LAUNCH_ACTIVITY, true);

            playFragment.setArguments(bundle);

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, playFragment).commit();
        }
    }
}
