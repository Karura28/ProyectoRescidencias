package com.example.vanessatrevizo.v5;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RelativeLayout rellay1, rellay2;
        Button btniniciar, btnregistr, btnpass;

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {


                //rellay1.setVisibility(View.VISIBLE);
              //  rellay2.setVisibility(View.VISIBLE);
            }
        }; setContentView(R.layout.activity_login);
        btniniciar = findViewById(R.id.btnlogin);
        btnregistr = findViewById(R.id.btnlogin);
        btnpass = findViewById(R.id.btnforgot);

        rellay1 = findViewById(R.id.rellay1);
        rellay2 =  findViewById(R.id.rellay2);

        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash
    }

    //  public void Onclick(View view){
    // Intent intent= new Intent (Activity_Login.this, UserInterfaz.class);
    //  startActivity(intent);

}




