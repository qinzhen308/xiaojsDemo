package cn.xiaojs.xma.ui.message.im;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.ui.message.im.chatkit.LCChatKitUser;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatProfileProvider;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatProfilesCallBack;


/**
 * Created by maxiaobao on 2017/4/5.
 */

public class UserProvider implements LCChatProfileProvider {

    private static UserProvider userProvider;

    public static UserProvider getUserProvider() {

        if (userProvider == null) {
            synchronized (UserProvider.class) {
                if (userProvider == null) {
                    userProvider = new UserProvider();
                }
            }
        }

        return userProvider;
    }

    @Override
    public void fetchProfiles(List<String> userIdList, LCChatProfilesCallBack profilesCallBack) {

        List<LCChatKitUser> userList = new ArrayList<>(1);
        for (String userId : userIdList) {
            userList.add(new LCChatKitUser(userId,"Testor",""));
            break;
        }



        profilesCallBack.done(userList,null);

    }
}
