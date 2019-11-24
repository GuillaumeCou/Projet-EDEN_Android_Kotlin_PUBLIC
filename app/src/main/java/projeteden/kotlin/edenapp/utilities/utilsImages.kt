package projeteden.kotlin.edenapp.utilities

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask


const val FIREBASE_STORAGE_PROFILE_PHOTO = "profile_photo"
const val FIREBASE_STORAGE_IDCARD_PHOTO = "idcard_photo"

fun getStorageInstance(): FirebaseStorage {
    return FirebaseStorage.getInstance()
}

fun getStorageReference(): StorageReference {
    return getStorageInstance().reference
}

fun getImageStorageReference(): StorageReference {
    return getStorageReference().child("images")
}

fun getImageProfileStorageReference(uid: String): StorageReference {
    return getImageStorageReference().child(uid).child(FIREBASE_STORAGE_PROFILE_PHOTO)
}

fun getImageIDCardStorageReference(uid: String): StorageReference {
    return getImageStorageReference().child(uid).child(FIREBASE_STORAGE_IDCARD_PHOTO)
}

fun uploadImage(image: Uri, uid: String, type: String): UploadTask? {
    return if (type.equals(FIREBASE_STORAGE_IDCARD_PHOTO, ignoreCase = true)
        || type.equals(FIREBASE_STORAGE_PROFILE_PHOTO, ignoreCase = true)) {
        getImageStorageReference().child(uid).child(type).child(uid).putFile(image)
    } else
        null
}

/*TODO: Arranger cette fonction pour qu'elle soit plus élégante
    Par exemple dans le cas où le fichier/référence n'existe pas cela retourne une erreur et
    il faut la traiter correctement !!
     */
fun deleteImageUserFolders(uid: String): Task<Void> {
    return getImageStorageReference().child(uid).delete()
}