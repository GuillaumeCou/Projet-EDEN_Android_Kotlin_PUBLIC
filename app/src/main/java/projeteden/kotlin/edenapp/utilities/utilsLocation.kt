package projeteden.kotlin.edenapp.utilities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import projeteden.kotlin.edenapp.models.UserLocation
import android.location.LocationManager
import android.util.Log


const val FIREBASE_LOCATION_COLLECTION = "location"
const val FIREBASE_GEOPOINT = "geopoint"
const val FIREBASE_TIMESTAMP = "timestamp"
const val REQUEST_CHECK_SETTINGS_GPS = 2019

val locationRequestHighPriority = LocationRequest.create().apply {
    interval = 5000
    fastestInterval = 2000
    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
}
val locationRequestLowPriority = LocationRequest.create().apply {
    interval = 15000
    fastestInterval = 6000
    priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
}



fun getLocationCollection(): CollectionReference {
    return FirebaseFirestore.getInstance().collection(FIREBASE_LOCATION_COLLECTION)
}

fun getLocationDocumentRef(uid: String): DocumentReference {
    return getLocationCollection().document(uid)
}

fun askEnableGPS(activity: Activity) {

    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequestHighPriority)
    val client: SettingsClient = LocationServices.getSettingsClient(activity)
    val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            // Location settings are not satisfied, but this can be fixed
            // by showing the user a dialog.
            try {
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                exception.startResolutionForResult(
                    activity,
                    REQUEST_CHECK_SETTINGS_GPS
                )
            } catch (sendEx: IntentSender.SendIntentException) {
                // Ignore the error.
            }
        }
    }
}


// --- CREATE ---

 fun createUserLocation(uid: String, geo_point: GeoPoint?, timestamp: com.google.firebase.Timestamp?): Task<Void> {
    val userLocation = UserLocation(uid, geo_point, timestamp)
    return getLocationDocumentRef(uid).set(userLocation)
}

// --- GET ---

fun getUserLocation(uid: String): Task<DocumentSnapshot> {
    return getLocationDocumentRef(uid).get()
}

fun isGpsEnable(context: Context): Boolean {

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun getMyLocation(context: Context,activity: Activity, locationCallback: LocationCallback, nextFun: (geopoint: GeoPoint) -> Unit) {

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val permissionLocationFINE =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val permissionLocationCOARSE =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    if (permissionLocationFINE == PackageManager.PERMISSION_GRANTED || permissionLocationCOARSE == PackageManager.PERMISSION_GRANTED) {


        if (isGpsEnable(context)) {
            //If the gps is already enable, we ask for the last kwown location
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    val myGeopoint = GeoPoint(location.latitude, location.longitude)
                    nextFun(myGeopoint)
                }
            }
            // If the gps was not already enable, we ask to and we get the location of the device
        } else {
            askEnableGPS(activity)
            fusedLocationClient.requestLocationUpdates(locationRequestHighPriority, locationCallback, null)
        }
    }
}


// --- UPDATE ---

fun updateUserLocation(uid: String, geo_point : GeoPoint) {

    val loca = getLocationDocumentRef(uid)

    loca
        .update(FIREBASE_GEOPOINT, geo_point)
        .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    loca
        .update(FIREBASE_TIMESTAMP, nowTimestamp())
        .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
}


// --- DELETE ---

fun deleteUserLocation(uid: String): Task<Void> {
    return getLocationDocumentRef(uid).delete()
}