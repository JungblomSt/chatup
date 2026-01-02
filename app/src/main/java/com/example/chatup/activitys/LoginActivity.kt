package com.example.chatup.activitys

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatup.R
import com.example.chatup.StartMenuActivity
import com.example.chatup.databinding.ActivityLoginBinding
import com.example.chatup.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.btnRegisterAl.setOnClickListener {
            if (checkValidInput()) {
                register()
            }

        }
        binding.btnLoginAl.setOnClickListener {
            if (checkValidInput()) {
                login()
            }
        }
        binding.btnForgotPasswordAl.setOnClickListener {
            val email = binding.etForgotEmailAl.text.toString().trim()
            authViewModel.resetPassword(email)

        }
        authViewModel.resetPasswordResult.observe(this) { result ->
            result
                .onSuccess {
                    Toast.makeText(
                        this,
                        it,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .onFailure {
                    Toast.makeText(
                        this,
                        it.message ?: getString(R.string.wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    fun clearFields(){
        binding.etPasswordAl.text.clear()
        binding.etEmailAl.text.clear()
        binding.etForgotEmailAl.text.clear()
    }

    fun checkValidInput(): Boolean{
        var check = true

        if (binding.etPasswordAl.text.isBlank()){
            check = false
            Toast.makeText(this, getString(R.string.etPasswordAlBlank), Toast.LENGTH_SHORT).show()
        }
        if (binding.etEmailAl.text.isBlank()){
            check = false
            Toast.makeText(this, getString(R.string.etEmailAlBlank), Toast.LENGTH_SHORT).show()
        }
        if (binding.etPasswordAl.text.length < 6){
            check = false
            Toast.makeText(this, getString(R.string.etPasswordAlToShort), Toast.LENGTH_SHORT).show()
        }

        return check

    }
    fun login() {
        val email = binding.etEmailAl.text.toString()
        val password = binding.etPasswordAl.text.toString()

        authViewModel.login(email, password, {
            clearFields()
            val intent = Intent(this, StartMenuActivity::class.java)
            startActivity(intent)
        }, {
            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
        })
    }

    fun register() {
        val email = binding.etEmailAl.text.toString()
        val password = binding.etPasswordAl.text.toString()

        authViewModel.register(email, password) {
            if (it.isSuccessful){
                clearFields()
                val intent = Intent(this, StartMenuActivity::class.java)
                startActivity(intent)
            }else {
                Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }
}
