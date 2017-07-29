package tmnt.example.androidutils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class GetSystemInfo {
	public static String getVersionName(Activity activity) {
		
		PackageManager packageManager = activity.getPackageManager();
		
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(activity.getPackageName(),
					0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packInfo.versionName;
	}

	public static int getVersionCode(Activity activity) {
		
		PackageManager packageManager = activity.getPackageManager();
		
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(activity.getPackageName(),
					0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packInfo.versionCode;
	}
}
