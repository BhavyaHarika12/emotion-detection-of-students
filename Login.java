package com.example.emotiondetection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class Login extends Activity {
    EditText user, pass;
    Button sign;
    DBConnect db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DBConnect(this);
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        sign = (Button) findViewById(R.id.signin);
        sign.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                try {
                    login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void login() {
        //read values from fields
        String s1 = user.getText().toString();
        String s2 = pass.getText().toString();
        if (s1.trim().length() == 0 || s1 == null) {
            Toast.makeText(Login.this, "Please enter username", Toast.LENGTH_LONG).show();
            user.requestFocus();
            return;
        }
        if (s2.trim().length() == 0 || s2 == null) {
            Toast.makeText(Login.this, "Please enter password", Toast.LENGTH_LONG).show();
            user.requestFocus();
            return;
        }
        boolean flag = db.login(s1, s2);
        //if response successfull then go to LoginActivity
        if (flag) {
            Intent intent = new Intent(Login.this, Emotion.class);
            intent.putExtra("user", s1);
            startActivity(intent);
        } else {
            //show login fail
            Toast.makeText(Login.this, "login failed", Toast.LENGTH_LONG).show();
        }
    }
}
