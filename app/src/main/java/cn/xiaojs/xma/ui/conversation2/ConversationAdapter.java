package cn.xiaojs.xma.ui.conversation2;

import android.content.Context;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.XMSManager;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.ChangeNotify;
import cn.xiaojs.xma.model.socket.room.RemoveDlg;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.SwipeLayout;
import cn.xiaojs.xma.ui.lesson.xclass.MyScheduleActivity;

/**
 * Created by maxiaobao on 2017/10/30.
 */

public class ConversationAdapter extends RecyclerView.Adapter<AbsConversationViewHolder> {

    private Context context;
    private ArrayList<Contact> contacts;
    private SwipeLayout openedSwipe;

    private ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClick(Contact contact, int position);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ConversationAdapter(Context context) {
        this.context = context;
        this.contacts = new ArrayList<>();
        contacts.add(createTimetable());
    }

    public void addContact(ArrayList<Contact> datas) {

        this.contacts = datas;
        notifyDataSetChanged();
    }

    public Contact getItem(int position) {
        return contacts == null ? null : contacts.get(position);
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    @Override
    public AbsConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ConversationType.TIME_TABLE) {

            View view = TimetableConViewHolder.createView(context, parent);
            return new TimetableConViewHolder(view);

        } else if (viewType == ConversationType.PERSON) {
            View view = PeerConViewHolder.createView(context, parent);
            return new PeerConViewHolder(view);
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

        final Contact contact = contacts.get(position);

        if (holder instanceof TimetableConViewHolder) {
            TimetableConViewHolder conViewHolder = (TimetableConViewHolder) holder;

            if (contact.unread > 0) {
                conViewHolder.descView.setText("今日" + contact.unread + "节课");
            } else {
                conViewHolder.descView.setText("今日无节课");
            }

            conViewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyScheduleActivity.invoke(context);
                }
            });

        } else if (holder instanceof PeerConViewHolder) {

            final PeerConViewHolder peerConViewHolder = (PeerConViewHolder) holder;


            if (AccountDataManager.isXiaojsAccount(contact.id)) {
                peerConViewHolder.avatorView.setImageResource(R.drawable.ic_customerservice);
            } else {
                String avatorUrl = Account.getAvatar(contact.id, peerConViewHolder.avatorView.getMeasuredWidth());
                Glide.with(context)
                        .load(avatorUrl)
                        .transform(new CircleTransform(context))
                        .placeholder(R.drawable.ic_defaultavatar)
                        .error(R.drawable.ic_defaultavatar)
                        .into(peerConViewHolder.avatorView);
            }


            peerConViewHolder.titleView.setText(contact.name);
            peerConViewHolder.descView.setText(contact.lastMessage);
            peerConViewHolder.timeView.setText(TimeUtil.getTimeShowString(contact.lastTalked, false));


            if (contact.unread > 0) {

                if (contact.silent) {
                    peerConViewHolder.flagView.setBackgroundResource(R.drawable.unread_slient_ovil);
                } else {
                    peerConViewHolder.flagView.setBackgroundResource(R.drawable.unread_read_ovil);
                }

                if (contact.unread > 99) {
                    peerConViewHolder.flagView.setText("99+");
                } else {
                    peerConViewHolder.flagView.setText(String.valueOf(contact.unread));
                }

                peerConViewHolder.flagView.setVisibility(View.VISIBLE);
                peerConViewHolder.disturbFlagView.setVisibility(View.GONE);
            } else {
                if (contact.silent) {
                    peerConViewHolder.disturbFlagView.setVisibility(View.VISIBLE);
                } else {
                    peerConViewHolder.disturbFlagView.setVisibility(View.GONE);
                }
                peerConViewHolder.flagView.setVisibility(View.GONE);
            }


            peerConViewHolder.uprootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(contact, position);
                    }
                }
            });
            peerConViewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeDlg(position);
                }
            });

//            peerConViewHolder.slientView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    changeSlient(contact);
//                    peerConViewHolder.swipeLayout.close();
//                }
//            });


            peerConViewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
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


        } else {
            final ClassConViewHolder conViewHolder = (ClassConViewHolder) holder;

            if (!TextUtils.isEmpty(contact.state)
                    && (contact.state.equals(Live.LiveSessionState.LIVE)
                    || contact.state.equals(Live.LiveSessionState.INDIVIDUAL)
                    || contact.streaming)) {
                conViewHolder.animationView2.setVisibility(View.VISIBLE);
                conViewHolder.avatorTextView.setVisibility(View.INVISIBLE);

            } else {
                String title = TextUtils.isEmpty(contact.title) ? "#" : String.valueOf(contact.title.trim().charAt(0));
                conViewHolder.avatorTextView.setText(title);
                conViewHolder.avatorTextView.setVisibility(View.VISIBLE);
                conViewHolder.animationView2.setVisibility(View.INVISIBLE);
            }


            conViewHolder.titleView.setText(contact.title);
            conViewHolder.descView.setText(contact.lastMessage);
            conViewHolder.timeView.setText(TimeUtil.getTimeShowString(contact.lastTalked, false));

            if (contact.unread > 0) {

                if (contact.silent) {
                    conViewHolder.flagView.setBackgroundResource(R.drawable.unread_slient_ovil);
                } else {
                    conViewHolder.flagView.setBackgroundResource(R.drawable.unread_read_ovil);
                }

                if (contact.unread > 99) {
                    conViewHolder.flagView.setText("99+");
                } else {
                    conViewHolder.flagView.setText(String.valueOf(contact.unread));
                }

                conViewHolder.flagView.setVisibility(View.VISIBLE);
                conViewHolder.disturbFlagView.setVisibility(View.GONE);
            } else {
                if (contact.silent) {
                    conViewHolder.disturbFlagView.setVisibility(View.VISIBLE);
                } else {
                    conViewHolder.disturbFlagView.setVisibility(View.GONE);
                }
                conViewHolder.flagView.setVisibility(View.GONE);
            }

            if (contact.silent) {
                conViewHolder.slientView.setText("取消免打扰");
            } else {
                conViewHolder.slientView.setText("设为免打扰");
            }


            conViewHolder.uprootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(contact, position);
                    }
                }
            });
            conViewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeDlg(position);
                }
            });

            conViewHolder.slientView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeSlient(contact);
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

        if (openedSwipe == null)
            return false;

        float x = ev.getX();
        float y = ev.getY();
        int[] p = new int[2];
        RectF curSwipViewRect = new RectF();
        openedSwipe.getLocationInWindow(p);

        if (XiaojsConfig.DEBUG) {
            Logger.d("----ev(%f,%f), offset(%d), swipe(%d,%d)", x, y, offset, p[0], p[1]);
        }

        p[1] = p[1] - offset;
        curSwipViewRect.set(p[0], p[1], p[0] + openedSwipe.getWidth(), p[1] + openedSwipe.getHeight());
        if (curSwipViewRect.contains(x, y)) {
            return false;
        }

        return true;
    }

    public boolean closeOpendSwap() {
        if (openedSwipe != null) {
            openedSwipe.close();
            openedSwipe = null;
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

    private void changeSlient(final Contact contact) {

        final ChangeNotify changeNotify = new ChangeNotify();
        changeNotify.to = contact.id;
        changeNotify.silent = !contact.silent;

        XMSManager.sendChangeNotify(context, changeNotify, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                DataProvider.getProvider(context).updateSilent(changeNotify.to, changeNotify.silent);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {

            }
        });
    }
}
