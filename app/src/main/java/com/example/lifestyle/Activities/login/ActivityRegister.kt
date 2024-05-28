package com.example.lifestyle.Activities.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.example.lifestyle.Activities.MainActivity
import com.example.lifestyle.R
import com.example.lifestyle.Users
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class ActivityRegister : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    var username: String = ""
    val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setup()
    }

    private fun setup() {
        title = "autentificación"

        val botonRegister = findViewById<Button>(R.id.botonRegister)
        val gmail = findViewById<EditText>(R.id.idUsuario)
        val password = findViewById<EditText>(R.id.idContrasenya)
        val nombreUsuario = findViewById<EditText>(R.id.idNombre)
        val btnNavigateLogin = findViewById<Button>(R.id.btnNavigateLogin)

        botonRegister.setOnClickListener {
            username = nombreUsuario.text.toString()
            if (gmail.text.isNotEmpty() && password.text.isNotEmpty()) {

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                        gmail.text.toString(), password.text.toString()
                    ).addOnCompleteListener {

                        if (password.text.length > 6) {

                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Error")
                            builder.setMessage("La contraseña debe contener al menos 6 carácteres")
                            builder.setPositiveButton("Aceptar", null)
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        } else if (it.isSuccessful) {
                            // Guardar los usuarios en la base de datos
                            val nuevoUsuario = Users(
                                gmail.text.toString(), password.text.toString(),
                                nombreUsuario.text.toString()
                            )
                            db.collection("usuarios").document(auth.currentUser!!.uid)
                                .set(nuevoUsuario)

                            Toast.makeText(this, "Te has registrado", Toast.LENGTH_SHORT).show()
                            Log.d("ActivityRegister", "La autenticación fue exitosa.")

                            val user = FirebaseAuth.getInstance().currentUser
                            showHome(it.result?.user?.email ?: "", ProvaiderType.BASIC)
                            showHome2(it.result?.user?.email ?: "", ProvaiderType.BASIC)

                            // Actualizar el perfil del usuario con el nombre de usuario
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build()

                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { profileUpdateTask ->
                                    if (profileUpdateTask.isSuccessful) {
                                        // Si el perfil del usuario se ha actualizado correctamente
                                        Toast.makeText(
                                            this,
                                            "Te has registrado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d(
                                            "ActivityRegister",
                                            "La autenticación fue exitosa. Nombre de usuario:${username}"
                                        )

                                    } else {
                                        // Error al actualizar el perfil del usuario
                                        Log.e(
                                            "ActivityRegister",
                                            "Error al actualizar el perfil del usuario: ${profileUpdateTask.exception}"
                                        )
                                        showAlert()
                                    }
                                }

                        } else {
                            Log.e(
                                "ActivityRegister",
                                "Error al autenticar usuario: ${it.exception}"
                            )
                            showAlert()
                        }

                    }
            }
        }

        btnNavigateLogin.setOnClickListener {
            val intent = Intent(this, ActivityRegister::class.java)
            startActivity(intent)
        }
    }


    private fun showHome(usuario: String, provaider: ProvaiderType) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", usuario)
            putExtra("provaider", provaider.name)
        }
        startActivity(intent)
    }

    private fun showHome2(usuario: String, provaider: ProvaiderType) {
        val intent = Intent(this, ActivityConf::class.java).apply {
            putExtra("email", usuario)
            putExtra("provaider", provaider.name)
        }
        startActivity(intent)
    }


    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("error")
        builder.setMessage("se ha producido un error autenticando el usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}