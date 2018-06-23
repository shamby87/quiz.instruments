package com.kidscademy.quiz.instruments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kidscademy.app.AppBase;
import com.kidscademy.app.FullScreenActivity;
import com.kidscademy.quiz.instruments.model.SharingAdapter;
import com.kidscademy.model.SharingApp;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import js.log.Log;
import js.log.LogFactory;
import js.util.BluetoothShare;
import js.util.FacebookShare;
import js.util.TwitterShare;

/**
 * Share application using device installed senders.
 *
 * @author Iulian Rotaru
 */
public class ShareActivity extends FullScreenActivity implements SharingAdapter.Listener, View.OnClickListener {
    private static final Log log = LogFactory.getLog(ShareActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, ShareActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_up_from_top);
    }

    private LinearLayoutManager layoutManager;
    private RecyclerView listView;
    private SharingAdapter sharingAdapter;

    private FloatingActionButton backFAB;

    private FacebookShare facebookShare;
    private TwitterShare twitterShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        AppBase.audit().openShare();
        setContentView(R.layout.activity_share);

        listView = findViewById(R.id.share_list);
        listView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(listView.getContext(), layoutManager.getOrientation());
        listView.addItemDecoration(divider);

        // discover device application able to send, aka share
        SortedSet<SharingApp> apps = new TreeSet<SharingApp>();

        // uses SENDTO action and email message mime type to discover email clients installed on device
        // also target activity should be able to handle 'mailto' schema in order to qualify as email client
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("message/rfc822");
        intent.setData(Uri.fromParts("mailto", "", null));
        for (ResolveInfo resolveInfo : getPackageManager().queryIntentActivities(intent, 0)) {
            apps.add(new SharingApp(this, resolveInfo, SharingApp.Type.EMAIL));
        }

        // discover installed applications able to SEND plain text
        // usually send means share but there are exceptions; for example Wikipedia uses text to conduct an internal search
        // also bluetooth system application has filter for SEND plain text and is listed here

        intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        for (ResolveInfo resolveInfo : getPackageManager().queryIntentActivities(intent, 0)) {
            assert resolveInfo.activityInfo.packageName != null;
            final String packageName = resolveInfo.activityInfo.packageName;
            SharingApp.Type sharingType = SharingApp.Type.TEXT;

            if (FacebookShare.isFacebookPackage(packageName)) {
                sharingType = SharingApp.Type.FACEBOOK;
            } else if (TwitterShare.isTwitterPackage(packageName)) {
                sharingType = SharingApp.Type.TWITTER;
            } else if (BluetoothShare.isBluetoothPackage(packageName)) {
                sharingType = SharingApp.Type.BLUETOOTH;
            }

            apps.add(new SharingApp(this, resolveInfo, sharingType));
        }

        log.debug("Apps discovered as beeing able to share. See next debug records.");
        for (SharingApp app : apps) {
            log.debug("%s: %s", app.getType(), app.getPackageName());
        }

        sharingAdapter = new SharingAdapter(this, new ArrayList<SharingApp>(apps));
        listView.setAdapter(sharingAdapter);
        sharingAdapter.notifyDataSetChanged();

        facebookShare = new FacebookShare(this);
        twitterShare = new TwitterShare(this);

        backFAB = findViewById(R.id.fab_back);
        backFAB.setOnClickListener(this);
    }

    @Override
    public void onSharingAppSelected(SharingApp app) {
        log.trace("onSharingAppSelected(SharingApp) - %s", app.getAppName());
        AppBase.audit().shareApp(app.getAppName());

        switch (app.getType()) {
            case FACEBOOK:
                onFacebookShare();
                return;

            case TWITTER:
                onTwitterShare();
                return;

            case EMAIL:
                onEmailShare();
                return;

            case BLUETOOTH:
                BluetoothShare bluetoothShare = new BluetoothShare(this);
                bluetoothShare.send();
                return;

            case TEXT:
                break;

            default:
                throw new IllegalStateException();
        }

        // at this point user selected a generic text sending application

        // I do not have a reliable logic to determine EXTRA parameters supported by application able to send plain text
        // also it can happen that SEND action to not mean SHARE on target application activity
        // the only solution I have is educated guess: use SUBJECT and TEXT in the hope they are supported
        // SUBJECT is for completeness but TEXT is highly probably to be supported
        // if none supported sharing will silently fail

        final String url = storeURL();
        final String subject = getString(R.string.app_name);
        final String text = String.format("%s\r\n\r\n%s", getString(R.string.app_description), url);

        Intent share = new Intent(Intent.ACTION_SEND);
        // share intent is explicit since I know application package and activity class name
        share.setComponent(new ComponentName(app.getPackageName(), app.getActivityName()));

        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, subject);
        share.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(share);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        log.trace("onActivityResult(int, int, Intent)");
        super.onActivityResult(requestCode, resultCode, data);
        facebookShare.onActivityResult(requestCode, resultCode, data);
    }

    private void onFacebookShare() {
        log.trace("onFacebookShare()");
        final Uri site = Uri.parse(storeURL());
        final Uri image = Uri.parse(AppBase.repository().getFacebookShaingImageURL());

        facebookShare.post(getString(R.string.app_name), getString(R.string.app_description), site, image);
    }

    private void onTwitterShare() {
        log.trace("onTwitterShare()");
        final String text = String.format("%s. %s.", getString(R.string.app_name), getString(R.string.app_logotype));
        final String url = storeURL();

        twitterShare.post(text, url);
    }

    private void onEmailShare() {
        log.trace("onEmailShare()");
        // see FableSharedFragment#onEmailShare for comment regarding Email client (com.adroid.email) bug

        final String url = storeURL();
        final String subject = getString(R.string.app_name);
        final String body = String.format("%s.\r\n\r\n%s\r\n\r\n%s", getString(R.string.app_logotype), getString(R.string.app_description), url);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(emailIntent);
    }

    private String storeURL() {
        return "https://play.google.com/store/apps/details?id=" + getPackageName();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.start(this);
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_left);
        finish();
    }
}
