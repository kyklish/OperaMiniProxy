package com.example.operaminiproxy

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlin.system.exitProcess

class MainActivity : Activity() {
	override fun onPause() {
		super.onPause()
		Log.i("OperaMiniProxy", "onPause Activity")
	}

	override fun onDestroy() {
		super.onDestroy()
		Log.i("OperaMiniProxy", "onDestroy Activity")
	}

	override fun onStart() {
		super.onStart()
		Log.i("OperaMiniProxy", "onStart Activity")
	}

	override fun onStop() {
		super.onStop()
		Log.i("OperaMiniProxy", "onStop Activity")
	}

	override fun onResume() {
		super.onResume()
		Log.i("OperaMiniProxy", "onResume Activity")
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Log.i("OperaMiniProxy", "onCreate Activity")

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
		var isFound = false

		intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
			text = it
			Log.i("OperaMiniProxy", "EXTRA_TEXT: $text")
		}

		val webpage = Uri.parse(text)

		// Build the intent
		val browserIntent = Intent(Intent.ACTION_VIEW, webpage)

		// Code from "Stack Overflow". There was example for several apps, I cut it to one.
		// I saved example to "android - Intent.createChooser.rar"
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
			Toast.makeText(this@MainActivity, "Opera Mini not installed!", Toast.LENGTH_SHORT)
				.show()
		}

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
