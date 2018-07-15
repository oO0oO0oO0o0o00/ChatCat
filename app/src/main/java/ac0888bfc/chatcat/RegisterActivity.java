package ac0888bfc.chatcat;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

public final class RegisterActivity extends BaseActivity {

    private EditText editServ, editPort, editName, editPass;
    private Button btnLogin, btnAlter;

    public void onLoginPressed(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void onRegisterPressed(View view) {
        flow:
        {
            Register register = new Register();
            toggleRegisterButton(true);
            try {
                register.server = editServ.getText().toString();
                register.port = Integer.parseInt(editPort.getText().toString());
                register.name = editName.getText().toString();
                register.password = editPass.getText().toString();
            } catch (Exception e) {
                break flow;
            }
            /*SharedPreferences.Editor ed = Storage.getLoginPrefs(this).edit();
            Storage.putServer(ed, register.server);
            Storage.putPort(ed, register.port);
            Storage.putId(ed, register.name);
            Storage.putPassword(ed, register.password);
            ed.apply();*/
            new RegisterTask().execute(register);
            return;
        }
        Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
        toggleRegisterButton(false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        editServ = findViewById(R.id.edit);
        editPort = findViewById(R.id.edit1);
        editName = findViewById(R.id.edit2);
        editPass = findViewById(R.id.edit3);
        btnLogin = findViewById(R.id.button);
        btnAlter = findViewById(R.id.button1);

        toggleRegisterButton(false);

        autofill:
        {
            try {
                SharedPreferences pref = Storage.getLoginPrefs(this);
                String str = Storage.getSavedServer(pref);
                if (str == null)
                    break autofill;
                editServ.setText(str);
                editPort.setText(Integer.toString(Storage.getSavedPort(pref)));
                //editName.setText(Integer.toString(Storage.getSavedId(pref)));
                //editPass.setText(Storage.getSavedPassword(pref));
            } catch (Exception e) {
            }
        }
    }

    private void toggleRegisterButton(boolean disabled) {
        if (disabled) {
            btnLogin.setEnabled(false);
            btnAlter.setEnabled(false);
            btnLogin.setText("请稍等..");
        } else {
            btnLogin.setEnabled(true);
            btnAlter.setEnabled(true);
            btnLogin.setText("注册");
        }
    }

    private class RegisterTask extends AsyncTask<Register, Void, Integer> {

        @Override
        protected Integer doInBackground(Register... registers) {
            Client client = Client.get();
            try {
                return client.register(registers[0].server, registers[0].port, registers[0].name, registers[0].password);
            } catch (JSONException e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        protected void onPostExecute(final Integer integer) {
            super.onPostExecute(integer);
            if (integer < 0) {
                Toast.makeText(RegisterActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                toggleRegisterButton(false);
            } else {
                AlertDialog dia = new AlertDialog.Builder(RegisterActivity.this)
                        .setMessage("记住您的ID：" + integer)
                        .setPositiveButton("复制并完成", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData d = ClipData.newPlainText("CarChat ID", "" + integer);
                                clip.setPrimaryClip(d);
                                startActivity(new Intent(RegisterActivity.this, ListActivity.class));
                                finish();
                            }
                        })
                        .create();
                dia.show();
                Storage.putId(Storage.getLoginPrefs(RegisterActivity.this).edit(), integer).apply();
            }
        }
    }

    private static class Register {
        public String server;
        public int port;
        public String name;
        public String password;
    }
}
