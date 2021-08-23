package my.fyp.app.mpart;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class activity_completed extends AppCompatActivity {
    private Button back;
    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);
        stopPlayer();


        back = findViewById(R.id.button);
        // back to menu
        back.setOnClickListener(v -> {
            Intent intent = new Intent (this, MainMenu.class);
            startActivity(intent);
        });
    }

    private void stopPlayer() {
        if (player != null){
            player.release();
            player = null;
        }
    }
}