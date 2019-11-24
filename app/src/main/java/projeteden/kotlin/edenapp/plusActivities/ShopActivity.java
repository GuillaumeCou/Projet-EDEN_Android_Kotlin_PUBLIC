package projeteden.kotlin.edenapp.plusActivities;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import projeteden.kotlin.edenapp.R;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        WebView landing_page = new WebView(this);
        setContentView(landing_page);
        landing_page.loadUrl("http://projet-eden.strikingly.com");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
