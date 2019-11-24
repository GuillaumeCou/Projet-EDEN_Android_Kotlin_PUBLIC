package projeteden.kotlin.edenapp.models

import com.google.firebase.firestore.GeoPoint
import projeteden.kotlin.edenapp.*

data class Utilisateur(
    var provider: String = PROVIDER_isEMPTY,
    var surname: String = SURNAME_isEMPTY,
    var firstname: String = FIRSTNAME_isEMPTY,
    var email: String = EMAIL_isEMPTY,
    var telephone: Long = 0,
    var uriPhoto: String? = URI_PHOTO_isEMPTY,
    var uriIdCard: String? = URI_IDCARD_isEMPTY,
    var isEmailChecked: Boolean = false,
    var isPhotoChecked: Boolean = false,
    var isIDCardChecked: Boolean = false,
    var isTelephoneChecked: Boolean = false
)

data class Alerte(
    var aid: String = AID_isEMPTY,
    var uid: String = UID_isEMPTY,
    var time: com.google.firebase.Timestamp = com.google.firebase.Timestamp(0,0),
    var geopoint: GeoPoint = GeoPoint(0.0, 0.0),
    var isDelivered: Boolean = false
)

data class UserLocation(
    var uid: String = UID_isEMPTY,
    var geopoint: GeoPoint? = GeoPoint(0.0, 0.0),
    var timestamp: com.google.firebase.Timestamp? = com.google.firebase.Timestamp(0,0)
)

data class Intervention(
    var iid: String = IID_isEMPTY,
    var aid: String = AID_isEMPTY,
    var uidR: String = UID_isEMPTY,
    var commentS: String = COMMENT_S_isEMPTY,
    var commentR: String = COMMENT_R_isEMPTY,
    var isAllRight: Boolean = false
)



