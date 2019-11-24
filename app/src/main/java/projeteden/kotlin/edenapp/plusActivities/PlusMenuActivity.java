package projeteden.kotlin.edenapp.plusActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import projeteden.kotlin.edenapp.ProfileActivity;
import projeteden.kotlin.edenapp.R;
import projeteden.kotlin.edenapp.utilities.UtilsGeneralKt;

public class PlusMenuActivity extends AppCompatActivity implements View.OnClickListener {

    TextView Boutique;
    TextView Tuto;
    TextView Appareillage;
    TextView Contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus_menu);

        Boutique = findViewById(R.id.BoutiqueButton);
        Boutique.setOnClickListener(this);

        Tuto = findViewById(R.id.TutoButton);
        Tuto.setOnClickListener(this);

        Appareillage = findViewById(R.id.AppareillageButton);
        Appareillage.setOnClickListener(this);

        Contact = findViewById(R.id.ContactButton);
        Contact.setOnClickListener(this);

        setupMenuNav();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.BoutiqueButton:
                startActivity(new Intent(PlusMenuActivity.this, ShopActivity.class));
                break;
            case R.id.TutoButton:
                //startActivity(new Intent(PlusMenuActivity.this, TutoActivity.class));
                UtilsGeneralKt.featureUnavailableToast(PlusMenuActivity.this);
                //TODO Corriger les erreurs
                break;
            case R.id.AppareillageButton:
                startActivity(new Intent(PlusMenuActivity.this, PairingActivity.class));
                break;
            case R.id.ContactButton:
                startActivity(new Intent(PlusMenuActivity.this, ContactActivity.class));
                break;
        }
    }

    private void setupMenuNav(){
        BottomNavigationView menuNav = findViewById(R.id.bottom_navigation_bar);
        menuNav.setSelectedItemId(R.id.navbar_other);

        menuNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    default:
                        break;
                    case R.id.navbar_map:
                        UtilsGeneralKt.featureUnavailableToast(PlusMenuActivity.this);
                        //TODO("Diriger vers la map")
                        break;
                    case R.id.navbar_alert:
                        finish();
                        break;
                    case R.id.navbar_profile:
                        startActivity(new Intent(PlusMenuActivity.this, ProfileActivity.class));
                        finish();
                        break;
                }
                return true;
            }
        });
    }
}
