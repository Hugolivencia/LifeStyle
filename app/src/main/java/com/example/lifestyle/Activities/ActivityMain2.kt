package com.example.lifestyle.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lifestyle.R
import com.example.lifestyle.Recipe
import com.example.lifestyle.databinding.ActivityMain2Binding
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

open class ActivityMain2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseStorage: FirebaseStorage
    private var imageUri: Uri? = null
    private var idFireStore: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        firebaseStorage = FirebaseStorage.getInstance()

        binding.imageButton.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        binding.saveButton.setOnClickListener {
            saveRecipe()
        }

        binding.imageViewRecipe.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.CrearTextView.setOnClickListener {
            addNewEditText(binding.linearlayout2, "Escribe un nuevo ingrediente...")
        }

        binding.CrearText.setOnClickListener {
            addNewEditText(binding.linearlayout3, "Escribe un nuevo paso de preparación...")
        }
    }

    private fun addNewEditText(layout: ViewGroup, hint: String) {
        val newEditText = EditText(this)
        newEditText.id = View.generateViewId()
        newEditText.hint = hint
        newEditText.height = 110
        newEditText.textSize = 10f
        newEditText.setPadding(10, 0, 0, 0)
        newEditText.setPaddingRelative(10, 10, 0, 10)
        layout.addView(newEditText)
    }

    private fun saveRecipe() {
        val ingredientes: MutableList<String> = mutableListOf()
        val pasosPreparacion: MutableList<String> = mutableListOf()

        for (i in 0 until binding.linearlayout2.childCount) {
            val view = binding.linearlayout2.getChildAt(i)
            if (view is EditText) {
                val ingrediente = view.text.toString()
                ingredientes.add(ingrediente)
                guardarIngrediente(ingredientes)
            }
        }

        for (i in 0 until binding.linearlayout3.childCount) {
            val view2 = binding.linearlayout3.getChildAt(i)
            if (view2 is EditText) {
                val pasoPreparacion = view2.text.toString()
                pasosPreparacion.add(pasoPreparacion)
                guardarPasos(pasosPreparacion)
            }
        }

        val newRecipe = Recipe(
            idFireStore,
            recuperarUsuario(),
            binding.editTextRecipeName.text.toString(),
            binding.editTextDuration.text.toString(),
            binding.editTextCalories.text.toString(),
            ingredientes,
            binding.editTextDescription.text.toString(),
            imageUri?.toString() ?: "",
            pasosPreparacion
        )

        if (binding.editTextRecipeName.text.toString().isEmpty()) {
            Toast.makeText(this, "El campo de nombre es obligatorio", Toast.LENGTH_SHORT).show()
        } else {
            db.collection("recipes").add(newRecipe)
                .addOnSuccessListener { documentReference ->
                    idFireStore = documentReference.id
                    documentReference.update("id", idFireStore)
                        .addOnSuccessListener {
                            Log.d("TAG", "ID almacenado como un campo dentro del documento.")
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error al actualizar el campo 'id' dentro del documento.", e)
                        }
                    Toast.makeText(this, "Receta guardada correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar la receta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }

    private fun guardarIngrediente(ingrediente: MutableList<String>) {
        val db = db.collection("recipes").document(binding.editTextRecipeName.text.toString())
        val updateTask: Task<Void> = db.update("ingredientes", ingrediente)
        updateTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Lista agregada correctamente al campo existente.")
            } else {
                println("Error al agregar la lista al campo existente: ${task.exception?.message}")
            }
        }
    }

    private fun recuperarUsuario(): String? {
        val auth = FirebaseAuth.getInstance()
        val usuarioActual = auth.currentUser
        var nombreUsuario: String? = null
        if (usuarioActual != null) {
            nombreUsuario = usuarioActual.displayName
        }
        Log.d("ActivityRegister", "UsuarioActivity10: $nombreUsuario")
        return nombreUsuario
    }

    private fun guardarPasos(pasoPreparacion: MutableList<String>?) {
        val db = db.collection("recipes").document(binding.editTextRecipeName.text.toString())
        val updateTask: Task<Void> = db.update("pasosPreparacion", pasoPreparacion)
        updateTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Lista agregada correctamente al campo existente.")
            } else {
                println("Error al agregar la lista al campo existente: ${task.exception?.message}")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            binding.imageViewRecipe.setImageURI(imageUri)
            subirImageAlStorege(imageUri)
        }
    }

    private fun subirImageAlStorege(imageUri: Uri?) {
        if (imageUri != null) {
            // Crea una referencia en Firebase Storage
            val storageReference = firebaseStorage.reference.child("img/${System.currentTimeMillis()}.jpg")

            // Sube el archivo a Firebase Storage
            storageReference.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Obtén la URL de descarga pública del archivo subido
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        // Guarda la URL en Realtime Database o Firestore
                        guardarImagen(downloadUrl)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Upload failed", e)
                }
        }
    }

    private fun guardarImagen(downloadUrl: String) {
        val imageInfo = hashMapOf(
            "url" to downloadUrl,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("images").add(imageInfo)
            .addOnSuccessListener {
                Toast.makeText(this, "URL de la imagen guardada en la base de datos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar la URL en la base de datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
