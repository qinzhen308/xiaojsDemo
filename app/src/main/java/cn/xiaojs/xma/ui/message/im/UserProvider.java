package cn.xiaojs.xma.ui.message.im;

import java.util.List;

import cn.leancloud.chatkit.LCChatProfileProvider;
import cn.leancloud.chatkit.LCChatProfilesCallBack;

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


    }
}
