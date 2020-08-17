package com.cahum.cliente.modelo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Cliente(
    val uid: String="",
    val nombre: String="",
    var tieneCv: Boolean = false,
    var perfilLinked: Boolean = false,
    var simulador: Boolean = false,
    var psicometria: Boolean = false,
    var estrategia: Boolean = false,
    val uidMentor: String = "",
    var tokenNotificacion: String = ""
) : Parcelable

