package com.example.chatup.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chatup.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Button

class SettingsActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val prefs by lazy { getSharedPreferences("chatup_settings", Context.MODE_PRIVATE) }

    private lateinit var swNotifications: SwitchMaterial

    private val requestNotifPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                saveNotificationsEnabled(true)
            } else {
                // رجّع السويتش OFF إذا رفض
                swNotifications.isChecked = false
                saveNotificationsEnabled(false)

                AlertDialog.Builder(this)
                    .setTitle("Notiser")
                    .setMessage("Du nekade tillåtelsen. Notiser är avstängda.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        swNotifications = findViewById(R.id.switchNotifications)
        val btnDelete = findViewById<Button>(R.id.btnDeleteAccount)

        // حمّل الحالة المحفوظة
        swNotifications.isChecked = prefs.getBoolean("notifications_enabled", true)

        swNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableNotificationsFlow()
            } else {
                saveNotificationsEnabled(false)
            }
        }

        btnDelete.setOnClickListener {
            showDeleteConfirmDialog()
        }
    }

    private fun enableNotificationsFlow() {
        // Android 13+ يحتاج POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestNotifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        // أجهزة أقدم أو permission already granted
        saveNotificationsEnabled(true)
    }

    private fun saveNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Radera konto?")
            .setMessage("Det här kommer att radera ditt konto permanent. Det går inte att ångra.")
            .setNegativeButton("Avbryt", null)
            .setPositiveButton("Radera") { _, _ -> deleteAccount() }
            .show()
    }

    private fun deleteAccount() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        // أولاً: احذف وثيقة المستخدم (Best effort)
        db.collection("users").document(uid).delete()
            .addOnCompleteListener {
                // ثم: احذف حساب Firebase Auth
                user.delete()
                    .addOnSuccessListener {
                        val intent = Intent(this, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(this)
                            .setTitle("Kunde inte radera konto")
                            .setMessage("Du behöver logga in igen och försöka på nytt (reauthentication).")
                            .setPositiveButton("OK", null)
                            .show()
                    }
            }
    }
}