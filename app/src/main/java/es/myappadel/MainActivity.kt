package es.myappadel

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.myappadel.adapter.ClickItem
import es.myappadel.adapter.PartidoAdapter
import es.myappadel.clases.Partido

class MainActivity : AppCompatActivity() {
    lateinit var rvPartidos: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var spProvinciasBuscar: Spinner
    lateinit var btnSearch: ImageButton
    lateinit var btnCrearPartido: FloatingActionButton
    lateinit var partidoAdapter: PartidoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        rvPartidos = findViewById(R.id.rvPartidos)
        spProvinciasBuscar = findViewById(R.id.spBusqueda)
        btnSearch = findViewById(R.id.btnSearch)
        btnCrearPartido = findViewById(R.id.fbCrearPartido)
        btnCrearPartido.setOnClickListener {
            val intent = Intent(this, FormularioPartidoActivity::class.java)
            startActivity(intent)
        }
        btnSearch.setOnClickListener {
            progressBar.isVisible = true
            rvPartidos.removeAllViews()
            val provincia = spProvinciasBuscar.selectedItem.toString()
            cargarPartidosFirestore(provincia)
        }
        partidoAdapter = PartidoAdapter(ClickItem {
            FirebaseAuth.getInstance().currentUser?.let { user ->
                if(user.uid == it.creadoPor){
                    val intent = Intent(applicationContext, FormularioPartidoActivity::class.java).apply {
                        putExtra("EXTRA_UID_PARTIDO",it.uid)
                    }
                    startActivity(intent)
                }else{
                    val intent = Intent(applicationContext, DetallePartidoActivity::class.java).apply {
                        putExtra("EXTRA_UID_PARTIDO",it.uid)
                    }
                    startActivity(intent)
                }

            }



        })
        cargarPartidosFirestore()


    }

    override fun onStart() {
        super.onStart()
        cargarPartidosFirestore()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_principal, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.menu_logout -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun cargarPartidosFirestore(provinciaBuscar: String = ""){

        Firebase.firestore.collection("partidos").orderBy("fecha", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                val tempPartidosList = mutableListOf<Partido>()
                for(document in it.documents) {
                    val partido = document.toObject(Partido::class.java)
                    if (partido != null) {
                        if(provinciaBuscar!=""){
                            if(  provinciaBuscar == partido.provincia)
                                tempPartidosList.add(partido)
                        }else{
                            tempPartidosList.add(partido)
                        }
                    }
                }
                if(tempPartidosList.size == 0 )
                    Toast.makeText(applicationContext, "No hay partidos disponible en $provinciaBuscar", Toast.LENGTH_SHORT).show()

                partidoAdapter.submitList(tempPartidosList)
                partidoAdapter.notifyDataSetChanged()
                rvPartidos.adapter = partidoAdapter
                progressBar.isVisible = false
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext,  "Error al cargar los partidos", Toast.LENGTH_SHORT).show()
            }
    }

    fun signOut() {


            val builder = AlertDialog.Builder(this)
            builder.apply {
                setPositiveButton("SI",
                    DialogInterface.OnClickListener { dialog, id ->
                        Firebase.auth.signOut()
                        startActivity(Intent(applicationContext, LoginScreenActivity::class.java))
                        finish()
                    })
                setNegativeButton("NO",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            }
                .setCancelable(true)
                .setTitle("Estás seguro/a de cerrar sesion")

            builder.create().show()



    }
}