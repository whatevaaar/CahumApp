package com.cahum.cliente.modelo

class Cliente(
    val uid: String,
    val nombre: String,
    val tieneCv: Boolean = false,
    val perfilLinked: Boolean = false,
    val simulador: Boolean = false,
    val psicometria: Boolean = false,
    val estrategia: Boolean = false
)