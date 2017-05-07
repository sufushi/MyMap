package com.rdc.mymap.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.rdc.mymap.R;
import com.rdc.mymap.camera.CameraManager;
import com.rdc.mymap.decoding.CaptureActivityHandler;
import com.rdc.mymap.decoding.InactivityTimer;
import com.rdc.mymap.view.ViewfinderView;

import java.io.IOException;
import java.net.URI;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Initial the camera
 *
 * @author wangjing
 */
public class CaptureActivity extends Activity implements Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private Button cancelScanButton;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        if(requireCamera() == false) finish();
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        //quit the scan view
        cancelScanButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        //http://www.ismartlearning.cn/online.html?bookid=e79f0e87dfdb4ffabf52033a1aa88765&chapterid=50c77e05a6e9439294fdf9c10453dbf2&pageid=6caee992241842289284413c3aa884d1&flag=jump
        //1.自己apk内部跳转
//        boolean isJump = handleUrl(result);
//        if (!isJump) {
//            Toast.makeText(this, "跳转失败，二维码中缺少必备数据", Toast.LENGTH_LONG).show();
//            setResult(Activity.RESULT_CANCELED);
//        } else {
//            setResult(Activity.RESULT_OK, intent);
//        }
        //2.http的直接跳转
        Intent intent = new Intent();
        intent.putExtra("dataUrl", result.getText());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private boolean handleUrl(Result result) {
        //1.获取Url数据
        String data = result.getText();
        //  up366mobile://flipbook?bookId=5b93db7f365744d881650d773e82bb25&flag=jump
        //  http://jira.up366.com:8080/secure/Dashboard.jspa
        //2.解析数据
        if ("".equals(data)) return false;
        URI uri = URI.create(data);
        String host = uri.getHost();
        if (!"flipbook".equals(host)) return false;
        // bookId=5b93db7f365744d881650d773e82bb25&flag=jump
        String query = uri.getQuery();
        Pattern pattern = Pattern.compile("bookId=([^&]+)&");
        Matcher m = pattern.matcher(query);
        if (!m.find()) return false;
        String bookId = m.group(1);
        //3.根据获取到的数据跳转到指定的Activity
//        WeakRefUtil.put("jumpDataBookId", bookId);
//        Intent mainIntent = new Intent(this, MainVKActivity.class);
//        BookInfo flipbook = new BookInfo();
//        flipbook.setBookId(bookId);
//        Intent chapterIntent;
//        if (FileUtils.isFileExists(FlipbookFileHelper.getCatalogHtmlPath(flipbook))) {
//            chapterIntent = new Intent(this, HtmlBookActivityV1.class);
//        }else {
//            chapterIntent = new Intent(this, GjsChapterActivity.class);
//        }
//        BookData bookData = new BookData();
//        bookData.bookId = bookId;
//        bookData.courseId = BookInfo.COURSE_ID_MARKET;
//        bookData.castFrom("market");
//        chapterIntent.putExtra("bookData", bookData);
//        startActivities(new Intent[]{mainIntent, chapterIntent});
//        finish();
        return true;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    private boolean requireCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setMessage("该相册需要赋予访问相机的权限，不开启将无法正常工作！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create().show();
                return false;

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                return false;

            }
        } else {
            return true;
        }
    }
}