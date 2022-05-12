package es.myappadel.herramientas

import android.graphics.Color
import android.opengl.Visibility
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.myappadel.clases.Partido

@BindingAdapter("setNumeroJugadores")
fun TextView.setNumeroJugadores(partido: Partido){
    if(partido.users!=null){
        text = "${partido.users!!.size}/${partido.numero_jugadores}"
    }else{
        text =  "0/${partido.numero_jugadores}"
    }
}
@BindingAdapter("setLayoutUser")
fun LinearLayout.setLayoutUser(partido: Partido){
    visibility = if(Firebase.auth.currentUser?.uid.toString() == partido.creadoPor){
        VISIBLE
    }else{
        INVISIBLE
    }
}

@BindingAdapter("setFechaDia")
fun TextView.setFechaDia(partido: Partido){
    partido.fecha?.let {
        val mesNombre = MetodosFecha.getNombreMes(partido.fecha!!.toDate())
        val dia = MetodosFecha.getNumero(partido.fecha!!.toDate())
        text = "$dia de $mesNombre"
    }
}
@BindingAdapter("setHora")
fun TextView.setHora(partido: Partido){
    partido.fecha?.let {
        val horas = MetodosFecha.getHora(partido.fecha!!.toDate())
        val minutos = MetodosFecha.getMinutos(partido.fecha!!.toDate())
        text = "$horas:$minutos"
    }
}