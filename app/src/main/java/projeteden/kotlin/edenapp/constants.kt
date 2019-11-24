package projeteden.kotlin.edenapp

import android.Manifest

const val TIME_FORMAT = "yyyy_MM_dd_kk:mm:ss "

//region ------ STRINGS EMPTY ------
const val UID_isEMPTY = "UID is EMPTY"
const val PROVIDER_isEMPTY = "PROVIDER is EMPTY"
const val SURNAME_isEMPTY = "SURNAME is EMPTY"
const val FIRSTNAME_isEMPTY = "FIRSTNAME is EMPTY"
const val EMAIL_isEMPTY = "EMAIL is EMPTY"
const val URI_PHOTO_isEMPTY = "URI for PROFILE_PHOTO is EMPTY"
const val URI_IDCARD_isEMPTY = "URI for ID_CARD is EMPTY"

const val AID_isEMPTY = "AID is EMPTY"
const val IID_isEMPTY = "IID is EMPTY"
const val COMMENT_S_isEMPTY = "COMMENT_SENDER is EMPTY"
const val COMMENT_R_isEMPTY = "COMMENT_RECEIVER is EMPTY"

//endregion


//region ------ PERMISSIONS ------
const val CALL_PHONE_PERM = Manifest.permission.CALL_PHONE
const val FINE_LOC_PERM = Manifest.permission.ACCESS_FINE_LOCATION
const val EXT_STORAGE_PERM = Manifest.permission.READ_EXTERNAL_STORAGE

//endregion


//region ------ REQUEST CODES ------
const val RC_MULTIPLE_PERMISSIONS = 1
const val RC_EXT_STORAGE_PERMISSION = 2
const val RC_DELETE_USER = -100
const val RC_SIGN_OUT = 100
const val RC_CHOOSE_PHOTO = 200

//endregion


//region ------ LOG TEXTS ------
const val LOG_UPDATE_UI = "UPDATE_USER_NAMES_EMAIL"
const val LOG_DELETE_USER = "DELETE USER"
const val LOG_SIGNOUT_USER = "USER SIGN OUT"
const val LOG_ADD_USER_INTERV = "ADD USER TO INTERV"

//endregion


const val TAG_ALERT = "FIREBASE ALERTE"

//endregion


// region ------ NOTIFICATIONS PUSH ----

const val NP_ID_NEW_ALERT = 1515
const val NP_CHANNEL_ID_NEW_ALERT = "EDEN_NOTIFICATION_PUSH_CHANNEL"

