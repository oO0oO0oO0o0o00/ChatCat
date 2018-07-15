package ac0888bfc.chatcat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public final class ChatsFragment extends Fragment {

    private View emptyView;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chats_fragment, container, false);

        recyclerView = v.findViewById(R.id.list);
        emptyView = v.findViewById(R.id.empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new CatAdapter());
        toggleEmpty(true);

        return v;
    }

    private void toggleEmpty(boolean empty) {
        if (empty) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private class CatAdapter extends RecyclerView.Adapter<CatAdapter.ViewHolder> {

        private List<Chat> chats;

        public CatAdapter() {
            chats = new ArrayList<>(128);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout lay = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_entry, parent, false);
            return new ViewHolder(lay);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Chat chat = chats.get(position);
            holder.name.setText(chat.name);
            holder.unread.setVisibility(chat.unread ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public int getItemCount() {
            return chats.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView name;
            TextView unread;

            public ViewHolder(LinearLayout v) {
                super(v);
                name = v.findViewById(R.id.text);
                unread = v.findViewById(R.id.text2);
            }

        }

    }
}
