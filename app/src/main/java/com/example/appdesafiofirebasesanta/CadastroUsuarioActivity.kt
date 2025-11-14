package com.example.appdesafiofirebasesanta

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appdesafiofirebasesanta.databinding.ActivityCadastroUsuarioBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class CadastroUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroUsuarioBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore


        binding.rgRoles.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_restaurante) {
                binding.tilNomeRestaurante.visibility = View.VISIBLE
            } else {
                binding.tilNomeRestaurante.visibility = View.GONE
            }
        }

        binding.btnCadastrar.setOnClickListener {
            cadastrarUsuario()
        }
    }

    private fun cadastrarUsuario() {
        val nome = binding.edtNomeCadastro.text.toString().trim()
        val email = binding.edtEmailCadastro.text.toString().trim()
        val senha = binding.edtSenhaCadastro.text.toString().trim()
        val nomeRestaurante = binding.edtNomeRestaurante.text.toString().trim()

        val role = if (binding.rbRestaurante.isChecked) "restaurante" else "usuario"

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha nome, email e senha.", Toast.LENGTH_SHORT).show()
            return
        }

        if (role == "restaurante" && nomeRestaurante.isEmpty()) {
            Toast.makeText(this, "Preencha o nome do restaurante.", Toast.LENGTH_SHORT).show()
            return
        }


        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user!!.uid


                val usuario = Usuario(
                    uid = uid,
                    nome = nome,
                    email = email,
                    role = role,
                    nomeRestaurante = if (role == "restaurante") nomeRestaurante else null
                )

                db.collection("usuarios").document(uid).set(usuario)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao criar conta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }