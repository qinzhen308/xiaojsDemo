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
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Doc;
import cn.xiaojs.xma.model.DynamicStatus;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.social.Comment;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.model.social.DynamicDetail;
import cn.xiaojs.xma.model.social.LikedRecord;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.view.MomentContent;
import cn.xiaojs.xma.ui.view.MomentHeader;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.ImageMatrixExpandableLayout;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.ToastUtil;
import cn.xiaojs.xma.util.VerifyUtils;

import static cn.xiaojs.xma.ui.home.HomeFragment.ACTION_UPDATE_FOWLLED;
import static cn.xiaojs.xma.ui.home.HomeFragment.EXTRA_FOWLLED_ID;

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
    @BindView(R.id.moment_detail_footer_image)
    ImageView mFooterImage;

    @BindView(R.id.right_image)
    ImageView rightImageView;

    @BindView(R.id.moment_footer_lay)
    LinearLayout momentFooter;


    private TextView commentCountView;

    private String mMomentId;
    private MomentDetailAdapter mAdapter;

    private final int MAX_LIKED = 100;

    private DynamicDetail mDetail;

    private int itemPosition;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_moment_detail);
        setMiddleTitle(R.string.detail);

        Intent intent = getIntent();
        if (intent != null) {
            mMomentId = intent.getStringExtra(HomeConstant.KEY_MOMENT_ID);
            itemPosition = intent.getIntExtra(HomeConstant.KEY_ITEM_POSITION,-1);
        }



        initList();
        mBinder = ButterKnife.bind(this);
        request();
        Glide.with(this)
                .load(Account.getAvatar(AccountDataManager.getAccountID(this),XiaojsConfig.PORTRAIT_SIZE))
                .bitmapTransform(new CircleTransform(this))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(mFooterImage);


        mHeader.setDetailActivity(this);

    }

    private void request() {
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

    private void requestLiked(DynamicDetail object) {
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

    private void initList() {
        mList = (PullToRefreshSwipeListView) findViewById(R.id.moment_detail_list);
        View header = LayoutInflater.from(this).inflate(R.layout.layout_moment_detail_header, null);
        commentCountView = (TextView) header.findViewById(R.id.moment_detail_comment_summary);

        mList.getRefreshableView().addHeaderView(header);

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                int index = position -1;//因为listview 添加了header，所以第一条item的position=1
                Comment comment = mAdapter.getItem(index);
                if (comment !=null && comment.createdBy!= null) {
                    String cid = comment.createdBy.getId();
                    if (!TextUtils.isEmpty(cid) && cid.equalsIgnoreCase(AccountDataManager.getAccountID(MomentDetailActivity.this))) {
                        showDelDlg(comment.id, index);
                    }
                }

                return true;
            }
        });


//        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
    }


    public void updateCommentCount(int count) {
        commentCountView.setText(count+"条评论");
    }

    private void initView(DynamicDetail detail) {
        if (detail == null)
            return;

        if (!AccountDataManager.getAccountID(this).equals(detail.owner.account)
                && (AccountDataManager.unFollowable(this,detail.owner.account) || !detail.owner.followed)) {

            setRightImage(-1);
        }else{
            setRightImage(R.drawable.ic_lesson_more);
        }


        mHeader.setData(detail);
        mContent.show(detail);
        mAdapter = new MomentDetailAdapter(this, mList, true, mMomentId, detail.typeName);
        mList.setAdapter(mAdapter);
        mPraiseNum.setText(getString(R.string.praise_summary, detail.stats.liked));
//        mCommentSummary.setText(getString(R.string.comment_summary,detail.stats.comments));
        if (detail.liked) {
            mPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_praise_on, 0, 0, 0);
        } else {
            mPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_praise_off, 0, 0, 0);
        }

        if (isFollowed(detail)) {
            mPraise.setVisibility(View.VISIBLE);
            momentFooter.setVisibility(View.VISIBLE);
        }else {
            mPraise.setVisibility(View.GONE);
            momentFooter.setVisibility(View.GONE);
        }


        //FIXME H5页面出来后，可打开注释代码
//        if (detail.scope == Social.ShareScope.PUBLIC){//公开动态可分享
//            setRightImage2(R.drawable.share_selector);
//        }
    }


    public void followSuccessed(String account) {
        request();
    }


    @Override
    protected boolean delayBindView() {
        return true;
    }

    @OnClick({R.id.left_image, R.id.moment_detail_footer_click, R.id.moment_detail_prise,
                R.id.right_image,R.id.right_image2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                close();
                break;
            case R.id.moment_detail_footer_click:
                Intent comment = new Intent(this, MomentCommentActivity.class);
                comment.putExtra(HomeConstant.KEY_COMMENT_TYPE, HomeConstant.COMMENT_TYPE_WRITE);
                comment.putExtra(HomeConstant.KEY_MOMENT_ID, mMomentId);
                startActivityForResult(comment, HomeConstant.REQUEST_CODE_COMMENT);
                break;
            case R.id.moment_detail_prise:
                praise();
                break;
            case R.id.right_image:
                more();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        close();
    }

    private void more() {
        if (mDetail == null)
            return;
        ListBottomDialog dialog = new ListBottomDialog(this);
        if (!VerifyUtils.isMyself(mDetail.owner.account)){
            String[] items = getResources().getStringArray(R.array.ugc_more);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
//                        case 0://忽略此条动态
//                            break;
//                        case 1://忽略他的动态
//                            break;
                        case 0://取消关注
                            cancelFollow();
                            break;
                        case 1://举报
                            break;
                    }
                }
            });
        }else {
            String[] items = new String[]{this.getString(R.string.delete)};
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    delete(mDetail.id);
                }
            });
        }
        dialog.show();
    }

    private void delete(String aid){

        showProgress(true);
        SocialManager.deleteActivity(this, aid, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                Intent i = new Intent(HomeConstant.ACTION_RESULT_DEL);
                i.putExtra(HomeConstant.KEY_ITEM_POSITION,itemPosition);
                setResult(HomeConstant.RESULT_MOMENT_DETAIL_OPERATED,i);
                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                ToastUtil.showToast(MomentDetailActivity.this,errorMessage);
            }
        });
    }

    //取消关注
    private void cancelFollow(){
        if (mDetail == null)
            return;
        //未关注时不能取消关注
        if (!mDetail.owner.followed)
            return;

        if (AccountDataManager.unFollowable(this,mDetail.owner.account)) {
            Toast.makeText(this, R.string.unfollow_forbidden,Toast.LENGTH_SHORT).show();
            return;
        }


        SocialManager.unfollowContact(this, mDetail.owner.account, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                ToastUtil.showToast(MomentDetailActivity.this,R.string.cancel_followed);
                setResult(HomeConstant.RESULT_MOMENT_DETAIL_OPERATED);
                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(MomentDetailActivity.this,errorMessage);
            }
        });
    }

    private void praise() {
        if (mDetail != null) {
            if (mDetail.liked) {
                //取消赞
            } else {

                //赞
                SocialManager.likeActivity(this, mDetail.id, new APIServiceCallback<Dynamic.DynStatus>() {
                    @Override
                    public void onSuccess(Dynamic.DynStatus object) {
                        mPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_praise_on, 0, 0, 0);
                        mDetail.stats.liked += 1;
                        mDetail.liked = !mDetail.liked;
                        mPraiseNum.setText(getString(R.string.praise_summary, mDetail.stats.liked));
                        LikedRecord record = new LikedRecord();

//                        if (XiaojsConfig.mLoginUser == null){
//                            XiaojsConfig.mLoginUser = AccountDataManager.getUserInfo(MomentDetailActivity.this);
//                        }

                        record.createdBy = AccountDataManager.getAccont(MomentDetailActivity.this);
                        mExpand.add(record);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        ToastUtil.showToast(MomentDetailActivity.this, errorMessage);
                    }
                });
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (mBinder != null) {
            mBinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case HomeConstant.REQUEST_CODE_COMMENT:
            case HomeConstant.REQUEST_CODE_COMMENT_REPLY:
                if (resultCode == Activity.RESULT_OK) {
                    if (mAdapter != null) {
                        mAdapter.doRefresh();
                    }
                    if (requestCode == HomeConstant.REQUEST_CODE_COMMENT){
                        mDetail.stats.comments += 1;
                    }
                }
                break;
        }
    }

    private void close() {
        if (mDetail != null) {
            DynamicStatus status = new DynamicStatus();
            status.id = mDetail.id;
            status.liked = mDetail.liked;
            status.status = mDetail.stats;
            Bundle bundle = new Bundle();
            bundle.putSerializable(HomeConstant.KEY_DATA_MOMENT_DETAIL,status);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK,intent);
        }
        finish();
    }


    private void showDelDlg(final String cid, final int pos) {
        ListBottomDialog dialog = new ListBottomDialog(MomentDetailActivity.this);
        String[] items = new String[]{ getString(R.string.delete)};
        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                delComment(cid,pos);
            }
        });
        dialog.show();
    }

    private void delComment(String cid, final int position){
        showProgress(true);
        SocialManager.deleteCommentOrReply(this, cid, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                cancelProgress();

                mAdapter.getList().remove(position);
                mAdapter.notifyDataSetChanged();

                Toast.makeText(MomentDetailActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(MomentDetailActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isFollowed(final Dynamic bean) {

        if (bean == null || bean.owner == null) {
            return false;
        }

        String id = bean.owner.account;
        if (AccountDataManager.getAccountID(this).equals(id)) {
            return true;
        }
        return bean.owner.followed;
    }

}
