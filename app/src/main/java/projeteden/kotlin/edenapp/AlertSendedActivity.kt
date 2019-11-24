package projeteden.kotlin.edenapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import projeteden.kotlin.edenapp.utilities.FIRESTORE_ALERTE_AID
import projeteden.kotlin.edenapp.utilities.IS_SENDER_BOOLEAN
import projeteden.kotlin.edenapp.utilities.updateAlertIsDelivered


class AlertSendedActivity : AppCompatActivity() {
    private lateinit var securityButton: Button
    private lateinit var commentAlert: Intent
    private lateinit var aid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_sended)

        commentAlert = intent

        securityButton = findViewById(R.id.alertsended_end_alert_button)

        securityButton.setOnClickListener {
            val comment = Intent(this,CommentActivity::class.java)
            comment.putExtra(FIRESTORE_ALERTE_AID, lastAlertAID)
            comment.putExtra(IS_SENDER_BOOLEAN,true)
            startActivity(comment)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (commentAlert.hasExtra(FIRESTORE_ALERTE_AID)) {
            // on récupère la valeur associée à la clé
            aid = commentAlert.getStringExtra(FIRESTORE_ALERTE_AID)
        }
    }

    override fun onBackPressed() {
        //Disable back button
        //super.onBackPressed();
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Voulez-vous vraiment annuler votre alerte ?")
            .setNegativeButton("non") { _, _ -> }
            .setPositiveButton("Oui") { _, _ ->
                updateAlertIsDelivered(true, aid)
                    .addOnCompleteListener {
                        finish()
                    }
            }.create()
            .show()
    }
}
