package com.example.androidtoydemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


class DownloadTask extends AsyncTask<String, Integer, Bitmap> {
  private final WeakReference<MainActivity> activityRef;

  @SuppressWarnings("deprecation")
  DownloadTask(MainActivity activity) {
    activityRef = new WeakReference<>(activity);
  }

  public static Bitmap getBitmap() {
    String url = "https://hakula.xyz/usr/uploads/hotlink-ok/avatar.jpg";
    URL myFileUrl;
    Bitmap bitmap;

    try {
      myFileUrl = new URL(url);
    } catch (MalformedURLException e) {
      Log.e("ERROR", "Invalid URL: " + url);
      return null;
    }

    try {
      HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
      conn.setDoInput(true);
      conn.connect();
      InputStream is = conn.getInputStream();
      bitmap = BitmapFactory.decodeStream(is);
      is.close();
    } catch (IOException e) {
      Log.e("ERROR", "Image not found");
      return null;
    }
    return bitmap;
  }

  @Override
  protected Bitmap doInBackground(String... params) {
    return getBitmap();
  }

  @Override
  protected void onPostExecute(Bitmap result) {
    MainActivity activity = activityRef.get();
    if (activity != null) {
      ImageView imageView = activity.findViewById(R.id.imageView);
      if (result != null) {
        imageView.setImageBitmap(result);
      }
    }
  }
}

class DownloadButtonListener implements View.OnClickListener {
  private final MainActivity activity;

  DownloadButtonListener(MainActivity activity) {
    this.activity = activity;
  }

  protected void dialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setTitle(R.string.download_modal_title);
    builder.setMessage(R.string.download_modal_msg);
    builder.setPositiveButton(R.string.ok_text, (dialog, which) -> {
      DownloadTask task = new DownloadTask(activity);
      task.execute();
    });
    builder.setNegativeButton(R.string.cancel_text, (dialog, which) -> dialog.dismiss());
    builder
        .create()
        .show();
  }

  @Override
  public void onClick(View v) {
    dialog();
  }
}

class RegisterBroadcastButtonListener implements View.OnClickListener {
  private final MainActivity activity;
  private final Button registerBroadcastButton;
  private final TextView broadcastTextView;
  private boolean isRegistered = false;
  private BroadcastReceiver mReceiver = null;

  RegisterBroadcastButtonListener(MainActivity activity) {
    this.activity = activity;
    registerBroadcastButton = activity.findViewById(R.id.register_broadcast_button);
    broadcastTextView = activity.findViewById(R.id.broadcast_textview);
  }

  public void register() {
    mReceiver = new AirplaneModeBroadcastReceiver();
    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    activity.registerReceiver(mReceiver, intentFilter);
    registerBroadcastButton.setText(R.string.unregister_broadcast_button);
    broadcastTextView.setText(R.string.broadcast_started);
  }

  public void unregister() {
    if (mReceiver != null) {
      activity.unregisterReceiver(mReceiver);
    }
    registerBroadcastButton.setText(R.string.register_broadcast_button);
    broadcastTextView.setText(R.string.broadcast_stopped);
  }

  @Override
  public void onClick(View v) {
    isRegistered = !isRegistered;
    if (isRegistered) {
      register();
    } else {
      unregister();
    }
  }
}

class SendBroadcastButtonListener implements View.OnClickListener {
  private final MainActivity activity;

  SendBroadcastButtonListener(MainActivity activity) {
    this.activity = activity;
  }

  protected void sendBroadcast() {
    Intent intent = new Intent();
    intent.setAction("MyBroadcast");
    activity.sendBroadcast(intent);
  }

  @Override
  public void onClick(View v) {
    sendBroadcast();
  }
}

class AirplaneModeBroadcastReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    if (action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
      String msg = intent
          .getExtras()
          .get("state")
          .toString();
      Toast
          .makeText(context, intent.getAction() + ": " + msg, Toast.LENGTH_LONG)
          .show();
    }
  }
}


public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button downloadButton = findViewById(R.id.download_button);
    DownloadButtonListener downloadButtonListener =
        new DownloadButtonListener(this);
    downloadButton.setOnClickListener(downloadButtonListener);

    Button registerBroadcastButton = findViewById(R.id.register_broadcast_button);
    RegisterBroadcastButtonListener registerBroadcastButtonListener =
        new RegisterBroadcastButtonListener(this);
    registerBroadcastButton.setOnClickListener(registerBroadcastButtonListener);

    Button sendBroadcastButton = findViewById(R.id.send_broadcast_button);
    SendBroadcastButtonListener sendBroadcastButtonListener =
        new SendBroadcastButtonListener(this);
    sendBroadcastButton.setOnClickListener(sendBroadcastButtonListener);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.i("INFO", "onStart");
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.i("INFO", "onRestart");
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.i("INFO", "onResume");
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.i("INFO", "onPause");

  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.i("INFO", "onStop");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.i("INFO", "onDestroy");
  }
}
