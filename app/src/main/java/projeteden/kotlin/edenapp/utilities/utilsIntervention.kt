package projeteden.kotlin.edenapp.utilities

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore



const val FIRESTORE_INTERVENTION_COLLECTION = "interventions"
const val FIRESTORE_ISALLRIGHT = "isAllRight"
const val FIRESTORE_COMMENT_S = "CommentS"
const val FIRESTORE_COMMENT_R = "CommentR"
const val FIRESTORE_UID_S = "uidS"
const val FIRESTORE_UID_R = "uidR"

const val TAG_FIREBASE_COMMENT = "FirebaseCommentaire"

fun getIntervDocRef(aid: String): DocumentReference {
    val db = FirebaseFirestore.getInstance()
    return db.collection(FIRESTORE_INTERVENTION_COLLECTION).document(aid)
}

//Ajoute le commentaire de l'emetteur à la DB
fun updateCommentSender(comment: String, aid: String) {
    val interv = getIntervDocRef(aid)

    interv.update(FIRESTORE_COMMENT_S, comment)
        .addOnSuccessListener { Log.d(TAG_FIREBASE_COMMENT, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(TAG_FIREBASE_COMMENT, "Error updating document", e) }
}

//Ajoute le commentaire du receveur à la DB
fun updateCommentHelper(comment: String, aid: String) {
    val interv = getIntervDocRef(aid)

    interv.update(FIRESTORE_COMMENT_R, comment)
        .addOnSuccessListener { Log.d(TAG_FIREBASE_COMMENT, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(TAG_FIREBASE_COMMENT, "Error updating document", e) }


}

//Modifie ou non le champ IsAllRight dans la DB
fun updateIsAllRight(isAllRight: Boolean, aid: String) {
    val interv = getIntervDocRef(aid)

    interv.update(FIRESTORE_ISALLRIGHT, isAllRight)
        .addOnSuccessListener { Log.d(TAG_FIREBASE_COMMENT, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(TAG_FIREBASE_COMMENT, "Error updating document", e) }
}