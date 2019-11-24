package projeteden.kotlin.edenapp.inscriptionSequence

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import projeteden.kotlin.edenapp.*
import projeteden.kotlin.edenapp.utilities.FIREBASE_STORAGE_PROFILE_PHOTO
import projeteden.kotlin.edenapp.utilities.getCurrentUserUID
import projeteden.kotlin.edenapp.utilities.uploadImage


class UploadPhotoActivity : AppCompatActivity() {

    lateinit var UserPhoto: ImageView
    lateinit var SelectPhoto: Button
    lateinit var NextStep: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_photo)

        UserPhoto = findViewById(R.id.uploadphoto_image)
        SelectPhoto = findViewById(R.id.uploadphoto_selectphoto)
        NextStep = findViewById(R.id.uploadphoto_nextstep)

        SelectPhoto.setOnClickListener { chooseImageFromPhone() }

        NextStep.isEnabled = false

        NextStep.setOnClickListener {
            startActivity(Intent(this@UploadPhotoActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null)
            loadPhoto(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        // Ne rien faire
    }

    private fun chooseImageFromPhone() {
        if (ContextCompat.checkSelfPermission(this, EXT_STORAGE_PERM) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(EXT_STORAGE_PERM), RC_EXT_STORAGE_PERMISSION)
        else{
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, RC_CHOOSE_PHOTO)
        }
    }

    private fun loadPhoto(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == Activity.RESULT_OK && data.data != null) { //SUCCESS
                NextStep.isEnabled = true
                uploadImage(data.data!!, getCurrentUserUID(), FIREBASE_STORAGE_PROFILE_PHOTO)

                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                    .load(data.data)
                    .apply(RequestOptions.circleCropTransform())
                    .into(UserPhoto)
            } else {
                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
