package com.example.chatup

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chatup.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnRegisterAl.setOnClickListener {
            signUp()

        }
        binding.btnLoginAl.setOnClickListener {
            signIn()
        }
    }

    fun signIn() {
        val email = binding.etEmailAl.text.toString()
        val password = binding.etPasswordAl.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    Log.d("!!!", "login success")
                } else{
                    Log.d("!!!", "user not logged in ${task.exception}")
                }
            }

    }

    fun signUp() {
        val email = binding.etEmailAl.text.toString()
        val password = binding.etPasswordAl.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    Log.d("!!!", "create success")
                } else{
                    Log.d("!!!", "user not created ${task.exception}")
                }
            }

    }
}
