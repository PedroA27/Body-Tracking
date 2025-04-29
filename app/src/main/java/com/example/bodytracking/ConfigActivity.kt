package com.example.bodytracking

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View.GONE
import android.view.View.VISIBLE
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
    private lateinit var calendarConfigurator: CalendarConfigurator
    lateinit var dialog: Dialog

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

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                if (imageUri != null) {
                    val imagePath = saveImageToInternalStorage(imageUri)
                    if (imagePath != null) {
                        saveImagePathToPreferences(imagePath)
                        dialogBinding.circleUser.setImageURI(Uri.parse(imagePath))
                    }
                }
            }
        }
    //-------------
    private fun saveImagePathToPreferences(imagePath: String) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit().putString("profile_image_path", imagePath).apply()
    }

    // SWITCH
    companion object {
        const val PREFS_NAME = "MyAppPrefs"
        const val SWITCH_STATE_KEY = "switch_state"
        private const val BIRTHDAY = "birthday_date"
        const val HEIGHT = "current_height"
        const val USER_NAME = "user_name"
    }
    //-------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        calendarConfigurator = CalendarConfigurator()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
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
        binding.mainCircle.setOnClickListener {
            dialog.show()
            loadSavedImage(dialogBinding.root.context, dialogBinding.circleUser)

        }
        binding.insertDate.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            inputType = android.text.InputType.TYPE_NULL

            setOnClickListener {
                calendarConfigurator.hideKeyboard(this@ConfigActivity,binding)
                Handler(Looper.getMainLooper()).postDelayed({
                    calendarConfigurator.showDatePickerDialog(this@ConfigActivity,binding)
                }, 15)
            }

            setOnTouchListener { v, event ->
                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    v.performClick()
                }
                true
            }
        }

        // SWITCH -----
        fun Int.dpToPx(): Int {
            return (this * resources.displayMetrics.density).toInt()
        }
        val customSwitchWidth = (resources.displayMetrics.widthPixels - resources.displayMetrics.widthPixels*0.42 - 40.dpToPx()).toInt()
        val customSwitchAdjust = binding.myCustomSwitch
        val femaleText = binding.femaleText
        val maleText = binding.maleText

        //Switch Config
        customSwitchAdjust.trackTintList = null
        customSwitchAdjust.thumbTintList = null
        customSwitchAdjust.setSwitchMinWidth(customSwitchWidth)

        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedSwitchState = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false)
        customSwitchAdjust.isChecked = savedSwitchState

        maleText.visibility = if (savedSwitchState) VISIBLE else GONE
        femaleText.visibility = if (savedSwitchState) GONE else VISIBLE
        customSwitchAdjust.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                maleText.visibility = VISIBLE
                femaleText.visibility = GONE
            } else {
                maleText.visibility = GONE
                femaleText.visibility = VISIBLE
            }
            sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY, isChecked).apply()
        }
        // EDITTEXT BIRTHDAY----
        val birthday = binding.insertDate
        val sharedPreferences2: String? = sharedPreferences.getString(BIRTHDAY, null)
        if (sharedPreferences2 != null && sharedPreferences2.trim().length > 0) {
            birthday.setText(sharedPreferences2)
        }
        birthday.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.trim() ?: ""
                sharedPreferences.edit().apply {
                    if (text.isBlank()) {
                        remove(BIRTHDAY)
                    } else {
                        putString(BIRTHDAY, text)
                    }
                }.apply()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        //-------------
        val customHeight = binding.insertHeight
        val sharedPreferences3: String? = sharedPreferences.getString(HEIGHT, null)
        if (sharedPreferences3 != null && sharedPreferences3.trim().length > 0) {
            customHeight.setText(sharedPreferences3)
        }
        customHeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.trim() ?: ""
                sharedPreferences.edit().apply {
                    if (text.isBlank()) {
                        remove(HEIGHT)
                    } else {
                        putString(HEIGHT, text)
                    }
                }.apply()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        //Custom Name________________________
        val customName = binding.nameText
        val sharedPreferences4: String? = sharedPreferences.getString(USER_NAME, null)
        if (sharedPreferences4 != null && sharedPreferences4.trim().length > 0) {
            customName.setText(sharedPreferences4)
        }
        customName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.trim() ?: ""
                sharedPreferences.edit().apply {
                    if (text.isBlank()) {
                        remove(USER_NAME)
                    } else {
                        putString(USER_NAME, text)
                    }
                }.apply()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


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
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val imagePath = sharedPreferences.getString("profile_image_path", null)

        if (imagePath != null) {
            val file = File(imagePath)
            if (file.exists()) {
                imageView.setImageURI(Uri.parse(imagePath))
            }
        }
    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }


}