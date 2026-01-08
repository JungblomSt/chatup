package com.example.chatup.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chatup.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val prefs by lazy { getSharedPreferences("chatup_settings",Context.MODE_PRIVATE) }

    private lateinit var swNotifications: SwitchMaterial

    private val requestNotifPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                saveNotificationsEnabled(true)
            } else {
                // رجّع السويتش OFF إذا رفض
                swNotifications.setOnCheckedChangeListener(null)
                swNotifications.isChecked = false
                swNotifications.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) enableNotificationsFlow() else saveNotificationsEnabled(false)
                }

                saveNotificationsEnabled(false)

                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.notif_title))
                    .setMessage("Du nekade tillåtelsen. Notiser är avstängda.")
                    .setPositiveButton(getString(R.string.btn_ok), null)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<android.widget.ImageButton>(R.id.btnBackBurger).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        swNotifications = findViewById(R.id.switchNotifications)

        // Rensa lokal data
        findViewById<Button>(R.id.btnClearLocal).setOnClickListener { showClearLocalDialog() }

        // About / Terms
        findViewById<androidx.cardview.widget.CardView>(R.id.cardAbout).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.cardTerms).setOnClickListener {
            startActivity(Intent(this, TermsActivity::class.java))
        }

        // Delete
        findViewById<androidx.cardview.widget.CardView>(R.id.cardDelete).setOnClickListener {
            showDeleteConfirmDialog()
        }

        // Init switch state correctly (saved + real permission)
        val saved = prefs.getBoolean("notifications_enabled", false)
        val hasPermission = hasNotifPermission()

        swNotifications.setOnCheckedChangeListener(null)
        swNotifications.isChecked = saved && hasPermission
        if (saved && !hasPermission) saveNotificationsEnabled(false)

        swNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) enableNotificationsFlow() else saveNotificationsEnabled(false)
        }
    }

    private fun hasNotifPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
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
        saveNotificationsEnabled(true)
        Toast.makeText(this, "Notiser aktiverade ✅", Toast.LENGTH_SHORT).show()
    }

    private fun saveNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    private fun showClearLocalDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dlg_clear_title))
            .setMessage(getString(R.string.dlg_clear_msg))
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .setPositiveButton(getString(R.string.clear_btn)) { _, _ ->
                clearLocalData()
            }
            .show()
    }

    private fun clearLocalData() {
        // 1) Clear settings prefs
        prefs.edit().clear().apply()

        // 2) Clear cache (safe local only)
        runCatching { cacheDir.deleteRecursively() }

        // Reset UI switch
        swNotifications.setOnCheckedChangeListener(null)
        swNotifications.isChecked = false
        swNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) enableNotificationsFlow() else saveNotificationsEnabled(false)
        }

        Toast.makeText(this, "Lokal data rensad ✅", Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dlg_delete_title))
            .setMessage(getString(R.string.dlg_delete_msg))
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .setPositiveButton(getString(R.string.btn_delete)) { _, _ -> deleteAccount() }
            .show()
    }

    private fun deleteAccount() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        // Best-effort: delete Firestore doc first, then Auth user
        db.collection("users").document(uid).delete()
            .addOnCompleteListener {
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
                            .setPositiveButton(getString(R.string.btn_ok), null)
                            .show()
                    }
            }
    }
}