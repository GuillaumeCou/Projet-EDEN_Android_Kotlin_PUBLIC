package projeteden.kotlin.edenapp.inscriptionSequence

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import projeteden.kotlin.edenapp.R
import projeteden.kotlin.edenapp.utilities.getCurrentUserUID
import projeteden.kotlin.edenapp.utilities.updateNamesAndEmail


class GetUIActivity : AppCompatActivity() {

    lateinit var Surname: EditText
    lateinit var Firstname: EditText
    lateinit var Email: EditText
    lateinit var SuivantButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_ui)

        setupSetContentView()
        setupSuivantButton()
    }

    override fun onBackPressed() {
    }

    private fun setupSetContentView() {
        setContentView(R.layout.activity_get_ui)

        Surname = findViewById(R.id.getUiAct_surname_edittext)
        Firstname = findViewById(R.id.getUiAct_firstname_edittext)
        Email = findViewById(R.id.getUiAct_email_edittext)
        SuivantButton = findViewById(R.id.getUiAct_nextstep_button)
        addDoneButton()
    }

    private fun setupSuivantButton() {
        SuivantButton.setOnClickListener {
            if (!(Surname.text.isBlank() && Firstname.text.isBlank() && Email.text.isBlank())) {
                updateNamesAndEmail(
                    getCurrentUserUID(),
                    Surname.text.toString(),
                    Firstname.text.toString(),
                    Email.text.toString()
                )

                Toast.makeText(this@GetUIActivity, "Veuillez allez valider votre email", Toast.LENGTH_LONG).show()

                startActivity(Intent(this@GetUIActivity, GetPhoneActivity::class.java))
                finish()
            } else
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addDoneButton()
    {
        val listEditText = ArrayList<EditText>()
        listEditText.add(Surname)
        listEditText.add(Firstname)
        listEditText.add(Email)

        for (editText in listEditText){
            editText.setHorizontallyScrolling(false)
            editText.maxLines = 1
        }
    }
}
