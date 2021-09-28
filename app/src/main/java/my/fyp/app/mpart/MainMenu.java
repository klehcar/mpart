package my.fyp.app.mpart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
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
            Intent intent = new Intent (this, manual_audio.class);
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode)
//        {
//            case REQUEST_CODE_CAMERA: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                {
//                    Toast.makeText(MainMenu.this, "Permission granted.", Toast.LENGTH_SHORT).show();
//                    //reload my activity with permission granted or use the features what required the permission
//                    finish();
//                    startActivity(getIntent());
//                } else
//                {
//                    Toast.makeText(MainMenu.this, "The app was not allowed to access your phone camera. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
//                }
//            }
//            case REQUEST_CODE_VIBRATE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                {
//                    Toast.makeText(MainMenu.this, "Permission granted.", Toast.LENGTH_SHORT).show();
//                    //reload my activity with permission granted or use the features what required the permission
//                    finish();
//                    startActivity(getIntent());
//                } else
//                {
//                    Toast.makeText(MainMenu.this, "The app was not allowed to get your phone vibration. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
//                }
//            }
//
//
//        }
//
//    }
}