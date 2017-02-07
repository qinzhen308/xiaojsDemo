package cn.xiaojs.xma.ui.classroom.whiteboard;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/2/6
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Board;
import cn.xiaojs.xma.model.live.BoardItem;
import cn.xiaojs.xma.model.live.SlidePage;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.classroom.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.Constants;
import cn.xiaojs.xma.ui.classroom.socketio.ProtocolConfigs;
import okhttp3.ResponseBody;

public class WhiteboardManager {
    private static WhiteboardManager mInstance;
    private ArrayList<WhiteboardCollection> mWhiteboardCollectionList;
    private String mWhiteboardCollTitleSuffix;
    private int mBoardCollIndex;

    private WhiteboardManager() {
        mWhiteboardCollectionList = new ArrayList<WhiteboardCollection>();
    }

    public static synchronized WhiteboardManager getInstance() {
        if (mInstance == null) {
            mInstance = new WhiteboardManager();
        }

        return mInstance;
    }

    /**
     * 获取所有的白板集
     */
    public ArrayList<WhiteboardCollection> getWhiteboardCollectionList() {
        return mWhiteboardCollectionList;
    }

    public void release() {
        if (mWhiteboardCollectionList != null) {
            mWhiteboardCollectionList.clear();
            mWhiteboardCollectionList = null;
        }
    }

    /**
     * 添加默认白板
     */
    public void addDefaultBoard(final Context context, Constants.User user, final WhiteboardAddListener listener) {
        if (TextUtils.isEmpty(mWhiteboardCollTitleSuffix)) {
            mWhiteboardCollTitleSuffix = context.getString(R.string.white_board);
        }

        String collTitle = mWhiteboardCollTitleSuffix + "_" + (++mBoardCollIndex);
        final WhiteboardCollection wbColl = new WhiteboardCollection();
        wbColl.setTitle(collTitle);
        WhiteboardLayer layer = new WhiteboardLayer();
        if (user == Constants.User.STUDENT) {
            layer.setCanSend(false);
            layer.setCanReceive(true);
        } else {
            layer.setCanSend(true);
            layer.setCanReceive(false);
        }
        wbColl.addWhiteboardLayer(layer);


        if (user == Constants.User.TEACHER) {
            //添加白板前，需要注册向服务器注册白板
            if (context instanceof ClassroomActivity) {
                String ticket = ((ClassroomActivity) context).getTicket();
                Board board = new Board();
                board.title = collTitle;
                board.drawing = new Dimension();
                board.drawing.width = ProtocolConfigs.VIRTUAL_WIDTH;
                board.drawing.height = ProtocolConfigs.VIRTUAL_HEIGHT;
                LiveManager.registerBoard(context, ticket, board, new APIServiceCallback<BoardItem>() {
                    @Override
                    public void onSuccess(BoardItem object) {
                        mWhiteboardCollectionList.add(wbColl);
                        if (listener != null) {
                            listener.onWhiteboardAdded(wbColl);
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        Toast.makeText(context, "白板注册失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onWhiteboardAdded(null);
                        }
                    }
                });
            }
        } else if (user == Constants.User.STUDENT) {
            //学生端，白板都是本地存放，不需要注册
            mWhiteboardCollectionList.add(wbColl);
            if (listener != null) {
                listener.onWhiteboardAdded(wbColl);
            }
        }
    }

    /**
     * 添加课件白板
     */
    public void addBoard(final Context context, Constants.User user, String collTitle, String[] whiteboardName,
                         final WhiteboardAddListener listener) {
        if (whiteboardName == null) {
            return;
        }

        final WhiteboardCollection wbColl = new WhiteboardCollection(WhiteboardCollection.COURSE_WARE);
        wbColl.setTitle(collTitle);

        for (int i = 0; i < whiteboardName.length; i++) {
            WhiteboardLayer layer = new WhiteboardLayer();
            if (user == Constants.User.STUDENT) {
                layer.setCanSend(false);
                layer.setCanReceive(true);
            } else {
                layer.setCanSend(true);
                layer.setCanReceive(false);
            }
            layer.setWhiteboardName(whiteboardName[i]);
            wbColl.addWhiteboardLayer(layer);
        }

        if (user == Constants.User.TEACHER) {
            //添加白板前，需要注册向服务器注册白板
            if (context instanceof ClassroomActivity) {
                String ticket = ((ClassroomActivity) context).getTicket();

                Board board = new Board();
                board.title = collTitle;
                board.drawing = new Dimension();
                board.drawing.width = ProtocolConfigs.VIRTUAL_WIDTH;
                board.drawing.height = ProtocolConfigs.VIRTUAL_HEIGHT;
                board.pages = new SlidePage[whiteboardName.length];
                for (int i = 0; i < board.pages.length; i++) {
                    board.pages[i].name = whiteboardName[i];
                }

                LiveManager.registerBoard(context, ticket, board, new APIServiceCallback<BoardItem>() {
                    @Override
                    public void onSuccess(BoardItem object) {
                        mWhiteboardCollectionList.add(wbColl);
                        if (listener != null) {
                            listener.onWhiteboardAdded(wbColl);
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        Toast.makeText(context, "白板注册失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } else if (user == Constants.User.STUDENT) {
            //学生端，白板都是本地存放，不需要注册
            mWhiteboardCollectionList.add(wbColl);
            if (listener != null) {
                listener.onWhiteboardAdded(wbColl);
            }
        }
    }

    public void closeBoard(Context context, Constants.User user, final WhiteboardCollection wbColl, final WhiteboardCloseListener listener) {
        if (user == Constants.User.STUDENT) {
            if (listener != null) {
                mWhiteboardCollectionList.remove(wbColl);
                listener.onWhiteboardClosed(wbColl);
            }
        } else if (context instanceof ClassroomActivity) {
            String ticket = ((ClassroomActivity) context).getTicket();
            LiveManager.closeBoard(context, ticket, wbColl.getTitle(), new APIServiceCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody object) {
                    if (listener != null) {
                        mWhiteboardCollectionList.remove(wbColl);
                        listener.onWhiteboardClosed(wbColl);
                    }
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {

                }
            });
        }
    }

    public interface WhiteboardAddListener {
        void onWhiteboardAdded(WhiteboardCollection boardCollection);
    }

    public interface WhiteboardCloseListener {
        void onWhiteboardClosed(WhiteboardCollection boardCollection);
    }
}
