package my.fyp.app.mpart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button mabutton = findViewById(R.id.manual_audio);
        mabutton.setOnClickListener(v -> {
            Intent intent = new Intent (this, manual_audio.class);
            startActivity(intent);
        });




    }
}