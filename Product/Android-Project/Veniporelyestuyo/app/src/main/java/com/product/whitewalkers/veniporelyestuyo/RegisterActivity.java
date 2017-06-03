package com.product.whitewalkers.veniporelyestuyo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText usernameTxt = (EditText)findViewById(R.id.username);
        final EditText emailTxt = (EditText)findViewById(R.id.email);
        final EditText passTxt = (EditText)findViewById(R.id.password);

        Button btnContinue = (Button)findViewById(R.id.btnContinuar);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameTxt.getText().toString();
                String email = emailTxt.getText().toString();
                String password = passTxt.getText().toString();

            }
        });
    }
}
