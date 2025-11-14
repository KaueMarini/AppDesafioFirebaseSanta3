package com.example.appdesafiofirebasesanta

data class Usuario(
    val uid: String? = null,
    val nome: String? = null,
    val email: String? = null,
    val role: String? = null, // "usuario" ou "restaurante"
    val nomeRestaurante: String? = null // Só preenchido se role="restaurante"
)