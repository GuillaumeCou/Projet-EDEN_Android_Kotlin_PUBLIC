package projeteden.kotlin.edenapp

//import android.app.ProgressDialog
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import projeteden.kotlin.edenapp.models.Utilisateur
import projeteden.kotlin.edenapp.plusActivities.PlusMenuActivity
import projeteden.kotlin.edenapp.utilities.*


class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var updateSwitch: Switch
    private lateinit var changePhotoButton: Button
    private lateinit var signOutButton: Button
    private lateinit var deleteProfileButton: Button
    private lateinit var updateProfileInFirebaseButton: Button

    private lateinit var userSurname: EditText
    private lateinit var userFirstname: EditText
    private lateinit var userEmail: EditText
    private lateinit var userPhoto: ImageView

    private lateinit var emailChecked: CheckBox
    private lateinit var cardidChecked: CheckBox
    private lateinit var telephoneChecked: CheckBox
    private lateinit var photoChecked: CheckBox

    private lateinit var navbar: BottomNavigationView

    private lateinit var uriImageSelected: Uri
    private lateinit var uid: String

    protected lateinit var progressBar: ProgressBar

    private lateinit var mFunctions: FirebaseFunctions

    //region ------ ACTIVITY LIFE ------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideVirtualKeyboard()

        if (getCurrentUser() != null) {
            setupContentView()
            setupContentState()
            setupNavigationBar()
            uid = getCurrentUserUID()
            mFunctions = FirebaseFunctions.getInstance()

            updateUIWhenCreating()
        } else
            finish()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile_enable_changes_switch -> enableUpdateButtons()
            R.id.profile_update_button -> updateUserData()
            R.id.profile_change_photo -> changeUserPhotoProfile()
            R.id.profile_sign_out_button -> signOut()
            R.id.profile_delete_profile_button -> deleteUser()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_CHOOSE_PHOTO -> if (data != null && resultCode == RESULT_OK) displayPhoto(data)
        }
    }

    private fun successListener(origin: Int): OnSuccessListener<Void> {
        return OnSuccessListener {
            when (origin) {
                RC_DELETE_USER -> Log.d(LOG_DELETE_USER, "User deleted from Firebase")
                RC_SIGN_OUT -> {
                    Log.i(LOG_SIGNOUT_USER, "User is signed out")
                    finish()
                }
                else -> {
                }
            }
        }
    }

    private fun setupContentView() {
        setContentView(R.layout.activity_profile)

        updateSwitch = findViewById(R.id.profile_enable_changes_switch)
        updateSwitch.setOnClickListener(this)
        changePhotoButton = findViewById(R.id.profile_change_photo)
        changePhotoButton.setOnClickListener(this)
        signOutButton = findViewById(R.id.profile_sign_out_button)
        signOutButton.setOnClickListener(this)
        deleteProfileButton = findViewById(R.id.profile_delete_profile_button)
        deleteProfileButton.setOnClickListener(this)
        updateProfileInFirebaseButton = findViewById(R.id.profile_update_button)
        updateProfileInFirebaseButton.setOnClickListener(this)

        userSurname = findViewById(R.id.profile_surname_text)
        userFirstname = findViewById(R.id.profile_firstname_text)
        userEmail = findViewById(R.id.profile_email_text)
        //On ajoute le bouton de validation à tous les EditText
        addDoneButton()
        userPhoto = findViewById(R.id.profile_user_photo)

        progressBar = findViewById(R.id.progressBarProfile)

        emailChecked = findViewById(R.id.profile_email_checkbox)
        cardidChecked = findViewById(R.id.profile_carteid_checkbox)
        telephoneChecked = findViewById(R.id.profile_telephone_checkbox)
        photoChecked = findViewById(R.id.profile_photo_checkbox)

        navbar = findViewById(R.id.bottom_navigation_bar)

    }

    private fun setupContentState() {
        userSurname.isEnabled = false
        userFirstname.isEnabled = false
        userEmail.isEnabled = false
        emailChecked.isEnabled = false
        cardidChecked.isEnabled = false
        telephoneChecked.isEnabled = false
        photoChecked.isEnabled = false
        updateSwitch.isChecked = false
        updateProfileInFirebaseButton.isEnabled = false
        updateProfileInFirebaseButton.visibility = View.INVISIBLE
    }

    private fun setupNavigationBar() {
        navbar.selectedItemId = R.id.navbar_profile
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navbar_map -> {
                    featureUnavailableToast(this)
                    //TODO: Rediriger vers la map
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navbar_alert -> {
                    Log.i("NAVBAR_ALERT", "Go to Main")
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navbar_other -> {
                    startActivity(Intent(this, PlusMenuActivity::class.java))
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        navbar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun deleteUser() {
        // Affichage d'une bar de chargement
        progressBar.visibility = View.VISIBLE

        Thread(Runnable {
            AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@ProfileActivity, "Compte supprimé !",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Veuillez vous déconnecter et vous reconnecter avant de réaliser cette action",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }).start()
    }

    private fun signOut() {
        Thread(Runnable {
            AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.successListener(RC_SIGN_OUT))
        }).start()
    }

    //region ------ PHOTO -----
    private fun changeUserPhotoProfile() {
        if (ContextCompat.checkSelfPermission(this, EXT_STORAGE_PERM) != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestPermissions(this)
            changeUserPhotoProfile()
        } else
            startActivityForResult(
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                RC_CHOOSE_PHOTO
            )
    }

    private fun displayPhoto(data: Intent) {

        if (data.data != null) {
            uriImageSelected = data.data as Uri

            uploadImage(uriImageSelected, uid, FIREBASE_STORAGE_PROFILE_PHOTO)

            Glide.with(this) //SHOWING PREVIEW OF IMAGE
                .load(this.uriImageSelected)
                .apply(RequestOptions.circleCropTransform())
                .into(userPhoto)
        } else {
            Toast.makeText(this, getString(R.string.toast_title_no_image_found), Toast.LENGTH_SHORT).show()
        }
    }

    //endregion

    //region ------ UPDATE ------
    private fun enableUpdateButtons() {
        updateProfileInFirebaseButton.visibility = View.VISIBLE
        updateProfileInFirebaseButton.isEnabled = true
        userEmail.isEnabled = true
        userFirstname.isEnabled = true
        userSurname.isEnabled = true
    }

    private fun disableUpdateButton() {
        updateProfileInFirebaseButton.visibility = View.INVISIBLE
        updateProfileInFirebaseButton.isEnabled = false
        userEmail.isEnabled = false
        userFirstname.isEnabled = false
        userSurname.isEnabled = false
    }

    private fun setUpdateSwitchOff() {
        updateSwitch.isChecked = false
    }

    private fun updateUserData() {
        updateNamesAndEmail(
            uid,
            userSurname.text.toString(),
            userFirstname.text.toString(),
            userEmail.text.toString()
        )

        disableUpdateButton()
        setUpdateSwitchOff()
    }

    //endregion

    // Fonction de mise à jour de l'affichage des données utilisateur
    private fun updateUIWhenCreating() {

        if (getCurrentUser() != null) {
            // Récupération des données utilisateurs sur Firestore
            getUser(getCurrentUserUID())
                .addOnSuccessListener { documentSnapshot ->
                    val user: Utilisateur? = documentSnapshot.toObject(Utilisateur::class.java)
                    if (user != null) {
                        userEmail.setText(user.email)
                        userSurname.setText(user.surname)
                        userFirstname.setText(user.firstname)
                        emailChecked.isChecked = user.isEmailChecked
                        cardidChecked.isChecked = user.isIDCardChecked
                        telephoneChecked.isChecked = user.isTelephoneChecked
                        photoChecked.isChecked = user.isPhotoChecked
                    }
                }

            getImageProfileStorageReference(uid).child(uid).downloadUrl
                .addOnSuccessListener { uri ->
                    uriImageSelected = uri
                    Glide.with(this@ProfileActivity)
                        .load(uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(userPhoto)
                }
                .addOnFailureListener {
                    if (FirebaseAuth.getInstance() != null)
                        if (getCurrentUser()!!.photoUrl != null) {
                            Glide.with(this@ProfileActivity)
                                .load(getCurrentUser()!!.photoUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .into(userPhoto)
                        }
                }
        }
    }

    private fun hideVirtualKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun addDoneButton() {
        val listEditText = ArrayList<EditText>()
        listEditText.add(userEmail)
        listEditText.add(userFirstname)
        listEditText.add(userSurname)

        for (editText in listEditText) {
            editText.setHorizontallyScrolling(false)
            editText.maxLines = 1
        }
    }
}
