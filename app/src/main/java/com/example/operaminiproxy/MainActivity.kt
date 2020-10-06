package com.example.operaminiproxy

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class MainActivity : Activity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		when (intent?.action) {
			Intent.ACTION_SEND -> {
				if ("text/plain" == intent.type) {
					handleSendAction(intent) // Handle text being sent
				}
			}
			else -> {
				// Handle other intents, such as being started from the home screen
				setContentView(R.layout.activity_main)
			}
		}
	}

	private fun handleSendAction(intent: Intent) {
		var text = ""

		intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
			text = it
			Log.i("OperaMiniProxy", "EXTRA_TEXT: $text")
		}

		val webpage = Uri.parse(text)

		// Build the intent
		val browserIntent = Intent(Intent.ACTION_VIEW, webpage)

/*
		// createChooser not work, if you set some browser as default. In that case default browser
		// will always open all your URLs, even if "intent.component" defined.

		// Code from "Stack Overflow". There was example for several apps, I cut it to one.
		// I saved example to "android - Intent.createChooser.rar"
		var isFound = false
		val resolveInfoList = packageManager.queryIntentActivities(browserIntent, 0)
		for (resInfo in resolveInfoList) {
			val packageName = resInfo.activityInfo.packageName
			val name = resInfo.activityInfo.name
			if (packageName.contains("com.opera.mini")) { // full name is "com.opera.mini.native"
				browserIntent.component = ComponentName(packageName, name)
				isFound = true
				break
			}
		}

		if (isFound) {
			val chooserIntent = Intent.createChooser(browserIntent, null)
			startActivity(chooserIntent) // Chooser is always safe to call
		} else {
			toast("Opera Mini not found!")
		}
*/

		// https://www.apkmirror.com/ -> Opera Mini -> Android Studio -> Build -> Analyze APK... ->
		// -> AndroidManifest -> android.intent.action.VIEW
		browserIntent.component = ComponentName(
			"com.opera.mini.native",
			"com.opera.mini.android.Browser"
		)
		// More variants here: "android - Determining if an Activity exists on the current device_ - Stack Overflow.rar"
		// intent.resolveActivity(...) get name from "browserIntent.component" or OS. In our case it
		// will always return what we put in "browserIntent.component". Sic... Use info-variant.
		if (browserIntent.resolveActivityInfo(packageManager, 0) != null) {
			logd("Opera intent resolved")
			startActivity(browserIntent)
		} else {
			logd("Opera intent not resolved")
			toast("Opera Mini not found!")
		}

		// Same thing can be done by activity attribute android:noHistory="true" in AndroidManifest.xml.
		// It will work only if activity was replaced by another activity. This does not happen,
		// when intent not resolved, blank activity will stay on screen. So... kill them all manually.
		killMyApp()
	}

	private fun killMyApp() {
//		finish()
		finishAffinity()
//		exitProcess(0)
		// If you will use only finishAffinity(); without System.exit(0); your application will quit
		// but the allocated memory will still be in use by your phone, so... if you want a clean and
		// really quit of an app, use both of them.
	}
}

fun Activity.toast(message: String) {
	Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.logd(message: String) {
	if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, message)
}
