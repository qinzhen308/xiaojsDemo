package cn.xiaojs.xma.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.Conversation;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.orhanobut.logger.Logger;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.account.User;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatKit;
import cn.xiaojs.xma.ui.message.im.chatkit.activity.LCIMConversationActivity;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMConstants;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMConversationUtils;

/**
 * Created by maxiaobao on 2017/4/1.
 */

public class LeanCloudUtil {

    public static final String ATTR_LEAGUERS = "leaguers";
    public static final String ATTR_CHATNAME = "chatName";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_COVER = "cover";

    public static void lanchChatPage(Context context, String accountId, String name) {
        Intent intent = new Intent(context, LCIMConversationActivity.class);
        intent.putExtra(LCIMConstants.PEER_ID, accountId);
        intent.putExtra(LCIMConstants.PEER_NAME, name);
        context.startActivity(intent);
    }

    public static void lanchGroupChat(final Context context,
                                      String title,
                                      String conversationId) {

        Intent intent = new Intent(context, LCIMConversationActivity.class);
        intent.putExtra(LCIMConstants.CONVERSATION_ID, conversationId);
        intent.putExtra(LCIMConstants.CONVERSATION_NAME, title);
        context.startActivity(intent);
    }

    public static void open(final String accountId) {

        LCChatKit.getInstance().open(accountId, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("leancloud open success: " + accountId);
                    }
                } else {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("leancloud open failed: " + accountId);
                    }
                }
            }
        });

    }


    public static void close() {

        try {
            LCChatKit.getInstance().close(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (e == null) {
                        if (XiaojsConfig.DEBUG) {
                            Logger.d("close success");
                        }
                    } else {
                        if (XiaojsConfig.DEBUG) {
                            Logger.d("close failed");
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Map<String, Object> getAttrMap(Context context, String memberId, String name) {
        User user = AccountDataManager.getUser(context);

        HashMap<String, String> leaguers = new HashMap<>();
        leaguers.put(user.getId(), user.getName());
        leaguers.put(memberId, name);

        Object o = (Object) leaguers;

        Map<String, Object> attrs = new HashMap<>();
        attrs.put(ATTR_LEAGUERS, o);
        attrs.put(ATTR_TYPE,0);
        return attrs;
    }

    public static String getNameByAttrs(AVIMConversation conversation) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("getNameByAttrs--------------");
        }


        if (conversation == null) return "";


        String targetId = LCIMConversationUtils.getConversationPeerId(conversation);
        if (TextUtils.isEmpty(targetId)) {
            return "";
        }


        Object object = conversation.getAttribute(ATTR_LEAGUERS);
        if (object != null && object instanceof Map) {

            Map<String, String> attr = (Map<String, String>) object;

            if (attr != null) {
                return attr.get(targetId);
            }

        }

        return "";

    }

    public static String getNameByAttrs(AVIMConversation conversation, String targetId) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("getNameByAttrs--------------");
        }

        if (TextUtils.isEmpty(targetId)) return "";

        Object object = conversation.getAttribute(ATTR_LEAGUERS);
        if (object != null && object instanceof Map) {

            Map<String, String> attr = (Map<String, String>) object;

            if (attr != null) {
                return attr.get(targetId);
            }

        }

        return "";

    }


    public static String getGroupChatTitle(AVIMConversation conversation) {
        if (XiaojsConfig.DEBUG) {
            Logger.d("getGroupChatTitle--------------");
        }


        if (conversation == null) return "";

        return (String) conversation.getAttribute(ATTR_CHATNAME);

    }

    public static String getGroupChatCover(AVIMConversation conversation) {
        if (XiaojsConfig.DEBUG) {
            Logger.d("getGroupChatCover--------------");
        }


        if (conversation == null) return "";

        return (String) conversation.getAttribute(ATTR_COVER);

    }


    public static boolean isGroupChat(AVIMConversation conversation) {

        if (conversation.isTransient() || conversation.getMembers().size() > 2) {
            return true;
        }

        return false;
    }
}
