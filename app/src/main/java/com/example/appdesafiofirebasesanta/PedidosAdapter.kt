package com.example.appdesafiofirebasesanta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appdesafiofirebasesanta.databinding.ItemPedidoBinding
import com.google.firebase.firestore.FirebaseFirestore

class PedidosAdapter(
    private val pedidos: List<Pedido>
) : RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder>() {


    private val statusOptions = listOf("Pendente", "Em preparo", "Pronto pra entrega", "Entregue")

    inner class PedidoViewHolder(val binding: ItemPedidoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val binding = ItemPedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PedidoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]

        holder.binding.tvDescricaoPedido.text = pedido.descricao
        holder.binding.tvNomeUsuario.text = "Cliente: ${pedido.nomeUsuario}"

        val spinnerAdapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_spinner_item,
            statusOptions
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        holder.binding.spinnerStatus.adapter = spinnerAdapter


        val currentStatusPosition = statusOptions.indexOf(pedido.status).coerceAtLeast(0)


        holder.binding.spinnerStatus.setSelection(currentStatusPosition, false)


        holder.binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val novoStatus = statusOptions[pos]


                if (novoStatus != pedido.status) {
                    val db = FirebaseFirestore.getInstance()
                    pedido.id?.let { pedidoId ->
                        db.collection("pedidos").document(pedidoId)
                            .update("status", novoStatus)

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    override fun getItemCount(): Int {
        return pedidos.size
    }
}