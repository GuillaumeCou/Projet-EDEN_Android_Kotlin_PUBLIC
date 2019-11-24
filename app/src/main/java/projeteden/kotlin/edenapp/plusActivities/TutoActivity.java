package projeteden.kotlin.edenapp.plusActivities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import projeteden.kotlin.edenapp.R;

import java.util.Vector;

public class TutoActivity extends AppCompatActivity {
    //TODO Corriger les erreurs

    RecyclerView recyclerView;
    Vector<YouTubeVideos> youtubeVideos = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuto);

        recyclerView = findViewById(R.id.youtube_video);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        youtubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/YQtT0qjcTgw\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
        youtubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/jVAbHGJapKA\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));

        VideoAdapter videoAdapter = new VideoAdapter(youtubeVideos);

        recyclerView.setAdapter(videoAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
