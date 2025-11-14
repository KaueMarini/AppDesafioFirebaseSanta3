package com.example.appdesafiofirebasesanta

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appdesafiofirebasesanta.databinding.ActivityCriarPedidoBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class CriarPedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriarPedidoBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    private val restaurantesMap = mutableMapOf<String, String>()
    private var nomeUsuarioLogado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriarPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        auth = Firebase.auth

        if (auth.currentUser == null) {

            finish()
            return
        }

        carregarDadosUsuario()
        carregarRestaurantes()

        binding.btnFazerPedido.setOnClickListener {
            enviarPedido()
        }
    }

    private fun carregarDadosUsuario() {
        db.collection("usuarios").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                nomeUsuarioLogado = it.getString("nome")
            }
    }

    private fun carregarRestaurantes() {
        db.collection("usuarios")
            .whereEqualTo("role", "restaurante")
            .get()
            .addOnSuccessListener { snapshot ->
                restaurantesMap.clear()
                val nomesRestaurantes = mutableListOf<String>()

                for (document in snapshot.documents) {
                    val restaurante = document.toObject<Usuario>()
                    if (restaurante != null && restaurante.nomeRestaurante != null && restaurante.uid != null) {
                        restaurantesMap[restaurante.nomeRestaurante] = restaurante.uid
                        nomesRestaurantes.add(restaurante.nomeRestaurante)
                    }
                }


                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nomesRestaurantes)
                binding.autoCompleteRestaurante.setAdapter(adapter)
            }
    }

    private fun enviarPedido() {
        val nomeRestauranteSelecionado = binding.autoCompleteRestaurante.text.toString()
        val descricao = binding.edtDescricaoPedido.text.toString().trim()

        if (nomeUsuarioLogado == null) {
            Toast.makeText(this, "Aguarde, carregando dados...", Toast.LENGTH_SHORT).show()
            return
        }


        val restauranteId = restaurantesMap[nomeRestauranteSelecionado]
        val usuarioId = auth.currentUser!!.uid

        if (restauranteId == null) {
            Toast.makeText(this, "Selecione um restaurante válido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (descricao.isEmpty()) {
            Toast.makeText(this, "Descreva seu pedido.", Toast.LENGTH_SHORT).show()
            return
        }

        val pedido = Pedido(
            usuarioId = usuarioId,
            nomeUsuario = nomeUsuarioLogado,
            restauranteId = restauranteId,
            descricao = descricao,
            status = "Pendente"
        )


        db.collection("pedidos").add(pedido)
            .addOnSuccessListener {
                Toast.makeText(this, "Pedido enviado com sucesso!", Toast.LENGTH_SHORT).show()
                binding.autoCompleteRestaurante.text.clear()
                binding.edtDescricaoPedido.text?.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}