package es.myappadel.clases

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class Partido(
    var uid: String? = null,
    var direccion: String? = null,
    var fecha: Timestamp? = Timestamp.now(),
    var nivel: String? = null,
    var numero_jugadores: Int? = 2,
    var provincia: String? = null,
    var users: List<String>? = listOf(),
    var creadoPor: String = Firebase.auth.currentUser?.uid.toString()
)
