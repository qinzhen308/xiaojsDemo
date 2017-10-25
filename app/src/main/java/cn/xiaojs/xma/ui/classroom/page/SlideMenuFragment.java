package cn.xiaojs.xma.ui.classroom.page;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.base.CommonRVAdapter;
import cn.xiaojs.xma.ui.classroom2.base.RightSheetFragment;
import cn.xiaojs.xma.ui.widget.ClosableAdapterSlidingLayout;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/10/19.
 */

public class SlideMenuFragment extends BaseFragment {

    @BindView(R.id.root_lay)
    ClosableAdapterSlidingLayout root;
    @BindView(R.id.title_view)
    TextView titleView;
    @BindView(R.id.rv_slide_menu)
    RecyclerView rvSlideMenu;
    CommonRVAdapter mAdapter;

    public static final String EXTRA_DOC_IMGS="extra_doc_imgs";
    public static final String EXTRA_CUR_PAGE="extra_cur_page";
    public static final String EXTRA_DOC_TITLE="extra_doc_title";
    ArrayList<SlideImgModel> datas = new ArrayList<>();
    private int curPage;


    @Override
    protected View getContentView() {
        return LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_classroom2_slide_menu, null);
    }

    @Override
    protected void init() {
        titleView.setText(getArguments().getString(EXTRA_DOC_TITLE));
        rvSlideMenu.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        mAdapter = new CommonRVAdapter(rvSlideMenu);
        rvSlideMenu.setAdapter(mAdapter);
        curPage=getArguments().getInt(EXTRA_CUR_PAGE);
        createSlides(getArguments().getStringArrayList(EXTRA_DOC_IMGS));
        mAdapter.setCallback(new EventCallback() {
            @Override
            public void onEvent(int what, Object... object) {
                for(int i=0;i<datas.size();i++){
                    if(i==(int)object[0]){
                        datas.get(i).isSelected=true;
                    }else {
                        datas.get(i).isSelected=false;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter.setList(datas);
        mAdapter.notifyDataSetChanged();
    }


    public void createSlides(ArrayList<String> imgs){
        if(ArrayUtil.isEmpty(imgs)){
           return;
        }
        SlideImgModel model=null;
        for(String img:imgs){
            model=new SlideImgModel();
            model.url=img;
            datas.add(model);
        }
        datas.get(curPage).isSelected = true;
    }


    /*public static SlideMenuFragment createInstance(ArrayList<String> imgs,int curPage){
        SlideMenuFragment fragment=new SlideMenuFragment();
        if(!ArrayUtil.isEmpty(imgs)){
            Bundle data=new Bundle();
            data.putStringArrayList(EXTRA_DOC_IMGS,imgs);
            fragment.setArguments(data);
        }
        return fragment;
    }*/

    public static SlideMenuFragment createInstance(String title,ArrayList<LibDoc.ExportImg> imgs, int curPage){
        SlideMenuFragment fragment=new SlideMenuFragment();

        if(!ArrayUtil.isEmpty(imgs)){
            ArrayList<String> urls=new ArrayList<>();
            for(LibDoc.ExportImg img:imgs){
                urls.add(img.name);
            }
            Bundle data=new Bundle();
            data.putStringArrayList(EXTRA_DOC_IMGS,urls);
            data.putInt(EXTRA_CUR_PAGE,curPage);
            data.putString(EXTRA_DOC_TITLE,title);
            fragment.setArguments(data);
        }
        return fragment;
    }

}
