package com.example.lifestyle.Activities.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lifestyle.Activities.MainActivity
import com.example.lifestyle.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider

enum class ProvaiderType {
    BASIC,
    GOOGLE
}

class LoginActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    private lateinit var constraintLogin: ConstraintLayout // Declaración de la variable
    private lateinit var usuario: EditText
    private lateinit var password: EditText
    private lateinit var googleButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        constraintLogin = findViewById(R.id.constraintLogin)
        usuario = findViewById(R.id.idUsuario2)
        password = findViewById(R.id.idContrasenya2)
        googleButton = findViewById(R.id.googleButton)

        setup()
        session()


    }

    override fun onStart() {
        super.onStart()
        constraintLogin.visibility = View.VISIBLE
    }

    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email",null)
        val provider = prefs.getString("provider",null)

        Log.d("Session", "Email: $email, Provider: $provider") // Agregar este log
        if(email != null && provider != null){
            constraintLogin.visibility = View.VISIBLE
            showHome(email, ProvaiderType.valueOf(provider))
        }
    }

    private fun setup() {

        title = "autentificación"
        val login = findViewById<Button>(R.id.botonLogin)
        val usuario = findViewById<EditText>(R.id.idUsuario2)
        val password = findViewById<EditText>(R.id.idContrasenya2)
        val btnNavegateRegister = findViewById<Button>(R.id.botomNavegateRegister)

        login.setOnClickListener {
            if (usuario.text.isNotEmpty() && password.text.isNotEmpty()) {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                        usuario.text.toString(),
                        password.text.toString()
                    ).addOnCompleteListener {

                        if (it.isSuccessful) {
                            Toast.makeText(this, "Has iniciado sesión", Toast.LENGTH_SHORT).show()
                            Log.d(
                                "ActivityRegister",
                                "Usuario: ${usuario.text}, Contraseña: ${password.text}"
                            )
                            showHome(it.result?.user?.email ?: "", ProvaiderType.BASIC)

                        } else {
                            showAlert()
                        }
                    }
            }
        }

        btnNavegateRegister.setOnClickListener {
            val intent = Intent(this, ActivityRegister::class.java)
            startActivity(intent)
        }


        googleButton.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }


    }

    private fun showHome(usuario: String, provider: ProvaiderType) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", usuario)
            putExtra("provider", provider.name)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                val account = task.getResult(ApiException::class.java)

                if(account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if(it.isSuccessful){
                            showHome(account.email?: "", ProvaiderType.GOOGLE)
                        }else{
                            showAlert()
                        }
                    }
                }
            }catch (e: ApiException){
                showAlert()
            }
        }
    }


}