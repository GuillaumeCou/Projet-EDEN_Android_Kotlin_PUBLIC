package projeteden.kotlin.edenapp.services

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import projeteden.kotlin.edenapp.currentUserUID
import projeteden.kotlin.edenapp.utilities.isGpsEnable
import projeteden.kotlin.edenapp.utilities.locationRequestLowPriority
import projeteden.kotlin.edenapp.utilities.updateUserLocation
import java.util.*


class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallbackServiceLoca: LocationCallback



    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }




    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)




        locationCallbackServiceLoca = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val permissionLocationFINE =
                    ContextCompat.checkSelfPermission(this@LocationService, Manifest.permission.ACCESS_FINE_LOCATION)
                val permissionLocationCOARSE =
                    ContextCompat.checkSelfPermission(this@LocationService, Manifest.permission.ACCESS_COARSE_LOCATION)
                if (permissionLocationFINE == PackageManager.PERMISSION_GRANTED || permissionLocationCOARSE == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
                        val geoPoint = GeoPoint(location.latitude, location.longitude)
                        val uid = currentUserUID
                        if(uid != null) updateUserLocation(uid,geoPoint)
                    }
                }

            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val permissionLocationFINE =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionLocationCOARSE =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissionLocationFINE == PackageManager.PERMISSION_GRANTED || permissionLocationCOARSE == PackageManager.PERMISSION_GRANTED)
        {
            if (isGpsEnable(this))
            {
                fusedLocationClient.requestLocationUpdates(locationRequestLowPriority, locationCallbackServiceLoca, null)

            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        val time = Calendar.getInstance()
        //time.set(Calendar.MINUTE, 15);
        time.set(Calendar.SECOND, 15)
        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.set(
            AlarmManager.RTC,
            time.timeInMillis,
            PendingIntent.getService(this, 0, Intent(this,LocationService::class.java), 0)
        )
    }






}
