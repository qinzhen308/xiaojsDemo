package cn.xiaojs.xma.ui.message.im;

import android.content.Context;
import android.text.TextUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.model.account.User;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatKitUser;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatProfileProvider;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatProfilesCallBack;


/**
 * Created by maxiaobao on 2017/4/5.
 */

public class UserProvider implements LCChatProfileProvider {

    private static UserProvider userProvider;

    private Context context;

    public static UserProvider getUserProvider(Context context) {

        if (userProvider == null) {
            synchronized (UserProvider.class) {
                if (userProvider == null) {
                    userProvider = new UserProvider(context);
                }
            }
        }

        return userProvider;
    }

    private UserProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void fetchProfiles(List<String> userIdList, LCChatProfilesCallBack profilesCallBack) {

        Map<String, Contact> contactsMap = DataManager.getContacts(context);

        List<LCChatKitUser> userList = new ArrayList<>(1);
        for (String userId : userIdList) {

            Contact contact;
            if (!TextUtils.isEmpty(userId) && userId.equals(AccountDataManager.getAccountID(context))) {
                String name = AccountDataManager.getAccont(context).getBasic().getName();

                String url = Account.getAvatar(userId, XiaojsConfig.PORTRAIT_SIZE);
                userList.add(new LCChatKitUser(userId,name,url));
                break;
            }

            if (contactsMap != null && (contact = contactsMap.get(userId)) != null) {
                String url = Account.getAvatar(userId, XiaojsConfig.PORTRAIT_SIZE);
                userList.add(new LCChatKitUser(userId,contact.name,url));
                break;
            }
        }
        
        profilesCallBack.done(userList,null);

    }
}
