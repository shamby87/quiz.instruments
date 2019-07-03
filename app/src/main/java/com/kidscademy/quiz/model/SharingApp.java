package com.kidscademy.quiz.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.kidscademy.quiz.instruments.R;

/**
 * Immutable descriptor for application providing sharing services. An application is unique identified by its package
 * name; two instances are considered equal if have the same package name no matter other fields, like sharing type.
 * 
 * @author Iulian Rotaru
 */
public class SharingApp implements Comparable<SharingApp>
{
  /**
   * Sharing type.
   * 
   * @author Iulian Rotaru
   * @see SharingApp
   */
  public static enum Type
  {
    NONE, TEXT, FACEBOOK, TWITTER, EMAIL, BLUETOOTH
  }

  private String appName;
  private String activityName;
  private String packageName;
  private String version;
  private Drawable icon;
  private Type type;

  public SharingApp(Context context, ResolveInfo resolveInfo, Type type)
  {
    PackageManager packageManager = context.getPackageManager();

    this.appName = resolveInfo.loadLabel(packageManager).toString();
    this.activityName = resolveInfo.activityInfo.name;
    this.packageName = resolveInfo.activityInfo.packageName;
    try {
      this.version = packageManager.getPackageInfo(packageName.toString(), 0).versionName;
    }
    catch(NameNotFoundException unused) {
      this.version = context.getString(R.string.unknown);
    }
    this.icon = resolveInfo.loadIcon(packageManager);
    this.type = type;
  }

  public String getAppName()
  {
    return appName;
  }

  public String getActivityName()
  {
    return activityName;
  }

  public String getPackageName()
  {
    return packageName;
  }

  public String getVersion()
  {
    return version;
  }

  public Drawable getIcon()
  {
    return icon;
  }

  public Type getType()
  {
    return type;
  }

  @Override
  public int compareTo(SharingApp other)
  {
    return packageName.compareTo(other.packageName);
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if(obj == null) return false;
    if(getClass() != obj.getClass()) return false;
    SharingApp other = (SharingApp)obj;
    if(packageName == null) {
      if(other.packageName != null) return false;
    }
    else if(!packageName.equals(other.packageName)) return false;
    return true;
  }

  @Override
  public String toString()
  {
    return this.packageName.toString();
  }
}
