package com.example.location_app.Base

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity



open class BaseActivity : AppCompatActivity() {

    var locationPermission: Boolean = false

    fun  request_Multiple_Permission(permission : Array<String>, isGrantedScoped : (isGrantedScoped:Boolean)->Unit)  {

        var isGranted = false

        val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback {
                it.forEach {
                    if(it.key == permission.get(0))  //here permission[0] is ACCESS_FINE_LOCATION
                    {
                        isGrantedScoped(it.value)
                    }
                }
            })

        launcher.launch(permission)

    }

}