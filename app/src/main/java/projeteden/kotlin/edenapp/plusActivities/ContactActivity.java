package projeteden.kotlin.edenapp.plusActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import projeteden.kotlin.edenapp.R;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        TextView Facebook = findViewById(R.id.contact_facebook);

        Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.facebook.com/EquipeEden/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook");
                startActivity(intent);
            }
        });

        TextView Twitter = findViewById(R.id.contact_twitter);

        Twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://twitter.com/Projet_EDEN?s=09/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        TextView Mail = findViewById(R.id.contact_email);

        Mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mailto:?subject=Prise de Contact&body=Bonjour");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
