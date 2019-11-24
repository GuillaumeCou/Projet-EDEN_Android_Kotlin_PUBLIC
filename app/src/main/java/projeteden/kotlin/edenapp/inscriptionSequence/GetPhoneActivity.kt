package projeteden.kotlin.edenapp.inscriptionSequence

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import projeteden.kotlin.edenapp.R
import projeteden.kotlin.edenapp.utilities.getCurrentUser
import projeteden.kotlin.edenapp.utilities.getCurrentUserUID
import projeteden.kotlin.edenapp.utilities.setTelephoneNumberChecked
import projeteden.kotlin.edenapp.utilities.updateUserTelephone
import java.util.concurrent.TimeUnit


class GetPhoneActivity : AppCompatActivity() {

    private val TAG = "PHONE_NUMBER_VERIFY"

    protected var Telephone: EditText? = null
    private var Code: EditText? = null
    private var Verifier: Button? = null
    private var Valider: Button? = null

    private var mVerificationId: String? = null

    private var mResendToken: ForceResendingToken? = null
    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_phone)

        Telephone = findViewById(R.id.getPhoneVerif_telephone_edittext)
        Code = findViewById(R.id.getPhoneVerif_codeVerif_edittext)
        Verifier = findViewById(R.id.getPhoneVerif_sendNumber_button)
        Valider = findViewById(R.id.getPhoneVerif_validate_button)

        Valider!!.isEnabled = false
        Code!!.isEnabled = false

        setupCallback()

        Verifier!!.setOnClickListener {
            if (!Telephone.toString().isBlank()) {
                Verifier!!.isEnabled = false
                Telephone!!.isEnabled = false
                Valider!!.isEnabled = true
                Code!!.isEnabled = true

                updateUserTelephone(
                    Telephone!!.text.toString().toLong(),
                    getCurrentUserUID()
                )

                val correctNumber = "+33" + Telephone!!.text.toString().substring(1)
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    correctNumber, // Phone number to verify
                    60, // Timeout duration
                    TimeUnit.SECONDS, // Unit of timeout
                    this, // Activity (for callback binding)
                    mCallbacks!!
                )        // OnVerificationStateChangedCallbacks
            } else
                Toast.makeText(this, "Veuillez renseigner un numéro valide", Toast.LENGTH_SHORT).show()
        }

        Valider!!.setOnClickListener {
            val credential = PhoneAuthProvider.getCredential(mVerificationId!!, Code!!.text.toString())
            linkProvidersAndNextStep(credential)
        }


    }

    override fun onBackPressed() {
        //Ne rien faire !
    }

    private fun setupCallback() {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")

                linkProvidersAndNextStep(credential)
                setTelephoneNumberChecked(getCurrentUserUID())
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                Valider!!.isEnabled = false
                Code!!.isEnabled = false
                Verifier!!.isEnabled = true
                Telephone!!.isEnabled = true

                Toast.makeText(
                    this@GetPhoneActivity,
                    getString(R.string.getphone_erreur),
                    Toast.LENGTH_SHORT
                ).show()

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                verificationId: String?,
                token: ForceResendingToken?
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId!!)

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
            }
        }


    }

    private fun linkProvidersAndNextStep(credential: PhoneAuthCredential) {
        getCurrentUser()?.linkWithCredential(credential)?.addOnSuccessListener {
            Toast.makeText(this, "Code validé", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, UploadPhotoActivity::class.java))
            finish()
        }

    }
}
