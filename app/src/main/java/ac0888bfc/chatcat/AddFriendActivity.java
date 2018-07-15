package ac0888bfc.chatcat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

public final class AddFriendActivity extends BaseActivity {

    private EditText editId;

    public void onAddPressed(View view) {
        try {
            Client.get().requestAddFriend(Integer.parseInt(editId.getText().toString()));
            CommonUtils.toast(this, "已发送");
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_activity);

        editId = findViewById(R.id.edit);
    }
}
