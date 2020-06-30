package com.route.notesapplicationc32.Base

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


/**
 * Created by Mohamed Nabil Mohamed on 6/10/2020.
 * m.nabil.fci2015@gmail.com
 */
open class BaseActivity : AppCompatActivity() {

    lateinit var activity: BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
    }

    fun showMessage(
        title: String?, message: String?,
        posActionString: String? = null,
        posAction: DialogInterface.OnClickListener? = null,
        negActionString: String? = null,
        negAction: DialogInterface.OnClickListener? = null,
        isCancelable: Boolean = true
    ) {

        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(posActionString, posAction)
            .setNegativeButton(negActionString, negAction)
            .setCancelable(isCancelable)
        builder.show()
    }

    fun showMessage(
        title: Int? = null, message: Int? = null,
        posActionString: Int? = null,
        posAction: DialogInterface.OnClickListener? = null,
        negActionString: Int? = null,
        negAction: DialogInterface.OnClickListener? = null,
        isCancelable: Boolean = true
    ) {

        val builder = AlertDialog.Builder(this)
        if (title != null)
            builder.setTitle(title)
        if (message != null)
            builder.setMessage(message)
        if (posActionString != null)
            builder.setPositiveButton(posActionString, posAction)
        if (negActionString != null)
            builder.setNegativeButton(negActionString, negAction)

        builder.setCancelable(isCancelable)

        builder.show()
    }

    var progressDialog: ProgressDialog? = null
    fun showLoadingDialog(message: String?): ProgressDialog {
        val dialog = ProgressDialog(this)
        progressDialog = dialog
        dialog.setMessage(message)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }

    fun hideLoadingDialge() {
        if (progressDialog?.isShowing == true)
            progressDialog?.dismiss()
    }

     fun isPermissionGranted(permission:String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun requestPermissionFromUser(permissions: Array<out String>,permissionRequestCode:Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                permissions, permissionRequestCode)
        }
    }

    private val permissionsCallBackMap = HashMap<Int,OnRequestPermissionResponseListener>()
     fun requestPermissionFromUser(
        permissionArray: Array<String>,
        permissionRequestCode:Int,
        message:String,
        posActionString:String,
        onRequestPermissionResponseListener: OnRequestPermissionResponseListener
    ) {
        //ask user to give us location
         permissionsCallBackMap.put(permissionRequestCode,onRequestPermissionResponseListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                // we should show dialoge to explain to user
                showMessage(null,
                    message =message ,
                    posAction = DialogInterface.OnClickListener { dialog, which ->
                        requestPermissions(permissionArray,
                            permissionRequestCode)
                    },posActionString = posActionString)

            }else
                requestPermissions(permissionArray,
                    permissionRequestCode)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            val result = permissionsCallBackMap.get(requestCode);
            if (result==null){
                return
            }else
            if ((grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                result.onRequestPermissionResponse(requestCode,PERMISSION_GRANTED);
            } else {
                result.onRequestPermissionResponse(requestCode,PERMISSION_DENID)
            }
    }


    val PERMISSION_GRANTED:Int = 0
    val PERMISSION_DENID:Int = 1

    interface OnRequestPermissionResponseListener{
        fun onRequestPermissionResponse(requestCode:Int,State:Int);
    }

}