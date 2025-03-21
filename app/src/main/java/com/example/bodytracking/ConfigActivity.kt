package com.example.bodytracking

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView

import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bodytracking.databinding.ActivityConfigBinding
import com.example.bodytracking.databinding.CustomImageBoxBinding
import java.io.File
import java.io.FileOutputStream


class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding
    private lateinit var dialogBinding: CustomImageBoxBinding
    lateinit var dialog: Dialog





    //-------------

    //-------------
    private fun saveImageToInternalStorage(imageUri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val filename = "profile_image.jpg"
            val file = File(filesDir, filename)

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()

            file.absolutePath // Retorna o caminho da imagem salva
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    //-------------
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                if (imageUri != null) {
                    val imagePath = saveImageToInternalStorage(imageUri) // Salva a imagem e obtém o caminho
                    if (imagePath != null) {
                        saveImagePathToPreferences(imagePath)
                        dialogBinding.circleUser.setImageURI(Uri.parse(imagePath)) // Exibe a imagem salva
                    }
                }
            }
        }
    //-------------
    private fun saveImagePathToPreferences(imagePath: String) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        sharedPreferences.edit().putString("profile_image_path", imagePath).apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val window: Window = this.getWindow()
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //  add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.actionBarColor));

        binding.icReturn.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        dialog = Dialog(this)
        dialogBinding = CustomImageBoxBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics).toInt()
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400f, resources.displayMetrics).toInt()
        dialog.window?.setLayout(height,width)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.bg_dialog_box))


        loadSavedImage(binding.root.context, binding.circleUser)
        binding.circle.setOnClickListener {
            dialog.show()
            loadSavedImage(dialogBinding.root.context, dialogBinding.circleUser)

        }

//        val composeView = binding.customSwitch
//        composeView.setContent {
//            CustomSwitch() // Apenas o conteúdo do Switch, sem constraints
//        }

        fun Int.dpToPx(): Int {
            return (this * resources.displayMetrics.density).toInt()
        }
        val customSwitchWidth = (resources.displayMetrics.widthPixels - resources.displayMetrics.widthPixels*0.42 - 40.dpToPx()).toInt()
        val customSwitchAdjust = binding.myCustomSwitch
        customSwitchAdjust.trackTintList = null // Desativa o trackTint
        customSwitchAdjust.thumbTintList = null
        customSwitchAdjust.setSwitchMinWidth(customSwitchWidth)



        dialogBinding.btnReturn.setOnClickListener{
            Log.d("DEBUG", "btnReturn clicado!")
            dialog.dismiss()
        }
        dialogBinding.changeButton.setOnClickListener{
            pickImageFromGallery()
            Toast.makeText(this, "LOL", Toast.LENGTH_SHORT).show()
        }

    }

    private fun loadSavedImage(context: Context, imageView: ImageView) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val imagePath = sharedPreferences.getString("profile_image_path", null)

        if (imagePath != null) {
            val file = File(imagePath)
            if (file.exists()) {
                imageView.setImageURI(Uri.parse(imagePath))
            }
        }
    }

//    @Composable
//    private fun CustomSwitch() {
//        var checked by remember { mutableStateOf(true) }
//
//        Box(
//            contentAlignment = Alignment.CenterStart, // Alinha o switch dentro da caixa
//            modifier = Modifier
//                .fillMaxWidth() // Ocupar toda a largura disponível
//                .height(30.dp) // Altura do track
//                .background(if (checked) Color.Green else Color.Gray, shape = RoundedCornerShape(15.dp)) // Fundo do track
//                .padding(horizontal = 8.dp) // Espaço interno para o switch
//        ) {
//            Switch(
//                checked = checked,
//                onCheckedChange = { checked = it },
//                colors = SwitchDefaults.colors(
//                    uncheckedTrackColor = Color.Transparent, // Remove o track padrão
//                    checkedTrackColor = Color.Transparent
//                ),
//                modifier = Modifier.scale(1.2f) // Ajusta apenas o switch sem alterar o track
//            )
//        }
//    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

}