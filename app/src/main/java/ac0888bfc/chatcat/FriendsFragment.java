package ac0888bfc.chatcat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public final class FriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeEmpty, swipNormal;
    private RecyclerView list;
    private CatAdapter ada;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.friends_fragment, container, false);

        swipeEmpty = v.findViewById(R.id.empty);
        swipNormal = v.findViewById(R.id.normal);
        list = v.findViewById(R.id.list);

        swipNormal.setOnRefreshListener(this);
        swipeEmpty.setOnRefreshListener(this);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        ada = new CatAdapter();
        list.setAdapter(ada);
        toggleEmpty(true);

        swipeEmpty.setRefreshing(true);
        onRefresh();
        return v;
    }

    private void toggleEmpty(boolean empty) {
        if (empty) {
            swipeEmpty.setVisibility(View.VISIBLE);
            swipNormal.setVisibility(View.GONE);
        } else {
            swipeEmpty.setVisibility(View.GONE);
            swipNormal.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        Client.get().requestFriendsList();
    }

    public void updateFriends(List<Friend> list) {
        if (list != null) {
            Datas.friends.clear();
            Datas.friends.addAll(list);
            ada.notifyDataSetChanged();
            CommonUtils.toast(getActivity(), "已刷新");
        } else {
        }
        swipeEmpty.setRefreshing(false);
        swipNormal.setRefreshing(false);
        toggleEmpty(Datas.friends.size() <= 0);
    }

    private class CatAdapter extends RecyclerView.Adapter<CatAdapter.ViewHolder> {

        public CatAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout lin = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_entry, parent, false);
            return new ViewHolder(lin);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Friend fri = Datas.friends.get(position);
            holder.name.setText(fri.name);
            holder.online.setVisibility(fri.online ? View.VISIBLE : View.INVISIBLE);
            holder.id = fri.id;
        }

        @Override
        public int getItemCount() {
            return Datas.friends.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            int id;
            TextView name;
            TextView online;

            public ViewHolder(LinearLayout itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.text);
                online = itemView.findViewById(R.id.text2);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FriendActivity.class).putExtra("id", id));
            }
        }

    }
}
