package es.myappadel

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.myappadel.clases.Partido
import es.myappadel.herramientas.MetodosFecha

class DetallePartidoActivity : AppCompatActivity() {
    
    lateinit var tvHora: TextView
    lateinit var tvFecha: TextView
    lateinit var tvDir: TextView
    lateinit var tvNumJugadores: TextView
    lateinit var tvNivel: TextView
    lateinit var btnInscribirme: Button
    lateinit var uidPartido: String
    lateinit var partidoSave: Partido
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_partido)
        
        tvHora = findViewById(R.id.tvDetallesHora)
        tvFecha = findViewById(R.id.tvDetallesFecha)
        tvDir = findViewById(R.id.tvDetallesDir)
        tvNumJugadores = findViewById(R.id.tvDetallesNumPersonas)
        tvNivel = findViewById(R.id.tvDetallesNivel)
        btnInscribirme = findViewById(R.id.btnInscribirse)
        
        val bundle  = intent.extras
        
        bundle?.let {
            uidPartido = it.get("EXTRA_UID_PARTIDO").toString()
            cargarPartidoFirestore(uidPartido)
        }
        
        
    }
    @SuppressLint("SetTextI18n")
    fun dibujarPartidoDetalles(partido: Partido?){
        partido?.let {
            val fechaNombre = MetodosFecha.getNombreMes(it.fecha!!.toDate())
            val fechaNumero = MetodosFecha.getNumero(it.fecha!!.toDate())
            val horaNombre = MetodosFecha.getHora(it.fecha!!.toDate())
            val minutos = MetodosFecha.getMinutos(it.fecha!!.toDate())
            tvFecha.text = "$fechaNumero de $fechaNombre"
            tvHora.text = "$horaNombre:$minutos"
            tvDir.text = "${it.direccion}, (${it.provincia})"
            if(it.users!=null){
                tvNumJugadores.text = "${it.users!!.size}/${it.numero_jugadores}"
            }else{
                tvNumJugadores.text =  "0/${it.numero_jugadores}"
            }
            tvNivel.text = "${it.nivel}"


            Firebase.auth.currentUser?.let{
                partido.users?.let{ users ->
                    if(users.contains(it.uid)){
                        btnInscribirme.text = "Borrarme del partido"
                    }else{
                        if(users.size == partido.numero_jugadores){
                            btnInscribirme.text = "Partido completo"
                            btnInscribirme.isEnabled = false
                        }else{
                            btnInscribirme.text = "Inscribirme en el partido"
                        }
                    }
                }
            }
        }
    }
    fun inscribirPartidoFirestore(uidPartido: String, uidUser: String){
        Firebase.firestore.collection("partidos").document(uidPartido).update(
            "users",
            FieldValue.arrayUnion(uidUser)
        ).addOnSuccessListener {
            Toast.makeText(applicationContext, "Te has inscrito al partido", Toast.LENGTH_SHORT).show()
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error al inscribirse al partido", Toast.LENGTH_SHORT).show()
            }

    }
    fun borrarInscripconPartidoFirestore(uidPartido: String, uidUser: String){
        Firebase.firestore.collection("partidos").document(uidPartido).update(
            "users",
            FieldValue.arrayRemove(uidUser)
        ).addOnSuccessListener {
            Toast.makeText(applicationContext, "Te has inscrito al partido", Toast.LENGTH_SHORT).show()
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error al inscribirse al partido", Toast.LENGTH_SHORT).show()
            }

    }
    fun cargarPartidoFirestore(uid: String){
        Firebase.firestore.collection("partidos").document(uid).get()
            .addOnSuccessListener {
                val partido = it.toObject(Partido::class.java)
                partido?.let { partidoIt ->
                    partidoSave = partidoIt
                }
                dibujarPartidoDetalles(partido)
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error al cargar el partido seleccionado", Toast.LENGTH_SHORT).show()
            }
    }
    fun onClickInscribirse(view: android.view.View) {
        Firebase.auth.currentUser?.let{
            partidoSave.users?.let{ users ->
                if(users.contains(it.uid)){
                    borrarInscripconPartidoFirestore(partidoSave.uid!!,it.uid)
                }else{
                    inscribirPartidoFirestore(partidoSave.uid!!,it.uid)
                }
            }
        }
    }
}