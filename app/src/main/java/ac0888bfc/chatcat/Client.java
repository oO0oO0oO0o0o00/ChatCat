package ac0888bfc.chatcat;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class Client {

    static private Client instance;

    static final private int IDEN_LOGIN = 0b00000000111111110000111111110000;
    static final private Charset cset = Charset.forName("UTF-8");

    @NonNull
    static public Client get() {
        if (instance == null) instance = new Client();
        return instance;
    }

    static private void sendInt(OutputStream stream, int intval) throws IOException {
        stream.write(intval >>> 24);
        stream.write(intval >>> 16);
        stream.write(intval >>> 8);
        stream.write(intval);
    }

    static private void sendBytes(OutputStream stream, byte[] bytes, int offset, int length) throws IOException {
        for (int i = offset, lim = offset + length; i < lim; i++) {
            stream.write(bytes[i]);
        }
    }

    static private void sendBytes(OutputStream stream, byte[] bytes) throws IOException {
        sendBytes(stream, bytes, 0, bytes.length);
    }

    static private int recvInt(InputStream stream) throws IOException {
        int intval = stream.read() << 24;
        intval |= stream.read() << 16;
        intval |= stream.read() << 8;
        intval |= stream.read();
        return intval;
    }

    static private int recvBytes(InputStream stream, byte[] bytes, int offset, int length) throws IOException {
        for (int i = offset, j = 0, lim = offset + length; i < lim; i++, j++) {
            int b = stream.read();
            if (b == -1) return j;
            bytes[i] = (byte) b;
        }
        return length;
    }

    static private byte[] recvBytes(InputStream stream, int length) throws IOException {
        byte[] bytes = new byte[length];
        recvBytes(stream, bytes, 0, length);
        return bytes;
    }

    static private void sendLengthAndJson(OutputStream stream, JSONObject json) throws IOException {
        byte[] payload = json2bytes(json);
        if (null == payload) throw new IOException("json input invalid");
        sendInt(stream, payload.length);
        sendBytes(stream, payload);
    }

    static private JSONObject recvLengthAndJson(InputStream stream) throws IOException {
        int len = recvInt(stream);
        return bytes2json(recvBytes(stream, len));
    }

    @Nullable
    static private JSONObject bytes2json(byte[] bytes) {
        try {
            String s = new String(bytes, cset);
            return new JSONObject(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    static final public byte[] json2bytes(JSONObject json) {
        try {
            return json.toString(2).getBytes(cset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Socket socket;
    private int state;
    private TxThread tx;
    private RxThread rx;
    private OutputStream out;

    private Handler evHandler;

    private Client() {
        state = 0;
    }

    public void reset() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        state = 0;
        socket = null;
    }

    public boolean login(String server, int port, int id, String password) throws JSONException {
        reset();
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            socket.setKeepAlive(true);
            OutputStream os = socket.getOutputStream();

            os.flush();
            os.write(1);
            JSONObject jso = new JSONObject();
            jso.put("id", id);
            jso.put("password", password);
            sendLengthAndJson(os, jso);
            os.flush();
            InputStream is = socket.getInputStream();
            if (1 != is.read()) throw new RuntimeException();
            jso = recvLengthAndJson(is);
            if (!"1".equals(jso.getString("status"))) throw new RuntimeException("www");
            state = 1;
            return true;
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e1) {
            }
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.out.print("disc");
            try {
                socket.close();
            } catch (IOException e1) {
            }
        }
        return false;
    }

    public int register(String server, int port, String name, String password) throws JSONException {
        reset();
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        try {
            socket.setKeepAlive(true);
            OutputStream os = socket.getOutputStream();
            os.write(0);
            JSONObject jso = new JSONObject();
            jso.put("name", name);
            jso.put("password", password);
            sendLengthAndJson(os, jso);
            os.flush();
            InputStream is = socket.getInputStream();
            if (0 != is.read()) throw new RuntimeException();
            jso = recvLengthAndJson(is);
            int id = jso.getInt("id");
            state = 1;
            return id;
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e1) {
            }
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.out.print("disc");
            try {
                socket.close();
            } catch (IOException e1) {
            }
        }
        return -1;
    }

    public synchronized void enterDuplexMode(Handler han) {
        if (state != 1) return;
        try {
            tx = new TxThread(socket.getOutputStream());
            rx = new RxThread(socket.getInputStream());
            out = socket.getOutputStream();
            tx.start();
            rx.start();
            this.evHandler = han;
        } catch (IOException e) {
            e.printStackTrace();
        }
        state = 2;
    }

    public void requestFriendsList() {
        tx.enqueue(() -> {
            try {
                out.write(40);
                sendLengthAndJson(out, new JSONObject());
            } catch (IOException e) {
                e.printStackTrace();
                reset();
            }
        });
    }

    public void requestAddFriend(int id) {
        tx.enqueue(() -> {
            try {
                out.write(41);
                JSONObject jso = new JSONObject();
                jso.put("to", id);
                sendLengthAndJson(out, jso);
            } catch (Exception e) {
                e.printStackTrace();
                reset();
            }
        });
    }

    public void sendMessage(Chat.Message msg, int toid, int totype) {
        tx.enqueue(() -> {
            try {
                out.write(42);
                JSONObject jso = new JSONObject();
                jso.put("to", toid);
                jso.put("type", msg.type);
                jso.put("content", msg.text);
                sendLengthAndJson(out, jso);
            } catch (Exception e) {
                e.printStackTrace();
                reset();
            }
        });
    }

    public void goDie() {
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class RxThread extends Thread {

        private InputStream stream;

        public RxThread(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public void run() {
            InputStream is;
            try {
                while (true) {
                    int iden = stream.read();
                    switch (iden) {
                        case 80: {
                            JSONObject o = recvLengthAndJson(stream);
                            Message msg = new Message();
                            try {
                                System.out.println(o.toString(2));
                                JSONArray list = o.getJSONArray("list");
                                List<Friend> friends = new ArrayList<>(list.length());
                                for (int i = 0, lim = list.length(); i < lim; i++) {
                                    Friend fri = new Friend();
                                    JSONObject fr = list.getJSONObject(i);
                                    fri.id = fr.getInt("id");
                                    fri.name = fr.getString("name");
                                    fri.online = fr.getBoolean("online");
                                    friends.add(fri);
                                }
                                msg.obj = friends;
                            } catch (JSONException ee) {
                                msg.obj = null;
                                ee.printStackTrace();
                            }
                            msg.what = 80;
                            evHandler.sendMessage(msg);
                        }
                        break;
                        case 82: {
                            JSONObject o = recvLengthAndJson(stream);
                            Message msg = new Message();
                            msg.what = 82;
                            msg.obj = o;
                            evHandler.sendMessage(msg);
                            break;
                        }
                        default:
                            Log.e("eee", "ee=");
                            throw new IOException("wwwwwwwwwwwww");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                reset();
            }
        }
    }

    private class TxThread extends Thread {

        private OutputStream stream;
        private BlockingQueue<Runnable> queue;

        public TxThread(OutputStream stream) {
            this.stream = stream;
            queue = new LinkedBlockingQueue();
        }

        public void enqueue(Runnable task) {
            queue.add(task);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Runnable task = queue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Log.v("Tx", "Interrupted, stopping...");
                    break;
                }
            }
        }
    }

    public static interface OnFriendsArrivedListener {

        abstract public void onFriendsArrived(String list);

    }
}
