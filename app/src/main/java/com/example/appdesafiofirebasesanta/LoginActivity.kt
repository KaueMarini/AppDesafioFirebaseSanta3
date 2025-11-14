package com.example.appdesafiofirebasesanta

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appdesafiofirebasesanta.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        binding.btnLogin.setOnClickListener {
            fazerLogin()
        }

        binding.btnIrCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroUsuarioActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            redirecionarPorRole(currentUser.uid)
        }
    }

    private fun fazerLogin() {
        val email = binding.edtEmail.text.toString()
        val senha = binding.edtSenha.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha email e senha.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { authResult ->
                Toast.makeText(this, "Login com sucesso!", Toast.LENGTH_SHORT).show()
                redirecionarPorRole(authResult.user!!.uid)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro no login: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun redirecionarPorRole(uid: String) {

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    val role = document.getString("role")


                    if (role == "restaurante") {
                        val intent = Intent(this, ListaPedidosActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, CriarPedidoActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Erro: Dados do usuário não encontrados.",
                        Toast.LENGTH_SHORT
                    ).show()
                    auth.signOut()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar dados.", Toast.LENGTH_SHORT).show()
                auth.signOut()
            }
    }
}