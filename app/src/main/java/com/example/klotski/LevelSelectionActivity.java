package com.example.klotski;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class LevelSelectionActivity extends AppCompatActivity {
    private HighScoreManager highScoreManager;
    private ButtonAdapter buttonAdapter;
    private Context c;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.darkbrown)));

        c = LevelSelectionActivity.this;

        highScoreManager = new HighScoreManager(this);

        List<String> buttons = new ArrayList<>();
        buttons.add("Easy");
        buttons.add("Classic");
        buttons.add("Difficult");
        buttons.add("Test Level");

        buttonAdapter = new ButtonAdapter(this, buttons, highScoreManager, c);

        listView = findViewById(R.id.list_view);
        listView.setAdapter(buttonAdapter);

    }
}
