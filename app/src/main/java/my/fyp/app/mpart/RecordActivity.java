package my.fyp.app.mpart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private OutputAnalyzer analyzer;

    private final int REQUEST_CODE_CAMERA = 0;
    public static final int MESSAGE_UPDATE_REALTIME = 1;
    public static final int MESSAGE_UPDATE_FINAL = 2;
    public static final int MESSAGE_CAMERA_NOT_AVAILABLE = 3;
    public static final int MESSAGE_UPDATE_TITLE = 4;

    private static final int MENU_INDEX_NEW_MEASUREMENT = 0;
    private static final int MENU_INDEX_EXPORT_RESULT = 1;
    private static final int MENU_INDEX_EXPORT_DETAILS = 2;

    private boolean justShared = false;

    MediaPlayer player;




    @SuppressLint("HandlerLeak")
    private final Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what ==  MESSAGE_UPDATE_TITLE) {
                ((TextView) findViewById(R.id.measureTitle)).setText(msg.obj.toString());
            }

            if (msg.what ==  MESSAGE_UPDATE_REALTIME) {
                ((TextView) findViewById(R.id.measureText1)).setText(msg.obj.toString());
            }

            if (msg.what == MESSAGE_UPDATE_FINAL) {
                ((TextView) findViewById(R.id.measureText2)).setText(msg.obj.toString());

                // make sure menu items are enabled when it opens.
                Menu appMenu = ((Toolbar) findViewById(R.id.toolbar)).getMenu();
                appMenu.getItem(MENU_INDEX_EXPORT_RESULT).setVisible(true);
                appMenu.getItem(MENU_INDEX_EXPORT_DETAILS).setVisible(true);
                appMenu.getItem(MENU_INDEX_NEW_MEASUREMENT).setVisible(true);

                ((Button) findViewById(R.id.menuButton)).setVisibility(View.VISIBLE);
                ((ImageView)findViewById(R.id.imageView)).setVisibility(View.INVISIBLE);


            }

            if (msg.what == MESSAGE_CAMERA_NOT_AVAILABLE) {
                Log.println(Log.WARN, "camera", msg.obj.toString());

                ((TextView) findViewById(R.id.measureText1)).setText(
                        R.string.camera_not_found
                );
                analyzer.stop();
            }
        }
    };

    private final CameraService cameraService = new CameraService(this, mainHandler);

    @Override
    protected void onResume() {
        super.onResume();

        analyzer = new OutputAnalyzer(this, findViewById(R.id.graphTextureView), mainHandler);

        TextureView cameraTextureView = findViewById(R.id.textureView2);
        SurfaceTexture previewSurfaceTexture = cameraTextureView.getSurfaceTexture();

        // justShared is set if one clicks the share button.
        if ((previewSurfaceTexture != null) && !justShared) {
            // this first appears when we close the application and switch back
            // - TextureView isn't quite ready at the first onResume.
            Surface previewSurface = new Surface(previewSurfaceTexture);

            // show warning when there is no flash
            if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                Snackbar.make(
                        findViewById(R.id.constraintLayout),
                        getString(R.string.noFlashWarning),
                        Snackbar.LENGTH_LONG
                ).show();
            }

            // hide the new measurement item while another one is in progress in order to wait
            // for the previous one to finish
            ((Toolbar) findViewById(R.id.toolbar)).getMenu().getItem(MENU_INDEX_NEW_MEASUREMENT).setVisible(false);
            cameraService.start(previewSurface);
            analyzer.measurePulse(cameraTextureView, cameraService);


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraService.stop();

        stopPlayer();

        if (analyzer != null) analyzer.stop();
        analyzer = new OutputAnalyzer(this, findViewById(R.id.graphTextureView), mainHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CODE_CAMERA);


        ((Button) findViewById(R.id.menuButton)).setVisibility(View.INVISIBLE);

        Button button = findViewById(R.id.menuButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent (this, MainMenu.class);
            startActivity(intent);
        });



    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Snackbar.make(
                        findViewById(R.id.constraintLayout),
                        getString(R.string.cameraPermissionRequired),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i("MENU", "menu is being prepared");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onPrepareOptionsMenu(menu);
    }

    public void onClickNewMeasurement(MenuItem item) {
        analyzer = new OutputAnalyzer(this, findViewById(R.id.graphTextureView), mainHandler);

        // clear prior results
        char[] empty = new char[0];
        ((TextView) findViewById(R.id.measureText2)).setText(empty, 0, 0);
        ((TextView) findViewById(R.id.measureText1)).setText(empty, 0, 0);

        // hide the new measurement item while another one is in progress in order to wait
        // for the previous one to finish
        // Exporting results cannot be done, either, as it would read from the already cleared UI.
        Menu appMenu = ((Toolbar) findViewById(R.id.toolbar)).getMenu();
        appMenu.getItem(MENU_INDEX_NEW_MEASUREMENT).setVisible(false);
        appMenu.getItem(MENU_INDEX_EXPORT_RESULT).setVisible(false);
        appMenu.getItem(MENU_INDEX_EXPORT_DETAILS).setVisible(false);

        ((Button) findViewById(R.id.menuButton)).setVisibility(View.INVISIBLE);

        TextureView cameraTextureView = findViewById(R.id.textureView2);
        SurfaceTexture previewSurfaceTexture = cameraTextureView.getSurfaceTexture();

        if (previewSurfaceTexture != null) {
            // this first appears when we close the application and switch back
            // - TextureView isn't quite ready at the first onResume.
            Surface previewSurface = new Surface(previewSurfaceTexture);
            cameraService.start(previewSurface);
            analyzer.measurePulse(cameraTextureView, cameraService);
        }
    }

    public void onClickExportResult(MenuItem item) {
        final Intent intent = getTextIntent((String) ((TextView) findViewById(R.id.measureText1)).getText());
        justShared = true;
        startActivity(Intent.createChooser(intent, getString(R.string.send_output_to)));
    }

    public void onClickExportDetails(MenuItem item) {
        final Intent intent = getTextIntent(((TextView) findViewById(R.id.measureText2)).getText().toString());
        justShared = true;
        startActivity(Intent.createChooser(intent, getString(R.string.send_output_to)));
    }

    private Intent getTextIntent(String intentText) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(
                Intent.EXTRA_SUBJECT,
                String.format(
                        getString(R.string.output_header_template),
                        new SimpleDateFormat(
                                getString(R.string.dateFormat),
                                Locale.getDefault()
                        ).format(new Date())
                ));
        intent.putExtra(Intent.EXTRA_TEXT, intentText);
        return intent;
    }

    private void stopPlayer() {
        if (player != null){
            player.release();
            player = null;
        }
    }
}

