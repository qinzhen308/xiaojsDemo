package cn.xiaojs.xma.ui.classroom.page;

import android.graphics.Bitmap;

import java.util.ArrayList;

import cn.xiaojs.xma.model.material.LibDoc;

/**
 * Created by Paul Z on 2017/10/24.
 */

public interface IBoardManager {

    public void onPushPreview(Bitmap bitmap);

    public void addNewBoard(String title);

    public void openBoard(String boardId);

    public boolean pushPreviewEnable();

    public void openSlideMenu(LibDoc doc,ArrayList<LibDoc.ExportImg> slides, int curPage);

}
