package projeteden.kotlin.edenapp

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import projeteden.kotlin.edenapp.utilities.*


class CommentActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var ValiderButton: Button
    private lateinit var Commentaire: EditText
    private lateinit var YesButton: RadioButton
    private lateinit var NoButton: RadioButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        ValiderButton = findViewById(R.id.comment_validate_button)
        ValiderButton.setOnClickListener(this)
        YesButton = findViewById(R.id.comment_radio_yes)
        NoButton = findViewById(R.id.comment_radio_no)

        Commentaire = findViewById(R.id.comment_comment_text)
        Commentaire.setHorizontallyScrolling(false)
        //Nombre de ligne maximum pour le commentaire
        Commentaire.maxLines = 5

        Commentaire.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.comment_validate_button -> validateComment()
        }
    }

    override fun onBackPressed() {
        validateComment()
    }

    private fun validateComment() {
        val isAllRight = YesButton.isChecked

        val Comment = Commentaire.text.toString()
        if (YesButton.isChecked || NoButton.isChecked) {
            updateInterv(Comment, isAllRight)
            finish()
        } else {
            val builder = AlertDialog.Builder(this)//Si aucun des boutons Oui et Non n'est coché alors cela déclenche un pop up
            builder.setTitle(R.string.warning_title)
                .setMessage(R.string.warning_select_at_least_one_choice)
                .create()
                .show()
        }
    }

    private fun updateInterv(comment: String, isAllRight: Boolean) {
        val aid: String
        if(intent != null){
            aid = if(intent.hasExtra(FIRESTORE_ALERTE_AID)) intent.getStringExtra(FIRESTORE_ALERTE_AID) else AID_isEMPTY
            if (intent.hasExtra(IS_SENDER_BOOLEAN)){
                updateCommentSender(comment, aid)
                updateIsAllRight(isAllRight, aid)
            }
            else
                updateCommentHelper(comment, aid)
        }
    }


    private fun hideKeyboard(v: View?) {
         val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v!!.windowToken, 0)
    }
}
