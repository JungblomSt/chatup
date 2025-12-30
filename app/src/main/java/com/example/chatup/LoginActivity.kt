package com.example.chatup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatup.databinding.ActivityLoginBinding
import com.example.chatup.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
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
                        it.message ?: "Fel uppstod",
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
            Toast.makeText(this, "lösenordet måste inehålla minst 6 tecken", Toast.LENGTH_SHORT).show()
        }
        if (binding.etEmailAl.text.isBlank()){
            check = false
            Toast.makeText(this, "Uppge en epostadress", Toast.LENGTH_SHORT).show()
        }
        if (binding.etPasswordAl.text.length < 6){
            check = false
            Toast.makeText(this, "lösenordet måste inehålla minst 6 tecken", Toast.LENGTH_SHORT).show()
        }

        return check

    }
    fun login() {
        val email = binding.etEmailAl.text.toString()
        val password = binding.etPasswordAl.text.toString()

        authViewModel.login(email, password, {
            clearFields()
            val intent = Intent(this, MainActivity::class.java)
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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else {
                Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }
}
