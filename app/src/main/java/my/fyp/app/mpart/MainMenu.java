package my.fyp.app.mpart;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainMenu extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private static final int REQUEST_CODE_CAMERA = 110 , REQUEST_CODE_VIBRATE = 220;
    private static final int REQUEST_CODE = 121;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if (Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            String[] perms = {
                    Manifest.permission.VIBRATE
            };
            if (!EasyPermissions.hasPermissions(this, perms)) {
                EasyPermissions.requestPermissions(this, "All permissions are required in oder to run this application", REQUEST_CODE, perms);
            }
        }


        Button recordButton = findViewById(R.id.record);
        recordButton.setOnClickListener(v -> {
            Intent intent = new Intent (this, MainActivity.class);
            startActivity(intent);
        });

        Button mabutton = findViewById(R.id.manual_audio);
        mabutton.setOnClickListener(v -> {
            Intent intent = new Intent (this, Audiovisual.class);
            startActivity(intent);
        });

        Button mahbutton = findViewById(R.id.manual_audio_haptic);
        mahbutton.setOnClickListener(v -> {
            Intent intent = new Intent (this, activity_manual_audiovibrate.class);
            startActivity(intent);
        });

        Button biofeedBtn = findViewById(R.id.biofeedBtn);
        biofeedBtn.setOnClickListener(v -> {
            Intent intent = new Intent (this, BiofeedbackActivity.class);
            startActivity(intent);
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.requestPermissions(this, "All permissions are required to run this application", requestCode, permissions);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }


}