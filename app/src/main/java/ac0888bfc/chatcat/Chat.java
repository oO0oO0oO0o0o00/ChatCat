package ac0888bfc.chatcat;

import java.util.List;

public final class Chat {

    public int id;

    public String name;

    public boolean unread;

    public long timestamp;

    public List<Message> messages;

    public static class Message {

        public int sender;

        public int type;

        public String text;

    }

}
