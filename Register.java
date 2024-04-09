package com.example.emotiondetection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class Register extends Activity {
    EditText user,pass,place,email,contact;
    Button signup;
    DBConnect db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DBConnect(this);
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        place = (EditText) findViewById(R.id.address);
        email = (EditText) findViewById(R.id.mail);
        contact = (EditText) findViewById(R.id.contact);

        signup=(Button) findViewById(R.id.signup);
        signup.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                try{
                    register();
                }catch(Exception e){
                    Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }
    public void register(){
        //read values from fields
        String s1 = user.getText().toString();
        String s2 = pass.getText().toString();
        String s3 = place.getText().toString();
        String s4 = email.getText().toString();
        String s5 = contact.getText().toString();

        //sb.append("1of"+list.size());
        //check values should not be empty
        if(s1.trim().length() == 0 ||s1 == null){
            Toast.makeText(Register.this, "Please enter username", Toast.LENGTH_LONG).show();
            user.requestFocus();
            return;
        }
        if(s2.trim().length() == 0 ||s2 == null){
            Toast.makeText(Register.this, "Please enter password", Toast.LENGTH_LONG).show();
            pass.requestFocus();
            return;
        }
        if(s3.trim().length() == 0 ||s3 == null){
            Toast.makeText(Register.this, "Please enter address", Toast.LENGTH_LONG).show();
            place.requestFocus();
            return;
        }
        if(s4.trim().length() == 0 ||s4 == null){
            Toast.makeText(Register.this, "Please enter emailid", Toast.LENGTH_LONG).show();
            email.requestFocus();
            return;
        }
        if(!CheckMail.checkMail(s4)){
            Toast.makeText(Register.this, "Please enter valid email id", Toast.LENGTH_LONG).show();
            email.requestFocus();
            return;
        }
        if(s5.trim().length() == 0 || s5 == null){
            Toast.makeText(Register.this, "Please enter contact no", Toast.LENGTH_LONG).show();
            contact.requestFocus();
            return;
        }
        String msg = db.register(s1,s2,s3,s4,s5);
        if (msg.equals("success")) {
            Intent in1 = new Intent(Register.this, Login.class);
            startActivity(in1);
        } else {
            //show login fail
            Toast.makeText(Register.this, "failed to register", Toast.LENGTH_LONG).show();
        }

    }

}

