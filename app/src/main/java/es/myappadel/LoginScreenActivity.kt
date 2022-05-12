package es.myappadel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginScreenActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            setContentView(R.layout.activity_login_screen)
            title = "Login"
            etEmail = findViewById(R.id.etLoginEmail)
            etPassword = findViewById(R.id.etPasswordLogin)
        }
    }

    fun onClickRecuperarContrasena(view: View) {

        val email = etEmail.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Debes de rellenar un email", Toast.LENGTH_LONG).show()
        } else {
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Email enviado, revisa tu bandeja de entrada",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }.addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Ocurrió un error al enviar el email, revisa tus datos.",
                        Toast.LENGTH_LONG
                    ).show()

                }
        }
    }

    fun onClickRegistro(view: View) {
        val intent = Intent(this, SingUpScreenActivity::class.java)
        startActivity(intent)
    }

    fun onClickAcceder(view: View) {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Debes de rellenar email y password", Toast.LENGTH_LONG).show()
        } else {
            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            baseContext, "¡Error! Ocurrió un error al intentar acceder",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
        }

    }
}