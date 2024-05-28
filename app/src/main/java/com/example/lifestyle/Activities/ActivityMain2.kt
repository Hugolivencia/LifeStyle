package com.example.lifestyle.Activities


import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lifestyle.Activities.login.ActivityRegister
import com.example.lifestyle.R
import com.example.lifestyle.Recipe
import com.example.lifestyle.databinding.ActivityMain2Binding
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


open class ActivityMain2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private val db = FirebaseFirestore.getInstance()
    val uri: Uri? = null
    val view: View? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var firebaseStorage: FirebaseStorage? = null
    var imageUri: Uri? = null // Declaración de la variable para almacenar la URI de la imagen
    var idFireStore: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)


        // Inicializar la base de datos
        FirebaseApp.initializeApp(this)

        // Inicializar el storage para guardar las imagenes
        FirebaseStorage.getInstance()
        subirimage()
        binding.imageButton.setOnClickListener {

            startActivity(Intent(applicationContext, MainActivity::class.java))

        }


        binding.saveButton.setOnClickListener {
            val ingredientes: MutableList<String> = mutableListOf()
            val pasosPreparacion: MutableList<String> = mutableListOf()

            for(i in 0 until binding.linearlayout2.childCount){
                val view = binding.linearlayout2.getChildAt(i)
                if(view is EditText){
                    val ingrediente = view.text.toString()
                    ingredientes.add(ingrediente)
                    guardarIngrediente(ingredientes!!)
                }
            }

            for(i in 0 until binding.linearlayout3.childCount){
                val view2 = binding.linearlayout3.getChildAt(i)
                if(view2 is EditText){
                    val pasoPreparacion = view2.text.toString()
                    pasosPreparacion.add(pasoPreparacion)
                    guardarPasos(pasosPreparacion!!)
                }
            }

            // Crear un objeto Recipe con los datos ingresados y la URI de la imagen
            val newRecipe = Recipe(
                idFireStore,
                recuperarUsuario(),
                binding.editTextRecipeName.text.toString(),
                binding.editTextDuration.text.toString(),
                binding.editTextCalories.text.toString(),
                ingredientes,
                binding.editTextDescription.text.toString(),
                imageUri.toString(),
                pasosPreparacion
            )



            if(binding.editTextRecipeName.text.toString().isEmpty()){
                Toast.makeText(this,"El campo de nombre es obligatorio",Toast.LENGTH_SHORT).show()
            }else {
                // Subir los datos a Firestore
                val db = FirebaseFirestore.getInstance()
                db.collection("recipes").add(newRecipe)
                    .addOnSuccessListener { documentReference ->

                        // Obtenemos la id generada por firStore
                        idFireStore = documentReference.id

                        // Actualizamos el documentos para agregar la id como campo del documento
                        documentReference.update("id", idFireStore)
                            .addOnSuccessListener {
                                Log.d(TAG, "ID almacenado como un campo dentro del documento.")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error al actualizar el campo 'id' dentro del documento.", e)
                            }

                        // Mostrar un mensaje de éxito o realizar otras acciones si es necesario
                        Toast.makeText(this, "Receta guardada correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Mostrar un mensaje de error si falla la subida
                        Toast.makeText(
                            this,
                            "Error al guardar la receta: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                startActivity(Intent(applicationContext, MainActivity::class.java))
            }


        }


        binding.imageViewRecipe.setOnClickListener {
            // Crear un intent para abrir la galería
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*" // Filtrar solo imágenes
            startActivityForResult(intent, PICK_IMAGE_REQUEST) // Iniciar la actividad de la galería
        }


        // Crear un nuevo text view
        binding.CrearTextView.setOnClickListener {
            // Crea un nuevo EditText programáticamente
            val newEditText = EditText(this)

            newEditText.id = View.generateViewId()  // Asignar un ID único al EditText
            newEditText.hint = "Escribe un nuevo ingrediente..."
            //newEditText.setBackgroundResource(R.drawable.edit_text_border) // Establecer un estilo
            newEditText.height = 110
            newEditText.textSize = 10f // Tamaño del texto
            newEditText.setPadding(10, 0, 0, 0) // Añadir el padding
            newEditText.setPaddingRelative(10, 10, 0, 10)
            // Agrega el EditText al LinearLayout
            binding.linearlayout2.addView(newEditText)
        }

        // Crear un nuevo text view
        binding.CrearText.setOnClickListener {
            // Crea un nuevo EditText programáticamente
            val nuevoEditText = EditText(this)

            nuevoEditText.id = View.generateViewId()  // Asignar un ID único al EditText
            nuevoEditText.hint = "Escribe un nuevo paso de preparación..."
            //nuevoEditText.setBackgroundResource(R.drawable.edit_text_border) // Establecer un estilo
            nuevoEditText.height = 110
            nuevoEditText.textSize = 10f // Tamaño del texto
            nuevoEditText.setPadding(10, 0, 0, 0) // Añadir el padding
            nuevoEditText.setPaddingRelative(10, 10, 0, 10)

            // Agrega el EditText al LinearLayout
            binding.linearlayout3.addView(nuevoEditText)
        }
    }

    private fun guardarIngrediente(ingrediente: MutableList<String>) {

        val db = db.collection("recipes").document(binding.editTextRecipeName.text.toString())

        val updateTask: Task<Void> = db.update("ingredientes", ingrediente)

        // Maneja el resultado de la actualización
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
        var nombreUsuario: String? = null  // Inicializar la variable fuera del bloque if

        if (usuarioActual != null) {
            // Obtener el nombre de usuario del usuario autentificado por Firebase
            nombreUsuario = usuarioActual.displayName
        }

        Log.d("ActivityRegister", "UsuarioActivity10: $nombreUsuario")
        return nombreUsuario
    }


    private fun guardarPasos(pasoPreparacion: MutableList<String>?) {

        val db = db.collection("recipes").document(binding.editTextRecipeName.text.toString())

        val updateTask: Task<Void> = db.update("pasosPreparacion", pasoPreparacion)

        // Maneja el resultado de la actualización
        updateTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Lista agregada correctamente al campo existente.")
            } else {
                println("Error al agregar la lista al campo existente: ${task.exception?.message}")
            }
        }

    }

    private fun subirimage() {
        val reference = firebaseStorage!!.reference.child("img")
            .child(System.currentTimeMillis().toString() + "")
        reference.putFile(uri!!).addOnSuccessListener {
            reference.downloadUrl.addOnSuccessListener { uri ->
                val recipe = Recipe()
                recipe.img = uri.toString()
                firebaseDatabase!!.reference.child("Imagenes").push()
                    .setValue(recipe).addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al Subir Imagen...", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    // Código de solicitud para seleccionar una imagen de la galería
    private val PICK_IMAGE_REQUEST = 1
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // La imagen se ha seleccionado correctamente desde la galería
            imageUri = data.data
            // Ahora puedes utilizar la Uri de la imagen como desees
            // Por ejemplo, mostrarla en un ImageView
            binding.imageViewRecipe.setImageURI(imageUri)
        }
    }





}