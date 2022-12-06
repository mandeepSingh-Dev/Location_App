package com.example.location_app

import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity



open class BaseActivity : AppCompatActivity() {

    var locationPermission: Boolean = false

    fun  request_Multiple_Permission(permission : Array<String>, isGrantedScoped : (isGrantedScoped:Boolean)->Unit)  {

        var isGranted = false

        val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback {
               it.forEach {
                   Toast.makeText(this, it.key+"__"+it.value.toString(), Toast.LENGTH_SHORT).show()

               }
            })

        launcher.launch(permission).let {
            locationPermission = isGranted
        }

    }

}