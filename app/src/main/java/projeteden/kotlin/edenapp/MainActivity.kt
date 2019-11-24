package projeteden.kotlin.edenapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import projeteden.kotlin.edenapp.inscriptionSequence.FirebaseSignInActivity
import projeteden.kotlin.edenapp.models.Alerte
import projeteden.kotlin.edenapp.plusActivities.PlusMenuActivity
import projeteden.kotlin.edenapp.services.LocationService
import projeteden.kotlin.edenapp.services.NotificationPushService
import projeteden.kotlin.edenapp.utilities.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    //Layout and navigation
    private lateinit var image122: ImageView
    private lateinit var imageAlert: ImageView
    private lateinit var navbar: BottomNavigationView


    private lateinit var locationCallbackAlertSended: LocationCallback

    private lateinit var context: Activity

    private var listenerRegistration: ListenerRegistration? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Layout and navigation
        image122 = findViewById(R.id.main_112_call)
        image122.setOnClickListener(this)

        imageAlert = findViewById(R.id.main_alert)
        imageAlert.setOnClickListener(this)

        context = this

        setupNavigationBar()

        //Check the permissions for data, call and location. If they are not enable, ask to.
        checkAndRequestPermissions(this)
    }

    override fun onStart() {
        super.onStart()

        startServices()

        locationCallbackAlertSended = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                getMyLocation(context, this@MainActivity, locationCallbackAlertSended, ::sendCommunityAlert)

                //Launch the service which update user location in the DB
                val refreshLocation = Intent(this@MainActivity, LocationService::class.java)
                this@MainActivity.startService(refreshLocation)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkConnectionFireAuth()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.main_112_call -> callRescueFunction()
            R.id.main_alert -> prepareGpsforAlert()
        }
    }

    //Appui sur la touche retour = fermer l'appli
    override fun onBackPressed() {
        //Pas de "super:onBackPressed" car nous redifinissions entièrement son fonctionnement

        /*On supprime le callback listerner qui vérifie
        les nouvelles alerte pour que le service NotificationPush soit seul*/
        listenerRegistration!!.remove()
        finish()
    }

    //L'activité n'est plus à l'écran
    override fun onPause() {
        super.onPause()
        /*On supprime le callback listerner qui vérifie
        les nouvelles alerte pour que le service NotificationPush soit seul*/
        if (listenerRegistration != null)
            listenerRegistration!!.remove()
    }

    private fun setupNavigationBar() {
        navbar = findViewById(R.id.bottom_navigation_bar)
        navbar.selectedItemId = R.id.navbar_alert

        navbar.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navbar_map -> {
                    featureUnavailableToast(this)
                    //TODO("Lancer la carte dans l'appli")
                }
                R.id.navbar_profile -> startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                R.id.navbar_other -> startActivity(Intent(this@MainActivity, PlusMenuActivity::class.java))
                else -> {
                }
            }
            true
        }
    }

    private fun startServices() {
        val listServices = ArrayList<Class<*>>()
        listServices.add(LocationService::class.java)
        listServices.add(NotificationPushService::class.java)

        for (service in listServices) {
            this@MainActivity.startService(Intent(this@MainActivity, service))
        }
    }

    //Check if the user is connected
    private fun checkConnectionFireAuth() {
        val mFirebaseUser = FirebaseAuth.getInstance().currentUser
        if (mFirebaseUser == null) {
            //If the user is not connected, he or she have to sign if first
            startActivity(Intent(this, FirebaseSignInActivity::class.java))
            finish()
        } else {
            //If the user is connected, start the function which detect new alerts and save the user uid
            initCheckNewAlertOnFirestore()
            currentUserUID = FirebaseAuth.getInstance().currentUser!!.uid
        }
    }

    //Double confirmation for the alert depending on the current state of the GPS
    private fun prepareGpsforAlert() {
        //If the GPS is already on, we show a dialog to be sure the user wants to send an alert
        if (isGpsEnable(this)) {
            alertDialogMessage()
        } else {
            getMyLocation(this, this, locationCallbackAlertSended, ::sendCommunityAlert)
        }
    }

    private fun sendCommunityAlert(geopoint: GeoPoint) {
        val uid = currentUserUID
        val time = nowTimestamp()
        val dateFormat = DateFormat.format(TIME_FORMAT, time.toDate())
        val aid = "$dateFormat $uid"
        val isDeliverd = false
        if (uid != null && uid != "") createAlerteMinimal(aid, uid, time, geopoint, isDeliverd)
        lastAlertAID = aid
        val alertSended = Intent(this, AlertSendedActivity::class.java)
        startActivity(alertSended)
    }

    private fun callRescueFunction() {
        image122.setOnClickListener {
            val number = "tel:" + numTel_112.random()
            val mIntent = Intent(Intent.ACTION_CALL)
            mIntent.data = Uri.parse(number)
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    1
                )

            } else {
                //You already have permission
                try {
                    startActivity(mIntent)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun alertDialogMessage() {
        //Double check when the user wants to send a alert to avoid missclick
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Confirmation")
            .setMessage("Etes-vous sûr de vouloir lancer une alerte ?")
            //If yes, the alert is send
            .setPositiveButton(
                "OUI"
            ) { _, _ -> getMyLocation(this, this, locationCallbackAlertSended, ::sendCommunityAlert) }
            //If no, the alert is canceled
            .setNegativeButton("NON") { _, _ -> }
            .create()
            .show()
    }

    private fun initCheckNewAlertOnFirestore() {
        //récupération de la dernière alerte émise
        val db = FirebaseFirestore.getInstance()
        listenerRegistration = db.collection(FIREBASE_ALERT_COLLECTION)
            .whereEqualTo(FIRESTORE_ALERTE_ISDELIVERED, false)
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                if (e != null) {
                    return@EventListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d(TAG_ALERT, "New alerte")
                            val alert = dc.document.toObject(Alerte::class.java)
                            //If the new alert is not null and its uid is different than the current user's uid then the user receive the alert
                            if (!alert.aid.equals("", ignoreCase = false)) {
                                if (!alert.uid.equals(currentUserUID, ignoreCase = true)) {
                                    val receptionAlerte = Intent(this@MainActivity, AlertReceptionActivity::class.java)
                                    receptionAlerte.putExtra(FIRESTORE_ALERTE_AID, alert.aid)
                                    startActivity(receptionAlerte)
                                }
                            }
                        }
                        else -> {
                        }
                    }
                }
            })
    }
}
