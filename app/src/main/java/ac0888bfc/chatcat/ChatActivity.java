package ac0888bfc.chatcat;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class ChatActivity extends BaseActivity {

    int id, type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        id = getIntent().getIntExtra("id", -1);
        type = getIntent().getIntExtra("type", -1);
        if (id < 0 || type < 0) finish();
        getSupportActionBar().setTitle(Integer.toString(id));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
