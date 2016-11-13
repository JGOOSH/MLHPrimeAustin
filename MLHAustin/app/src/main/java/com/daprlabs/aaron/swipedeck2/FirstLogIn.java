package com.daprlabs.aaron.swipedeck2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by GOOSH on 2016-11-13.
 */

public class FirstLogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        final EditText username = (EditText) findViewById(R.id.username);
        final EditText name = (EditText) findViewById(R.id.name);
        final EditText age = (EditText) findViewById(R.id.age);
        final EditText location = (EditText) findViewById(R.id.location);
        final EditText phoneNum = (EditText) findViewById(R.id.phone_num);
        final EditText bio = (EditText) findViewById(R.id.bio);
        Button submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = username.getText().toString();
                String nameText = name.getText().toString();
                String ageText = age.getText().toString();
                String locationText = location.getText().toString();
                String phoneNumText = phoneNum.getText().toString();
                String bioText = bio.getText().toString();
                User user = new User(uName, nameText, ageText, locationText, phoneNumText, bioText);
            }
        });



    }
}
