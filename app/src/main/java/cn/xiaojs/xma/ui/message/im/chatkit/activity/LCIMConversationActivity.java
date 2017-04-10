package cn.xiaojs.xma.ui.message.im.chatkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.orhanobut.logger.Logger;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatKit;
import cn.xiaojs.xma.ui.message.im.chatkit.cache.LCIMConversationItemCache;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMConstants;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMConversationUtils;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMLogUtils;
import cn.xiaojs.xma.ui.personal.PersonHomeActivity;
import cn.xiaojs.xma.ui.personal.PersonalBusiness;
import cn.xiaojs.xma.ui.widget.SingleSelectDialog;

/**
 * Created by wli on 16/2/29.
 * 会话详情页
 * 包含会话的创建以及拉取，具体的 UI 细节在 LCIMConversationFragment 中
 */
public class LCIMConversationActivity extends FragmentActivity {

    @BindView(R.id.chat_title)
    TextView titleView;

    protected LCIMConversationFragment conversationFragment;

    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lcim_conversation_activity);
        ButterKnife.bind(this);
        conversationFragment = (LCIMConversationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
        initByIntent(getIntent());

        DataManager.setHasMessage(this,false);
    }


    @OnClick({R.id.back_btn, R.id.chat_right_btn, R.id.chat_right_btn2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.chat_right_btn:

                SingleSelectDialog dialog = new SingleSelectDialog(this);
                dialog.setTitle("消息设置");
                dialog.setItems(getResources().getStringArray(R.array.chat_setting));
                dialog.setSelectPosition(0);
                dialog.setOnOkClick(new SingleSelectDialog.OnOkClickListener() {
                    @Override
                    public void onOk(int position) {

                        if (position == 1) {
                            conversationFragment.imConversation.mute(new AVIMConversationCallback() {
                                @Override
                                public void done(AVIMException e) {
                                    if (e == null) {
                                        //设置成功
                                        if (XiaojsConfig.DEBUG) {
                                            Logger.d("set mute success");
                                        }
                                    } else {
                                        if (XiaojsConfig.DEBUG) {
                                            Logger.d("set mute failed");
                                        }
                                    }
                                }
                            });
                        } else {
                            conversationFragment.imConversation.unmute(new AVIMConversationCallback() {
                                @Override
                                public void done(AVIMException e) {
                                    if (e == null) {
                                        //设置成功
                                        if (XiaojsConfig.DEBUG) {
                                            Logger.d("set unmute success");
                                        }
                                    } else {
                                        if (XiaojsConfig.DEBUG) {
                                            Logger.d("set unmute failed");
                                        }
                                    }
                                }
                            });
                        }


                    }
                });
                dialog.show();

                break;
            case R.id.chat_right_btn2:
                if (TextUtils.isEmpty(accountId)) {
                    return;
                }

                Intent intent = new Intent(this, PersonHomeActivity.class);
                intent.putExtra(PersonalBusiness.KEY_PERSONAL_ACCOUNT, accountId);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initByIntent(intent);
    }

    private void initByIntent(Intent intent) {
        if (null == LCChatKit.getInstance().getClient()) {
            //FIXME
            showToast("please login first!");
            finish();
            return;
        }

        Bundle extras = intent.getExtras();
        if (null != extras) {
            if (extras.containsKey(LCIMConstants.PEER_ID)) {

                accountId = extras.getString(LCIMConstants.PEER_ID);
                getConversation(accountId);
            } else if (extras.containsKey(LCIMConstants.CONVERSATION_ID)) {
                String conversationId = extras.getString(LCIMConstants.CONVERSATION_ID);
                updateConversation(LCChatKit.getInstance().getClient().getConversation(conversationId));
            } else {
                showToast("memberId or conversationId is needed");
                finish();
            }
        }
    }

    /**
     * 设置 actionBar title 以及 up 按钮事件
     */
    protected void initActionBar(String title) {

        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }


//    ActionBar actionBar = getSupportActionBar();
//    if (null != actionBar) {
//      if (null != title) {
//        actionBar.setTitle(title);
//      }
//      actionBar.setDisplayUseLogoEnabled(false);
//      actionBar.setDisplayHomeAsUpEnabled(true);
//      finishActivity(RESULT_OK);
//    }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 主动刷新 UI
     */
    protected void updateConversation(AVIMConversation conversation) {
        if (null != conversation) {

            conversationFragment.setConversation(conversation);
            LCIMConversationItemCache.getInstance().clearUnread(conversation.getConversationId());

            accountId = LCIMConversationUtils.getConversationPeerId(conversation);

            LCIMConversationUtils.getConversationName(conversation, new AVCallback<String>() {
                @Override
                protected void internalDone0(String s, AVException e) {
                    if (null != e) {
                        LCIMLogUtils.logException(e);
                    } else {
                        initActionBar(s);
                    }
                }
            });

        }
    }

    /**
     * 获取 conversation
     * 为了避免重复的创建，createConversation 参数 isUnique 设为 true·
     */
    protected void getConversation(final String memberId) {
        LCChatKit.getInstance().getClient().createConversation(
                Arrays.asList(memberId), "", null, false, true, new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation avimConversation, AVIMException e) {
                        if (null != e) {
                            showToast(e.getMessage());
                        } else {
                            updateConversation(avimConversation);
                        }
                    }
                });
    }

    /**
     * 弹出 toast
     */
    private void showToast(String content) {
        Toast.makeText(LCIMConversationActivity.this, content, Toast.LENGTH_SHORT).show();
    }
}