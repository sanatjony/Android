package com.myapp.installer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val btn = Button(this).apply { text = "ILOVANI O'RNATISH" }
        setContentView(btn)

        btn.setOnClickListener {
            checkPermissionAndInstall()
        }
    }

    private fun checkPermissionAndInstall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !packageManager.canRequestPackageInstalls()) {
            startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName")))
        } else {
            startInstall()
        }
    }

    private fun startInstall() {
        val apkName = "target_app.apk"
        val file = File(getExternalFilesDir(null), apkName)
        try {
            assets.open(apkName).use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Xato: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
