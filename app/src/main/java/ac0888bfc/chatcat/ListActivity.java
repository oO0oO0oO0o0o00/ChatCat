package ac0888bfc.chatcat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.logging.MemoryHandler;

public final class ListActivity extends BaseActivity {

    static public ListActivity instance;

    private ChatsFragment chatFrag;
    private FriendsFragment friFrag;

    public ChatActivity thischat;

    public void onAddFriendPressed(View view) {
        startActivity(new Intent(this, AddFriendActivity.class));
    }

    public void onAddChatPressed(View view) {
        //
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    public void updateChats() {
        chatFrag.update();
    }

    @Override
    public void onBackPressed() {
        CommonUtils.toast(this, "正在关闭");
        Client.get().goDie();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.list_activity);

        ViewPager pager = findViewById(R.id.pager);

        chatFrag = new ChatsFragment();
        friFrag = new FriendsFragment();
        pager.setAdapter(new CatAdapter(chatFrag, friFrag));

        Client.get().enterDuplexMode(new MeowHandler());
    }

    private class CatAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments;
        private String[] titles;

        public CatAdapter(ChatsFragment cf, FriendsFragment ff) {
            super(getSupportFragmentManager());
            fragments = new Fragment[]{
                    cf, ff
            };
            titles = new String[]{
                    "聊天", "好友"
            };
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private class MeowHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 80: {
                    List<Friend> list = (List<Friend>) msg.obj;
                    friFrag.updateFriends(list);
                    break;
                }
                case 82: {
                    JSONObject jso = (JSONObject) msg.obj;
                    System.out.print(jso.toString());
                    CommonUtils.toast(ListActivity.this, jso.toString());
                    try {
                        int id = jso.getInt("from");
                        Chat chat = Datas.getChat(id, 1);
                        if (chat == null) {
                            chat = new Chat();
                            chat.type = 1;
                            chat.id = id;
                            Datas.chats.add(chat);
                        }
                        chat.unread = true;
                        Chat.Message m = new Chat.Message();
                        m.sender = id;
                        m.type = jso.getInt("type");
                        m.text = jso.getString("content");
                        chat.messages.add(m);
                        ListActivity.this.updateChats();
                        if (ListActivity.this.thischat != null) {
                            ListActivity.this.thischat.onNewMessage(m);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
