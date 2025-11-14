package com.example.appdesafiofirebasesanta

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appdesafiofirebasesanta.databinding.ActivityListaPedidosBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class ListaPedidosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaPedidosBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var pedidosAdapter: PedidosAdapter
    private val listaDePedidos = mutableListOf<Pedido>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore

        setupRecyclerView()
        fetchPedidos()
    }

    private fun setupRecyclerView() {
        pedidosAdapter = PedidosAdapter(listaDePedidos)
        binding.recyclerViewPedidos.apply {
            adapter = pedidosAdapter
            layoutManager = LinearLayoutManager(this@ListaPedidosActivity)
        }
    }

    private fun fetchPedidos() {
        val restauranteId = Firebase.auth.currentUser?.uid
        if (restauranteId == null) {
            Toast.makeText(this, "Erro: Restaurante não logado.", Toast.LENGTH_SHORT).show()
            return
        }


        db.collection("pedidos")
            .whereEqualTo("restauranteId", restauranteId)
            .orderBy("status", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->

                if (e != null) {
                    Log.w("ListaPedidos", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    listaDePedidos.clear()
                    for (document in snapshot.documents) {

                        val pedido = document.toObject<Pedido>()
                        if (pedido != null) {
                            listaDePedidos.add(pedido)
                        }
                    }
                    pedidosAdapter.notifyDataSetChanged()
                }
            }
    }
}