package com.cahum.cliente.modelo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Mentor(
    val uid: String = "",
    val nombre: String = "",
    val calificacion: Double = 0.0
) : Parcelable
