package com.example.bodytracking

import android.app.Dialog
import android.content.Intent
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
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bodytracking.databinding.ActivityConfigBinding
import com.example.bodytracking.databinding.CustomImageBoxBinding
import java.io.File

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding
    private lateinit var dialogBinding: CustomImageBoxBinding
    lateinit var dialog: Dialog

    private lateinit var captureIV: ImageView
    private lateinit var imageUri: Uri



    companion object {
        val IMAGE_REQUEST_CODE = 1_000;
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



        binding.circle.setOnClickListener {
            dialog.show()
        }

        val imageView = dialogBinding.circleUser
        dialogBinding.btnReturn.setOnClickListener{
            Log.d("DEBUG", "btnReturn clicado!")
            dialog.dismiss()
        }
        dialogBinding.changeButton.setOnClickListener{
            pickImageFromGallery()
            Toast.makeText(this, "LOL", Toast.LENGTH_SHORT).show()
        }

    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            dialogBinding.circleUser.setImageURI(data?.data)
        }
    }

}