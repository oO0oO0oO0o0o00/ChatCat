package ac0888bfc.chatcat;

import android.content.Context;
import android.widget.Toast;

public final class CommonUtils {

    static public void toast(Context ctx, String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

}
