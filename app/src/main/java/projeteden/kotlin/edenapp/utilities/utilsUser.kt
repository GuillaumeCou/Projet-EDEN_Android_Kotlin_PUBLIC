package projeteden.kotlin.edenapp.utilities

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import projeteden.kotlin.edenapp.LOG_UPDATE_UI
import projeteden.kotlin.edenapp.models.Utilisateur


//region ------ FIRESTORE CONSTANTS ------

const val FIRESTORE_USER_COLLECTION = "utilisateurs"
const val FIRESTORE_UTILISATEUR_PRENOM = "firstname"
const val FIRESTORE_UTILISATEUR_NOM = "surname"
const val FIRESTORE_UTILISATEUR_EMAIL = "email"
const val FIRESTORE_UTILISTATEUR_TELEPHONE = "telephone"
const val FIRESTORE_UTILISATEUR_CGU_VALID = "cguChecked"
const val FIRESTORE_UTILISATEUR_IDCARD_VALID = "carteIDChecked"
const val FIRESTORE_UTILISATEUR_PHOTO_VALID = "photoChecked"
const val FIRESTORE_UTILISATEUR_EMAIL_VALID = "emailChecked"
const val FIRESTORE_UTILISATEUR_TELEPHONE_VALID = "telephoneChecked"

//endregion


// --- CONSTRUCTORS ---

fun  createUserAll(
    uid: String, connection: String, surname: String, firstname: String,
    email: String, urlCarteID: String?, urlPhoto: String?, telephone: Long
): Task<Void> {
    val user = Utilisateur( connection, surname, firstname, email, telephone, urlPhoto,urlCarteID )
    return getUserDocumentReference(uid).set(user)
}

fun  createUser(uid: String, currentProvider: String): Task<Void> {
    val user = Utilisateur(provider = currentProvider)
    return getUserDocumentReference(uid).set(user)
}


// --- GET ---

fun  getUsersCollection(): CollectionReference {
    return FirebaseFirestore.getInstance().collection(FIRESTORE_USER_COLLECTION)
}

fun  getUserDocumentReference(uid: String): DocumentReference{
    return getUsersCollection().document(uid)
}

fun  getUser(uid: String): Task<DocumentSnapshot> {
    return getUserDocumentReference(uid).get()
}

fun getFirebaseInstance(): FirebaseAuth{
    return FirebaseAuth.getInstance()
}

fun getCurrentUser(): FirebaseUser?{
    return getFirebaseInstance().currentUser
}

fun getCurrentUserUID(): String {
    return getCurrentUser()!!.uid
}

// --- UPDATES ---

fun  updateNamesAndEmail(uid: String, surname: String, firstname: String, email: String): Task<Void> {
    val batch = FirebaseFirestore.getInstance().batch()
    val documentRef = getUserDocumentReference(uid)

    batch.update(
        documentRef,
        FIRESTORE_UTILISATEUR_PRENOM,
        firstname
    )

    batch.update(
        documentRef,
        FIRESTORE_UTILISATEUR_NOM,
        surname
    )

    batch.update(
        documentRef,
        FIRESTORE_UTILISATEUR_EMAIL,
        email
    )

    return batch.commit()
        .addOnSuccessListener { Log.d(LOG_UPDATE_UI, projeteden.kotlin.edenapp.R.string.log_updateUI_success.toString()) }
        .addOnFailureListener { e -> Log.w(LOG_UPDATE_UI, projeteden.kotlin.edenapp.R.string.log_updateUI_fail.toString(), e) }
}

fun  updateNames(uid: String, surname: String, firstname: String): Task<Void> {
    val batch = FirebaseFirestore.getInstance().batch()
    batch.update(
        getUserDocumentReference(uid),
        FIRESTORE_UTILISATEUR_PRENOM,
        firstname
    )

    batch.update(
        getUserDocumentReference(uid),
        FIRESTORE_UTILISATEUR_NOM,
        surname
    )

    return batch.commit()
        .addOnSuccessListener { Log.d(LOG_UPDATE_UI, projeteden.kotlin.edenapp.R.string.log_updateUI_success.toString()) }
        .addOnFailureListener { e -> Log.w(LOG_UPDATE_UI, projeteden.kotlin.edenapp.R.string.log_updateUI_fail.toString(), e) }
}

fun  updateUserEmail(email: String, uid: String): Task<Void> {
    return getUserDocumentReference(uid).update(FIRESTORE_UTILISATEUR_EMAIL, email)
}

fun  updateUserFirstname(firstname: String, uid: String): Task<Void> {
    return getUserDocumentReference(uid).update(FIRESTORE_UTILISATEUR_PRENOM, firstname)
}

fun  updateUserSurname(surname: String, uid: String): Task<Void> {
    return getUserDocumentReference(uid).update(FIRESTORE_UTILISATEUR_NOM, surname)
}

fun  updateUserTelephone(telephone: Long, uid: String): Task<Void> {
    return getUserDocumentReference(uid).update(FIRESTORE_UTILISTATEUR_TELEPHONE, telephone)
}

fun  setTelephoneNumberChecked(uid: String): Task<Void> {
    return getUserDocumentReference(uid).update(FIRESTORE_UTILISATEUR_TELEPHONE_VALID, true)
}