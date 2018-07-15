package ac0888bfc.chatcat;

import android.content.Intent;
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
    private CatAdapter ada;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chats_fragment, container, false);

        recyclerView = v.findViewById(R.id.list);
        emptyView = v.findViewById(R.id.empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        ada = new CatAdapter();
        recyclerView.setAdapter(ada);
        toggleEmpty(true);

        return v;
    }

    public void update() {
        toggleEmpty(Datas.chats.isEmpty());
        if (null != ListActivity.instance) {
            ChatActivity a = ListActivity.instance.thischat;
            if (a != null) {
                a.chat.unread = false;
            }
        }
        ada.notifyDataSetChanged();
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

        public CatAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout lay = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_entry, parent, false);
            return new ViewHolder(lay);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Chat chat = Datas.chats.get(position);
            holder.name.setText(Integer.toString(chat.id));
            holder.unread.setVisibility(chat.unread ? View.VISIBLE : View.INVISIBLE);
            holder.id = chat.id;
            holder.type = chat.type;
        }

        @Override
        public int getItemCount() {
            return Datas.chats.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView name;
            TextView unread;
            int id;
            int type;

            public ViewHolder(LinearLayout v) {
                super(v);
                name = v.findViewById(R.id.text);
                unread = v.findViewById(R.id.text2);
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("id", id).putExtra("type", type));
            }
        }

    }
}
