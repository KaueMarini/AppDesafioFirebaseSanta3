package com.example.appdesafiofirebasesanta

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appdesafiofirebasesanta.databinding.ItemPedidoBinding
import com.google.firebase.firestore.FirebaseFirestore


class PedidosAdapter(private val pedidos: List<Pedido>) : RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder>() {

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
        holder.binding.tvStatusPedido.text = pedido.status


        if (pedido.status == "Pendente") {
            holder.binding.tvStatusPedido.setBackgroundColor(
                holder.itemView.context.getColor(android.R.color.holo_red_light)
            )
            holder.binding.root.setOnClickListener {

                val db = FirebaseFirestore.getInstance()
                pedido.id?.let { id ->
                    db.collection("pedidos").document(id)
                        .update("status", "Entregue")
                }
            }
        } else {
            holder.binding.tvStatusPedido.setBackgroundColor(
                holder.itemView.context.getColor(android.R.color.holo_green_light)
            )
            holder.binding.root.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        return pedidos.size
    }
}