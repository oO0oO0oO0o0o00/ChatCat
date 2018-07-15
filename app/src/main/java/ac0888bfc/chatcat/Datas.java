package ac0888bfc.chatcat;

import java.util.ArrayList;
import java.util.List;

public final class Datas {

    static public List<Friend> friends = new ArrayList<>(128);

    static public List<Chat> chats = new ArrayList<>(128);

    static public Friend getFriend(int id) {
        for (Friend f : friends) {
            if (id == f.id) return f;
        }
        return null;
    }

    static public Chat getChat(int id, int type) {
        for (Chat f : chats) {
            if (id == f.id && type == f.type) return f;
        }
        return null;
    }

}
