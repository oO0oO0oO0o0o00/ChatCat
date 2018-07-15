package ac0888bfc.chatcat;

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

public final class LoginActivity extends BaseActivity {

    private EditText editServ, editPort, editID, editPass;
    private Button btnLogin, btnAlter;

    public void onLogin(View view) {
        flow:
        {
            Login login = new Login();
            toggleLoginButton(true);
            try {
                login.server = editServ.getText().toString();
                login.port = Integer.parseInt(editPort.getText().toString());
                login.id = Integer.parseInt(editID.getText().toString());
                login.password = editPass.getText().toString();
            } catch (Exception e) {
                break flow;
            }
            SharedPreferences.Editor ed = Storage.getLoginPrefs(this).edit();
            Storage.putServer(ed, login.server);
            Storage.putPort(ed, login.port);
            Storage.putId(ed, login.id);
            Storage.putPassword(ed, login.password);
            ed.apply();
            new LoginTask().execute(login);
            return;
        }
        Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
        toggleLoginButton(false);
    }

    public void onRegisterPressed(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        editServ = findViewById(R.id.edit);
        editPort = findViewById(R.id.edit1);
        editID = findViewById(R.id.edit2);
        editPass = findViewById(R.id.edit3);
        btnLogin = findViewById(R.id.button);
        btnAlter = findViewById(R.id.button1);

        toggleLoginButton(false);

        autofill:
        {
            try {
                SharedPreferences pref = Storage.getLoginPrefs(this);
                String str = Storage.getSavedServer(pref);
                if (str == null)
                    break autofill;
                editServ.setText(str);
                editPort.setText(Integer.toString(Storage.getSavedPort(pref)));
                editID.setText(Integer.toString(Storage.getSavedId(pref)));
                editPass.setText(Storage.getSavedPassword(pref));
            } catch (Exception e) {
            }
        }
    }

    private void toggleLoginButton(boolean disabled) {
        if (disabled) {
            btnLogin.setEnabled(false);
            btnAlter.setEnabled(false);
            btnLogin.setText("登录中..");
        } else {
            btnLogin.setEnabled(true);
            btnAlter.setEnabled(true);
            btnLogin.setText("保存 & 登录");
        }
    }

    private class LoginTask extends AsyncTask<Login, Void, Boolean> {

        int id;

        @Override
        protected Boolean doInBackground(Login... logins) {
            Client client = Client.get();
            try {
                id = logins[0].id;
                return client.login(logins[0].server, logins[0].port, logins[0].id, logins[0].password);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                startActivity(new Intent(LoginActivity.this, ListActivity.class).putExtra("me", id));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                toggleLoginButton(false);
            }
        }
    }

    private static class Login {
        public String server;
        public int port;
        public int id;
        public String password;
    }
}
