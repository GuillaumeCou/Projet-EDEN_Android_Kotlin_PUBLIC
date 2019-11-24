package projeteden.kotlin.edenapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import projeteden.kotlin.edenapp.models.Alerte
import projeteden.kotlin.edenapp.models.UserLocation
import projeteden.kotlin.edenapp.models.Utilisateur
import projeteden.kotlin.edenapp.utilities.*


class AlertReceptionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var infoAlerte: TextView
    private lateinit var photoEmetteur: ImageView
    private lateinit var buttonAccept: Button
    private lateinit var buttonDecline: Button
    private lateinit var uriImageSelected: Uri

    private var uidSender: String = AID_isEMPTY
    private var aidSender: String = UID_isEMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_reception)

        setupViewIds()

        loadAlerteSenderUI()
    }

    private fun setupViewIds() {
        infoAlerte = findViewById(R.id.alert_reception_text)
        photoEmetteur = findViewById(R.id.alert_reception_photo)
        buttonAccept = findViewById(R.id.alert_reception_accept)
        buttonAccept.setOnClickListener(this)
        buttonDecline = findViewById(R.id.alert_reception_decline)
        buttonDecline.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.alert_reception_accept -> acceptationAction()
            R.id.alert_reception_decline -> finish()
        }
    }

    private fun loadAlerteSenderUI() {
        //récupération des données de MainActivity

        aidSender = this.intent.getStringExtra(FIRESTORE_ALERTE_AID)
        updateAlertIsDelivered(true, aidSender)

        if (aidSender != "") {
            getAlerte(aidSender)
                .addOnSuccessListener { documentSnapshot ->
                    val alert = documentSnapshot.toObject(Alerte::class.java)
                    if (alert != null) {
                        uidSender = alert.uid

                        updateTextReception(uidSender)

                        loadProfilePhotoAlertSender(uidSender)
                    } else
                        finish()
                }
        }
    }

    private fun updateTextReception(uidSender: String) {
        getUser(uidSender).addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(Utilisateur::class.java)

            if (user != null) {

                val surname: String = user.surname
                val firstname: String = user.firstname

                val alertText = "$surname $firstname a besoin d'aide ! Veux-tu l'aider ?"

                infoAlerte.text = alertText
            }
        }
    }

    private fun loadProfilePhotoAlertSender(uid: String) {
        getImageProfileStorageReference(uid).child(uid).downloadUrl
            .addOnSuccessListener { uri ->
                uriImageSelected = uri
                Glide.with(this)
                    .load(uriImageSelected)
                    .apply(RequestOptions.circleCropTransform())
                    .into(photoEmetteur)
            }
            .addOnFailureListener { photoEmetteur.isEnabled = false }
    }

    private fun acceptationAction() {
        addUsertoInterv()

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(FIREBASE_LOCATION_COLLECTION).document(uidSender)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val userLocation = documentSnapshot.toObject(UserLocation::class.java)
            if (userLocation != null) {
                // Après avoir accepter, la map se lance et on lance également l'activité commentaire
                val goComment = Intent(this, CommentActivity::class.java)
                goComment.putExtra(IS_SENDER_BOOLEAN, false)
                goComment.putExtra(FIRESTORE_ALERTE_AID, aidSender)
                startActivity(goComment)

                startMapActivity(userLocation.geopoint!!)
            }

            else
                finish()
        }
    }

    private fun startMapActivity(geoPoint: GeoPoint) {
        //Launch google map and the destination is the location of the alert sender
        val latitude = geoPoint.latitude
        val longitude = geoPoint.longitude

        val label = "I'm Here!"
        val uriBegin = "geo:$latitude,$longitude"
        val query = "$latitude, $longitude($label)"
        //String query = "I'm Here!";
        val encodedQuery = Uri.encode(query)
        val uriString = "$uriBegin?q=$encodedQuery&z=16"
        val uri = Uri.parse(uriString)
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(mapIntent)
        finish()
    }

    private fun addUsertoInterv() {

        getIntervDocRef(aidSender).update(FIRESTORE_UID_R, getCurrentUserUID())

        val uidRecep = getCurrentUserUID()

        //Add the user(helper) to the current intervention in the DB
        val intervRef = getIntervDocRef(aidSender)

        intervRef.update(FIRESTORE_UID_R, uidRecep)
            .addOnSuccessListener { Log.d(LOG_ADD_USER_INTERV, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(LOG_ADD_USER_INTERV, "Error updating document", e) }

    }
}
