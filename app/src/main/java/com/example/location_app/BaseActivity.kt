package com.example.location_app

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity



open class BaseActivity : AppCompatActivity() {

    var locationPermission: Boolean = false

    fun  request_Permission(permission : String, isGrantedScoped : (isGrantedScoped:Boolean)->Unit) : Pair<ActivityResultLauncher<String>,Boolean> {

        var isGranted = false

        val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission(),
            ActivityResultCallback {
                isGranted = it
                isGrantedScoped(it)
            })

        launcher.launch(permission).let {
            locationPermission = isGranted
        }

           val pair = Pair<ActivityResultLauncher<String>,Boolean>(launcher,locationPermission)
        return pair

    }

}