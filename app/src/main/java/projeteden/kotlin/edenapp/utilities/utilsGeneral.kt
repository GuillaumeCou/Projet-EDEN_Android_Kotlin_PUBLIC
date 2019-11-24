package projeteden.kotlin.edenapp.utilities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import projeteden.kotlin.edenapp.*


fun checkAndRequestPermissions(activity: Activity): Boolean {
    val listPermissionsNeeded = ArrayList<String>()
    val listPermissionsToRequest = ArrayList<String>()

    listPermissionsNeeded.add(FINE_LOC_PERM)
    listPermissionsNeeded.add(EXT_STORAGE_PERM)
    listPermissionsNeeded.add(CALL_PHONE_PERM)


    for(perm in listPermissionsNeeded){
        if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED)
            listPermissionsToRequest.add(perm)
    }


    if (!listPermissionsToRequest.isEmpty()) {
        ActivityCompat.requestPermissions(activity, listPermissionsToRequest.toTypedArray(), RC_MULTIPLE_PERMISSIONS)
        return false
    }
    return true
}

fun isConnectedInternet(context: Context): Boolean{
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    //TODO("Fonction dépréciée")
    return activeNetwork?.isConnectedOrConnecting == true
}

fun needInternetConnectionMessage(activity: Activity){
    if(isConnectedInternet(activity))
        AlertDialog.Builder(activity)
            .setTitle(R.string.warning_internet_connection_needed_title)
            .setMessage(R.string.warning_internet_connection_needed_message)
            .setCancelable(true)
            .create()
            .show()
}

fun convertToTimestamp(value: Long): Timestamp {
    return Timestamp(value, 0)
}

fun nowTimestamp(): Timestamp{
    return Timestamp.now()
}


fun featureUnavailableToast(context: Activity) {
    Toast.makeText(context, "This feature is currently unavailable", Toast.LENGTH_LONG).show()
}
