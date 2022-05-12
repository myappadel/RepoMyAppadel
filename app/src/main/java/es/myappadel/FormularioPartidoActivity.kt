package es.myappadel

import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.myappadel.clases.Partido
import es.myappadel.herramientas.MetodosFecha
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class FormularioPartidoActivity : AppCompatActivity() {
    lateinit var etDireccion: EditText
    lateinit var etFecha: EditText
    lateinit var spNumJugadores: Spinner
    lateinit var spProvincia: Spinner
    lateinit var spNivel: Spinner
    lateinit var btnCrear: Button
    lateinit var btnEliminar: Button

    lateinit var uidPartido: String
    var esModificar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_partido)

        etDireccion = findViewById(R.id.etPartidoDireccion)
        etFecha = findViewById(R.id.etPartidoFecha)
        spNumJugadores = findViewById(R.id.spNumJugadores)
        spProvincia = findViewById(R.id.spProvincia)
        spNivel = findViewById(R.id.spNivel)
        btnEliminar = findViewById(R.id.btnEliminarPartido)
        btnCrear = findViewById(R.id.btnCrearPartido)

        val bundle = intent.extras
        bundle?.let {
            uidPartido = it.getString("EXTRA_UID_PARTIDO").toString()
            esModificar = true
            btnCrear.text = "Modificar"
            btnEliminar.isVisible = true
            cargarPartidoFirestore(uidPartido)
        }

    }

    fun dibujarPartido(partido: Partido){
        partido?.let {

            etDireccion.setText("${it.direccion}")

            val listNumJugadores = resources.getStringArray(R.array.jugadores)
            val indexJugador = listNumJugadores.indexOf(it.numero_jugadores.toString())
            spNumJugadores.setSelection(indexJugador)

            val listNivel= resources.getStringArray(R.array.nivel)
            val indexNivel = listNivel.indexOf(it.nivel)
            spNivel.setSelection(indexNivel)

            val listProvincia= resources.getStringArray(R.array.provincias)
            val indexProvincia = listProvincia.indexOf(it.provincia)
            spProvincia.setSelection(indexProvincia)

            etFecha.setText(MetodosFecha.getFechaFormato(it.fecha!!.toDate()))


        }
    }
    fun cargarPartidoFirestore(uid: String){
        Firebase.firestore.collection("partidos").document(uid).get()
            .addOnSuccessListener {
                val partido = it.toObject(Partido::class.java)
                partido?.let { partidoIt ->
                    dibujarPartido(partido)
                }
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error al cargar el partido seleccionado", Toast.LENGTH_SHORT).show()
            }
    }
    fun eliminarPartidoFirestore(uid: String) {
        Firebase.firestore.collection("partidos").document(uid).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Partido eliminado", Toast.LENGTH_LONG)
                    .show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar partido.", Toast.LENGTH_LONG)
                    .show()
                finish()
            }
    }

    fun crearModificarPartidoFirestore(partido: Partido) {

        partido.uid?.let {
            Firebase.firestore.collection("partidos").document(it).set(partido)
                .addOnSuccessListener {
                    Toast.makeText(this, "Partido creado/modificado", Toast.LENGTH_LONG)
                        .show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Sucedió un error al crear el partido", Toast.LENGTH_LONG)
                        .show()
                }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun onClickCrearPartido(view: android.view.View) {
        val direccion = etDireccion.text.toString()
        val fecha = etFecha.text.toString()
        if (direccion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Debes de indicar una dirección y/o fecha", Toast.LENGTH_LONG)
                .show()
        } else {
            val fecha: Timestamp? = convertirFecha(fecha)
            if (fecha == null) {
                Toast.makeText(
                    this,
                    "Revisa el formato de la fecha, no es correcto.",
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                val nivel = spNivel.selectedItem.toString()
                val provincia = spProvincia.selectedItem.toString()
                val jugadores = spNumJugadores.selectedItem.toString().toInt()


                if (esModificar) {
                    val partida = Partido(
                        uid = uidPartido,
                        direccion = direccion,
                        fecha = fecha,
                        nivel = nivel,
                        numero_jugadores = jugadores,
                        provincia = provincia
                    )
                    crearModificarPartidoFirestore(partida)
                } else {
                    val partida = Partido(
                        uid = UUID.randomUUID().toString(),
                        direccion = direccion,
                        fecha = fecha,
                        nivel = nivel,
                        numero_jugadores = jugadores,
                        provincia = provincia
                    )
                    crearModificarPartidoFirestore(partida)

                }

            }

        }
    }

    fun onClickEliminar(view: android.view.View) {

        val builder = AlertDialog.Builder(this)
        builder.apply {
            setPositiveButton("SI",
                DialogInterface.OnClickListener { dialog, id ->
                    eliminarPartidoFirestore(uidPartido)
                })
            setNegativeButton("NO",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
        }
            .setCancelable(true)
            .setTitle("Estás seguro/a de eliminar este partido")

        builder.create().show()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertirFecha(fechaStr: String): Timestamp? {
        if (validarFecha(fechaStr)) {
            try {
                val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm");
                return Timestamp(formatter.parse(fechaStr))
            } catch (e: Exception) {
                return null
            }
        } else {
            return null
        }


    }

    fun validarFecha(fecha: String): Boolean {
        val PATTERN_DATE =
            "^([1-9]|([012][0-9])|(3[01]))/([0]{0,1}[1-9]|1[012])/\\d\\d\\d\\d [012]{0,1}[0-9]:[0-6][0-9]\$"
        return Pattern.matches(PATTERN_DATE, fecha)
    }

}