package projeteden.kotlin.edenapp.plusActivities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import projeteden.kotlin.edenapp.R


class PairingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pairing)
        val buttonAppairage: Button = findViewById(R.id.pairing_button)

        buttonAppairage.setOnClickListener {
            val uri = Uri.parse("http://projet-eden.strikingly.com")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
