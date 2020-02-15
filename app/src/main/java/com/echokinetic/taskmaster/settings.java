package com.echokinetic.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;

public class settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();

        Button save_Button = findViewById(R.id.usernameSave);
        save_Button.setOnClickListener( (e)-> {
            EditText usernameField = findViewById(R.id.add_Title);
            String usernameContent = usernameField.getText().toString();
            editor.putString("user", usernameContent);
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } );
    }


}
