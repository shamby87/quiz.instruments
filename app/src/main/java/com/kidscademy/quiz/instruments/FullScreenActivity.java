package com.kidscademy.quiz.instruments;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import js.log.Log;
import js.log.LogFactory;

public class FullScreenActivity extends AppCompatActivity
{
  private static final Log log = LogFactory.getLog(FullScreenActivity.class);

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    log.trace("onCreate(Bundle)");
    super.onCreate(savedInstanceState);

    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    if(Build.VERSION.SDK_INT < 19) {
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
  }

  @Override
  public void onStart()
  {
    log.trace("onStart()");
    super.onStart();

    if(Build.VERSION.SDK_INT >= 19) {
      // immersive flag was added on SDK 19
      // is possible for activity to become active without onCreate invoked, e.g. when is not recycled
      // for this reason need to set decorator flag here but not on onCreate otherwise navigation bar remain visible
      int options = View.SYSTEM_UI_FLAG_LAYOUT_STABLE //
          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION //
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
          | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
          | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

      getWindow().getDecorView().setSystemUiVisibility(options);
    }
  }
}
