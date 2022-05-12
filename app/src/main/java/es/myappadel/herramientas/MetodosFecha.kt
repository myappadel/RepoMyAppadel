package es.myappadel.herramientas

import java.text.SimpleDateFormat
import java.util.*

object MetodosFecha {
    fun getNumero(date: Date): String {
       val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH).toString()
    }
    fun getNombreMes( date: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = date
        when(calendar.get(Calendar.MONTH)+1){
            1 -> return "Enero"
            2 -> return "Febrero"
            3 -> return "Marzo"
            4 -> return "Abril"
            5 -> return "Mayo"
            6 -> return "Junio"
            7 -> return "Julio"
            8 -> return "Agosto"
            9 -> return "Septiembre"
            10 -> return "Octubre"
            11 -> return "Noviembre"
            12 -> return "Diciembre"

        }
        return  "-";
    }

    fun getHora( date: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = date

        return  calendar.get(Calendar.HOUR_OF_DAY).toString()
    }
    fun getMinutos( date: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = date
        var minutos = calendar.get(Calendar.MINUTE).toString()
        if (minutos == "0"){
            minutos = "00"
        }
        return minutos
    }

    fun getFechaFormato(date: Date): String{
        var df: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
        return df.format(date)
    }
}