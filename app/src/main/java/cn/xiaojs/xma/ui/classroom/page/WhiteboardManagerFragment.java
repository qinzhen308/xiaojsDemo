package cn.xiaojs.xma.ui.classroom.page;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.base.CommonRVAdapter;

/**
 * Created by Paul Z on 2017/10/18.
 */

public class WhiteboardManagerFragment extends BaseFragment {
    @BindView(R.id.rv_whiteboard)
    RecyclerView rvWhiteboard;
    @BindView(R.id.middle_view)
    TextView middleView;

    CommonRVAdapter mAdapter;

    public static final String EXTRA_SELECTED_BOARD_ID="extra_selected_board_id";

    @Override
    protected View getContentView() {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_classroom_board_manager, null);
    }

    @Override
    protected void init() {
        middleView.setText("白板");
        rvWhiteboard.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
        mAdapter=new CommonRVAdapter(rvWhiteboard);
        rvWhiteboard.setAdapter(mAdapter);
        //test
        ArrayList<WhiteboardModel> datas=new ArrayList<>();
        datas.add(new WhiteboardModel());
        datas.add(new WhiteboardModel());
        datas.add(new WhiteboardModel());
        datas.add(new WhiteboardModel());
        mAdapter.setList(datas);
        mAdapter.notifyDataSetChanged();
    }




    @OnClick({R.id.left_image, R.id.right_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                getTargetFragment().getChildFragmentManager().popBackStack();
                break;
            case R.id.right_view:
                break;
        }
    }

    public static WhiteboardManagerFragment createInstance(String selectedBoardId){
        WhiteboardManagerFragment fragment=new WhiteboardManagerFragment();
        if(!TextUtils.isEmpty(selectedBoardId)){
            Bundle data=new Bundle();
            data.putString(EXTRA_SELECTED_BOARD_ID,selectedBoardId);
            fragment.setArguments(data);
        }
        return fragment;
    }
}
