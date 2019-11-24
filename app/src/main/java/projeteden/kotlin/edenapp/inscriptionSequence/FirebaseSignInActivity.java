package projeteden.kotlin.edenapp.inscriptionSequence;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import projeteden.kotlin.edenapp.MainActivity;
import projeteden.kotlin.edenapp.R;

import java.util.Objects;

import static projeteden.kotlin.edenapp.utilities.UtilsLocationKt.createUserLocation;
import static projeteden.kotlin.edenapp.utilities.UtilsUserKt.createUser;


public class FirebaseSignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "FIREBASE_CONNECTION";
    private static final String GOOGLE_CONNECTION_TYPE = "GOOGLE";
    private static final String FACEBOOK_CONNECTION_TYPE = "FACEBOOK";

    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;

    private SignInButton googleSignInButton;
    private LoginButton fbSignInButton;


    //region ------ ACTIVITY LIFE ------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_connection);

        new AlertDialog.Builder(this)
                .setTitle("Avertissement")
                .setMessage("Bonjour à toi bel utilisateur de la première heure ! " +
                        "L'application que tu t'apprètes à utiliser est toujours en phase de développement. " +
                        "Cela signifie qu'elle ne respecte pas tout à fait les normes du RGPD entre autres. " +
                        "Il est également possible que tu rencontres de nombreux bugs, n'hésite pas à les " +
                        "signaler ! ;) " +
                        "Pour la gestion de tes données personnelles, pas d'inquiétude, " +
                        "elles sont très régulièrement " +
                        "effacées. Notre stockage se fait actuellement sur la plateforme Firebase de Google. " +
                        "Nous te souhaitons la meilleure expérience d'utilisation <3\n" +
                        "Signé : Les devs")
                .setCancelable(true)
                .create()
                .show();

        googleSignInButton = findViewById(R.id.google_sign_in_button);
        fbSignInButton = findViewById(R.id.facebook_login_button);

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        setupGoogleButton();
        setupFacebookButton();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null)
                    firebaseAuthWithGoogle(account);
            } else {
                // Google Sign-In failed
                Log.e(TAG, "Google Sign-In failed.");
            }
        }
    }

    public void onBackPressed() {
        //super.onBackPressed();
        finish();
        System.exit(-1);
    }

    //endregion

    //region ------ FACEBOOK ------
    private void setupFacebookButton() {
        callbackManager = CallbackManager.Factory.create();
        //TODO("Corriger l'erreur de dépréciation")
        fbSignInButton.setReadPermissions("email", "public_profile");
        fbSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            AuthResult result = task.getResult();
                            if (result != null)
                                if (result.getAdditionalUserInfo() != null)
                                    collectUserDataIfIsNew(FACEBOOK_CONNECTION_TYPE, result.getAdditionalUserInfo().isNewUser());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(FirebaseSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //endregion

    //region ------ GOOGLE ------

    private void setupGoogleButton() {
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleApiClient = new GoogleApiClient.Builder(FirebaseSignInActivity.this)
                        //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseCredential(credential);
    }

    //endregion

    //region ------ FIREBASE ------

    private void firebaseCredential(AuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());

                            Toast.makeText(FirebaseSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FirebaseSignInActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();

                            AuthResult result = task.getResult();
                            if (result != null)
                                if (result.getAdditionalUserInfo() != null)
                                    collectUserDataIfIsNew(GOOGLE_CONNECTION_TYPE, result.getAdditionalUserInfo().isNewUser());
                        }
                    }
                });
    }

    private void collectUserDataIfIsNew(final String PROVIDER, Boolean isNewUser) {
        if (mFirebaseAuth != null) {
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                if (isNewUser) {
                    addUserFirestore(PROVIDER);
                } else updateUI();
            }
        }

    }

    private void addUserFirestore(final String PROVIDER) {
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mFirebaseUser != null;
        createUser(mFirebaseUser.getUid(), PROVIDER);
        createUserLocation(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), null, null);

        startActivity(new Intent(FirebaseSignInActivity.this, GetUIActivity.class));
        finish();
    }

    //endregion

    private void updateUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(FirebaseSignInActivity.this, MainActivity.class));
            finish();
        }
    }
}