package cn.xiaojs.xma.ui.home;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/12/13
 * Desc:动态详情界面
 *
 * ======================================================================================== */

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Doc;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.model.social.DynamicDetail;
import cn.xiaojs.xma.model.social.LikedRecord;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.view.MomentContent;
import cn.xiaojs.xma.ui.view.MomentHeader;
import cn.xiaojs.xma.ui.widget.ImageMatrixExpandableLayout;
import cn.xiaojs.xma.util.ToastUtil;

public class MomentDetailActivity extends BaseActivity {

    private Unbinder mBinder;

    PullToRefreshSwipeListView mList;
    @BindView(R.id.moment_detail_image_expand)
    ImageMatrixExpandableLayout mExpand;
    @BindView(R.id.moment_detail_header)
    MomentHeader mHeader;
    @BindView(R.id.moment_detail_content)
    MomentContent mContent;
    @BindView(R.id.moment_detail_prise_sum)
    TextView mPraiseNum;
    @BindView(R.id.moment_detail_prise)
    TextView mPraise;
//    @BindView(R.id.moment_detail_comment_summary)
//    TextView mCommentSummary;

    private String mMomentId;
    private MomentDetailAdapter mAdapter;

    private final int MAX_LIKED = 100;

    private DynamicDetail mDetail;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_moment_detail);
        setMiddleTitle(R.string.detail);
        setRightImage(R.drawable.ic_lesson_more);
        setRightImage2(R.drawable.share_selector);
        Intent intent = getIntent();
        if (intent != null){
            mMomentId = intent.getStringExtra(HomeConstant.KEY_MOMENT_ID);
        }
        initList();
        mBinder = ButterKnife.bind(this);
        request();
    }

    private void request(){
        showProgress(false);
        SocialManager.getActivityDetails(this, mMomentId, new APIServiceCallback<DynamicDetail>() {
            @Override
            public void onSuccess(DynamicDetail object) {
                cancelProgress();
                mDetail = object;
                requestLiked(object);
                initView(object);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                showFailedView(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        request();
                    }
                });
            }
        });
    }

    private void requestLiked(DynamicDetail object){
        if (object == null)
            return;
        Criteria criteria = new Criteria();
        Doc doc = new Doc();
        doc.id = mMomentId;
        doc.subtype = object.typeName;
        criteria.setDoc(doc);

        Pagination pagination = new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(MAX_LIKED);

        SocialManager.getLikedRecords(this, criteria, pagination, new APIServiceCallback<CollectionPage<LikedRecord>>() {
            @Override
            public void onSuccess(CollectionPage<LikedRecord> object) {
                mExpand.show(object.objectsOfPage);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    private void initList(){
        mList = (PullToRefreshSwipeListView) findViewById(R.id.moment_detail_list);
        View header = LayoutInflater.from(this).inflate(R.layout.layout_moment_detail_header,null);
        mList.getRefreshableView().addHeaderView(header);
    }

    private void initView(DynamicDetail detail){
        if (detail == null)
            return;
        mHeader.setData(detail);
        mContent.show(detail.body,detail.typeName);
        mAdapter = new MomentDetailAdapter(this,mList,true,mMomentId,detail.typeName);
        mList.setAdapter(mAdapter);
        mPraiseNum.setText(getString(R.string.praise_summary,detail.stats.liked));
//        mCommentSummary.setText(getString(R.string.comment_summary,detail.stats.comments));
        if (detail.liked){
            mPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_praise_on,0,0,0);
        }else {
            mPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_praise_off,0,0,0);
        }
    }

    @Override
    protected boolean delayBindView() {
        return true;
    }

    @OnClick({R.id.left_image,R.id.moment_detail_footer_click})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_image:
                finish();
                break;
            case R.id.moment_detail_footer_click:
                Intent comment = new Intent(this,MomentCommentActivity.class);
                comment.putExtra(HomeConstant.KEY_COMMENT_TYPE,HomeConstant.COMMENT_TYPE_WRITE);
                comment.putExtra(HomeConstant.KEY_MOMENT_ID,mMomentId);
                startActivityForResult(comment,HomeConstant.REQUEST_CODE_COMMENT);
                break;
            case R.id.moment_detail_prise:
                praise();
                break;
        }
    }

    private void praise(){
        if (mDetail != null){
            if (mDetail.liked){
                //取消赞
            }else {
                //赞
                SocialManager.likeActivity(this, mDetail.id, new APIServiceCallback<Dynamic.DynStatus>() {
                    @Override
                    public void onSuccess(Dynamic.DynStatus object) {
                        mPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_praise_on,0,0,0);
                        mDetail.stats.liked += 1;
                        mPraise.setText(String.valueOf(mDetail.stats.liked));
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        ToastUtil.showToast(MomentDetailActivity.this,errorMessage);
                    }
                });
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (mBinder != null){
            mBinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case HomeConstant.REQUEST_CODE_COMMENT:
                if (resultCode == Activity.RESULT_OK){
                    if (mAdapter != null){
                        mAdapter.doRefresh();
                    }
                }
                break;
        }
    }
}
