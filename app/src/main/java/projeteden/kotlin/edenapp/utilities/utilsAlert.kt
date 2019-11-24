package projeteden.kotlin.edenapp.utilities

import android.location.Location
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import projeteden.kotlin.edenapp.TAG_ALERT
import projeteden.kotlin.edenapp.lastAlertAID
import projeteden.kotlin.edenapp.models.Alerte
import java.sql.Timestamp


const val FIREBASE_ALERT_COLLECTION = "alertes"
const val FIRESTORE_ALERTE_UID = "userUid"
const val FIRESTORE_ALERTE_AID = "alertAid"
const val FIRESTORE_ALERTE_TIME = "alertTime"
const val FIRESTORE_ALERTE_ISDELIVERED = "delivered"
const val FIRESTORE_ALERTE_GEOPOINT = "geopoint"

const val IS_SENDER_BOOLEAN = "isSender"

// --- COLLECTION REFERENCE ---

fun getAlertCollection(): CollectionReference {
    return FirebaseFirestore.getInstance().collection(FIREBASE_ALERT_COLLECTION)
}

fun getAlertDocumentReference(aid: String): DocumentReference{
    return getAlertCollection().document(aid)
}

// --- CREATE ---

fun createAlerteMinimal(
    aid: String,
    uid: String,
    time: com.google.firebase.Timestamp,
    geopoint: GeoPoint,
    isDelivered: Boolean?
): Task<Void> {
    updateUserLocation(uid, geopoint)

    val alert = Alerte(aid, uid, time, geopoint, isDelivered!!)
    return getAlertDocumentReference(aid).set(alert)
}

// --- GET ---

fun getAlerte(aid: String): Task<DocumentSnapshot> {
    return getAlertDocumentReference(aid).get()
}

// --- UPDATE ---

fun updateAlertIsDelivered(isDelivered: Boolean, aid: String): Task<Void> {
    return getAlertDocumentReference(aid).update(FIRESTORE_ALERTE_ISDELIVERED, isDelivered)
}

// --- DELETE ---

fun deleteAlerte(aid: String): Task<Void> {
    return getAlertDocumentReference(aid).delete()
}