package projeteden.kotlin.edenapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import projeteden.kotlin.edenapp.*
import projeteden.kotlin.edenapp.models.Alerte
import projeteden.kotlin.edenapp.utilities.FIREBASE_ALERT_COLLECTION
import projeteden.kotlin.edenapp.utilities.FIRESTORE_ALERTE_AID
import projeteden.kotlin.edenapp.utilities.FIRESTORE_ALERTE_ISDELIVERED

class NotificationPushService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private var gNotificationBuilder: NotificationCompat.Builder? = null

    override fun onCreate() {
        super.onCreate()
        gNotificationBuilder = NotificationCompat.Builder(this, NP_CHANNEL_ID_NEW_ALERT)
            .setSmallIcon(R.drawable.ic_search_people)
            .setContentTitle("Alerte reçue !")
            .setContentText("Quelqu'un à besoin de toi. Veux-tu l'aider ?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (FirebaseAuth.getInstance().currentUser != null)
            checkNewAlertOnFirestore()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alerte EDEN"
            val description = "Quelqu'un à besoin de toi. Veux-tu l'aider ?"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NP_CHANNEL_ID_NEW_ALERT, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkNewAlertOnFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection(FIREBASE_ALERT_COLLECTION)
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
                            if (!alert.aid.equals("", ignoreCase = false)) {
                                if (!alert.uid.equals(currentUserUID, ignoreCase = true)) {

                                    val receptionAlerte =
                                        Intent(this@NotificationPushService, AlertReceptionActivity::class.java)
                                    receptionAlerte.putExtra(FIRESTORE_ALERTE_AID, alert.aid)

                                    val pendingIntent =
                                        PendingIntent.getActivity(this@NotificationPushService, 0, receptionAlerte, 0)

                                    gNotificationBuilder!!.setContentIntent(pendingIntent)!!.setAutoCancel(true)

                                    val notificationManager =
                                        NotificationManagerCompat.from(this@NotificationPushService)

                                    notificationManager.notify(NP_ID_NEW_ALERT, gNotificationBuilder!!.build()!!)
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
