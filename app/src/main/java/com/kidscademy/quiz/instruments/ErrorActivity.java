package com.kidscademy.quiz.instruments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import js.log.Log;
import js.log.LogFactory;

/**
 * Error activity displays the error message or exception content.
 * <p>
 * In order to use error activity from uncaught exception handler need to open it in separated process. For this add
 * process and task affinity attributes to activity element from manifest.
 * 
 * <pre>
 *  &lt;activity android:name="com.kids_cademy.app.ErrorActivity"
 *      . . .
 *      android:process=":exception_process"
 *      android:taskAffinity="com.kids_cademy.app.ErrorActivity"
 * </pre>
 * 
 * @author Iulian Rotaru
 */
public class ErrorActivity extends AppCompatActivity implements OnClickListener
{
  private static final Log log = LogFactory.getLog(ErrorActivity.class);

  public static final String EXTRA_EXEPTION = ErrorActivity.class.getName() + ".exception";

  /**
   * Start error activity with message and optional throwable instance.
   * 
   * @param context execution context,
   * @param messageRef message references,
   * @param throwable optional throwable instance.
   */
  public static void start(Context context, int messageRef, Throwable... throwable)
  {
    log.trace("start(Context, int, Throwable...)");
    Intent intent = new Intent(context, ErrorActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(Intent.EXTRA_TEXT, context.getString(messageRef));
    if(throwable.length > 0) {
      intent.putExtra(ErrorActivity.EXTRA_EXEPTION, throwable[0]);
    }
    context.startActivity(intent);
  }

  public ErrorActivity()
  {
    log.trace("ErrorActivity()");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    log.trace("onCreate(Bundle)");
    super.onCreate(savedInstanceState);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    setContentView(R.layout.activity_error);
    TextView messageText = findViewById(R.id.activity_error_message);
    TextView exceptionText = findViewById(R.id.activity_error_exception);
    findViewById(R.id.activity_error_exit_button).setOnClickListener(this);

    Intent intent = getIntent();
    if(intent.hasExtra(Intent.EXTRA_TEXT)) {
      messageText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
    }

    if(intent.hasExtra(EXTRA_EXEPTION)) {
      Exception exception = (Exception)getIntent().getSerializableExtra(EXTRA_EXEPTION);
      StringBuilder message = new StringBuilder();
      message.append(exception.getClass().getSimpleName());
      if(exception.getMessage() != null) {
        message.append(":\r\n\r\n");
        message.append(exception.getMessage());
      }
      exceptionText.setVisibility(View.GONE);
      exceptionText.setText(message.toString());
    }
  }

  @Override
  public void onClick(View view)
  {
    if(view.getId() == R.id.activity_error_exit_button) {
      finish();
    }
  }
}
