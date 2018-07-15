package ac0888bfc.chatcat;

import java.util.ArrayList;
import java.util.List;

public final class Datas {

    static public List<Friend> friends = new ArrayList<>(128);

    static public Friend getFriend(int id) {
        for (Friend f : friends) {
            if (id == f.id) return f;
        }
        return null;
    }

}
