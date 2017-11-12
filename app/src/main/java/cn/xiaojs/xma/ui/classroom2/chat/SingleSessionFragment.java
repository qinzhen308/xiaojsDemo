package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.personal.PersonHomeActivity;
import cn.xiaojs.xma.ui.personal.PersonalBusiness;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;

/**
 * Created by maxiaobao on 2017/10/31.
 */

public class SingleSessionFragment extends ChatSessionFragment {


    private String accountId;
    private int followType;
    private String name;


    public static void invoke(FragmentManager fragmentManager,
                              String accountId, String title, int followType) {
        SingleSessionFragment sessionFragment = new SingleSessionFragment();
        Bundle b = new Bundle();
        b.putString(CTLConstant.EXTRA_ACCOUNTID, accountId);
        b.putString(CTLConstant.EXTRA_SESSION_NAME, title);
        b.putInt(CTLConstant.EXTRA_FOLLOWTYPE, followType);
        sessionFragment.setArguments(b);
        sessionFragment.show(fragmentManager,"ssession");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountId = getArguments().getString(CTLConstant.EXTRA_ACCOUNTID);
        name = getArguments().getString(CTLConstant.EXTRA_SESSION_NAME);
        titleStr = name;
        followType = getArguments().getInt(CTLConstant.EXTRA_FOLLOWTYPE);

    }


    @Override
    public LiveCriteria createLiveCriteria() {
        LiveCriteria liveCriteria = new LiveCriteria();
        liveCriteria.to = accountId;
        liveCriteria.type = Communications.TalkType.PEER;
        return liveCriteria;
    }

    @Override
    public void showMoreMenu() {
        final ListBottomDialog dialog = new ListBottomDialog(getContext());

        String[] items = getResources().getStringArray(R.array.classroom2_chat_more_unfollow);
        if (isFollowed()) {
            items = getResources().getStringArray(R.array.classroom2_chat_more_followed);
        }
        dialog.setItems(items);
        dialog.setTitleVisibility(View.GONE);
        dialog.setRightBtnVisibility(View.GONE);
        dialog.setLeftBtnVisibility(View.GONE);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://个人主页
                        Intent intent = new Intent(getContext(), PersonHomeActivity.class);
                        intent.putExtra(PersonalBusiness.KEY_PERSONAL_ACCOUNT, accountId);
                        getContext().startActivity(intent);

                        dialog.dismiss();
                        break;
                    case 1://关注/取消关注
                        toFollowOrCancelFollow();
                        dialog.dismiss();
                        break;
                }

            }

        });
        dialog.show();
    }

    private boolean isFollowed() {

        if (followType== Social.FllowType.FOLLOW_SHIP || followType== Social.FllowType.MUTUAL) {
            return true;
        }

        return false;
    }

    private void toFollowOrCancelFollow(){

        if (isFollowed()) {
            SocialManager.unfollowContact(getContext(), accountId, new APIServiceCallback() {
                @Override
                public void onSuccess(Object object) {

                    if (followType == Social.FllowType.MUTUAL) {
                        followType = Social.FllowType.FAN_ONLY;
                    }else {
                        followType = Social.FllowType.NA;
                    }
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Contact.MetIn metIn = new Contact.MetIn();
            metIn.id = accountId;
            metIn.subtype = Collaboration.SubType.PERSON;
            SocialManager.followContact(getContext(), accountId, name,
                    Social.ContactGroup.FRIENDS, metIn, new APIServiceCallback<Relation>() {
                @Override
                public void onSuccess(Relation object) {
                    if (object !=null) {
                        followType = object.followType;
                    }
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

}
