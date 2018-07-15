package ac0888bfc.chatcat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class ChatActivity extends BaseActivity implements TextWatcher {

    private int id, type;
    public Chat chat;

    private EditText edit;
    private Button button;
    private LinearLayout list;

    private void addMsgToUi(Chat.Message message) {
        LinearLayout lin = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.chat_entry, null, false);
        TextView tv = lin.findViewById(R.id.text);
        if (message.sender < 0) {
            lin.setGravity(Gravity.RIGHT);
            tv.setText("我：");
        } else {
            lin.setGravity(Gravity.LEFT);
            tv.setText(Integer.toString(message.sender));
        }
        tv = lin.findViewById(R.id.text2);
        tv.setText(message.text);
        list.addView(lin);
    }

    public void onNewMessage(Chat.Message message) {
        addMsgToUi(message);
        if (chat == null) {
            chat = new Chat();
            chat.type = type;
            Datas.chats.add(chat);
            ListActivity la = ListActivity.instance;
            if (la != null) {
                la.updateChats();
            }
        }
        if (chat.unread == true) {
            chat.unread = false;
            ListActivity la = ListActivity.instance;
            if (la != null) {
                la.updateChats();
            }
        }
        chat.messages.add(message);
        Client.get().sendMessage(message, id, type);
    }

    public void onSendPressed(View view) {
        Chat.Message msg = new Chat.Message();
        msg.sender = -1;
        msg.type = 1;
        msg.text = edit.getText().toString();
        onNewMessage(msg);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        edit = findViewById(R.id.edit);
        button = findViewById(R.id.button);
        list = findViewById(R.id.list);

        edit.addTextChangedListener(this);

        id = getIntent().getIntExtra("id", -1);
        type = getIntent().getIntExtra("type", -1);
        if (id < 0 || type < 0) finish();
        getSupportActionBar().setTitle(Integer.toString(id));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chat = Datas.getChat(id, type);
        if (chat != null) {
            for (Chat.Message m : chat.messages) {
                addMsgToUi(m);
            }
        }
        if (ListActivity.instance != null) {
            ListActivity.instance.thischat = this;
        }
    }

    @Override
    protected void onDestroy() {
        if (ListActivity.instance != null) {
            ListActivity.instance.thischat = this;
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //
    }

    @Override
    public void afterTextChanged(Editable s) {
        if ("".equals(s.toString().trim())) button.setEnabled(false);
        else button.setEnabled(true);
    }
}
