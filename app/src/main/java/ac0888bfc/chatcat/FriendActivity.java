package ac0888bfc.chatcat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

public final class FriendActivity extends BaseActivity {

    private int id;

    public void onChatPressed(View v) {
        startActivity(new Intent(this, ChatActivity.class).putExtra("id", id).putExtra("type", 1));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_activity);
        id = getIntent().getIntExtra("id", -1);
        if (id < 0) finish();
        Friend friend = Datas.getFriend(id);
        if (null == friend) return;
        TextView tv = findViewById(R.id.text);
        tv.setText(friend.name);
        tv = findViewById(R.id.text2);
        tv.setText(Integer.toString(friend.id));
        tv = findViewById(R.id.text3);
        if (friend.online) tv.setVisibility(View.VISIBLE);
        else tv.setVisibility(View.INVISIBLE);
    }
}
