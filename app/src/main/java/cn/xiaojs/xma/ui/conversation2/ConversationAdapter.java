package cn.xiaojs.xma.ui.conversation2;

import android.content.Context;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.xiaojs.xma.data.XMSManager;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.socket.room.RemoveDlg;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;
import cn.xiaojs.xma.ui.widget.SwipeLayout;
import cn.xiaojs.xma.ui.lesson.xclass.MyScheduleActivity;

/**
 * Created by maxiaobao on 2017/10/30.
 */

public class ConversationAdapter extends RecyclerView.Adapter<AbsConversationViewHolder> {

    private Context context;
    private ArrayList<Contact> contacts;
    private SwipeLayout openedSwipe;

    public ConversationAdapter(Context context) {
        this.context = context;
        //this.contacts = new ArrayList<>();
        //contacts.add(createTimetable());
    }

    public void addContact(ArrayList<Contact> datas) {
        if (contacts != null) {
            contacts.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void setContacts(ArrayList<Contact> datas) {
        contacts = datas;
        notifyDataSetChanged();
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    @Override
    public AbsConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ConversationType.TIME_TABLE) {

            View view = TimetableConViewHolder.createView(context, parent);
            return new TimetableConViewHolder(view);

        } else {
            View view = ClassConViewHolder.createView(context, parent);
            return new ClassConViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String subType = contacts.get(position).subtype;
        return ConversationType.getConversationType(subType);
    }

    @Override
    public void onBindViewHolder(AbsConversationViewHolder holder, final int position) {

        Contact contact = contacts.get(position);

        if (holder instanceof TimetableConViewHolder) {
            TimetableConViewHolder conViewHolder = (TimetableConViewHolder) holder;
            conViewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyScheduleActivity.invoke(context);
                }
            });

        } else {
            final ClassConViewHolder conViewHolder = (ClassConViewHolder) holder;

            conViewHolder.avatorTextView.setText(String.valueOf(contact.title.trim().charAt(0)));
            conViewHolder.titleView.setText(contact.title);
            conViewHolder.descView.setText(contact.lastMessage);
            conViewHolder.timeView.setText(TimeUtil.getTimeShowString(contact.lastTalked, false));

            if (contact.unread > 0) {
                conViewHolder.flagView.setText(String.valueOf(contact.unread));
                conViewHolder.flagView.setVisibility(View.VISIBLE);
            }else {
                conViewHolder.flagView.setVisibility(View.GONE);
            }


            conViewHolder.uprootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO 进入
                }
            });
            conViewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeDlg(position);
                }
            });

            conViewHolder.topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    conViewHolder.swipeLayout.close();
                }
            });


            conViewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    openedSwipe = layout;
                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onClose(SwipeLayout layout) {
                    openedSwipe = null;
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    public boolean isSwapByEv(MotionEvent ev, int offset) {

        if (openedSwipe ==null)
            return false;

        float x=ev.getX();
        float y=ev.getY() + offset;
        int[] p=new int[2];
        RectF curSwipViewRect =new RectF();
        openedSwipe.getLocationInWindow(p);
        curSwipViewRect.set(p[0],p[1],p[0]+openedSwipe.getWidth(),p[1]+openedSwipe.getHeight());
        if(curSwipViewRect.contains(x,y)){
            return false;
        }

        return true;
    }

    public boolean closeOpendSwap() {
        if (openedSwipe != null) {
            openedSwipe.close();
            return true;
        }

        return false;
    }


    private Contact createTimetable() {
        Contact timeTable = new Contact();
        timeTable.subtype = ConversationType.TypeName.TIME_TABLE;
        return timeTable;
    }

    private void removeDlg(int position) {
        Contact contact = contacts.get(position);
        contacts.remove(position);

        RemoveDlg removeDlg = new RemoveDlg();
        removeDlg.type = ConversationType.getTalkType(contact.subtype);
        removeDlg.to = contact.id;
        XMSManager.sendRemoveDialog(context, removeDlg, null);

        notifyItemRemoved(position);
    }
}
