package com.example.appdesafiofirebasesanta

import com.google.firebase.firestore.DocumentId


data class Pedido(
    @DocumentId
    val id: String? = null,
    val usuarioId: String? = null,
    val nomeUsuario: String? = null,
    val restauranteId: String? = null,
    val descricao: String? = null,
    val status: String? = null
)