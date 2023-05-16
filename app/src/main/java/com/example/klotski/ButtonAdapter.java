package com.example.klotski;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ButtonAdapter extends ArrayAdapter<String> {
    private HighScoreManager highScoreManager;
    private Context c;

    public ButtonAdapter(Context context, List<String> buttons, HighScoreManager hsm, Context c) {
        super(context, R.layout.list_item_button, buttons);
        highScoreManager = hsm;
        this.c = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Button button = (Button) convertView;
        if (button == null) {
            button = (Button) LayoutInflater.from(getContext()).inflate(R.layout.list_item_button, parent, false);
        }
        if(highScoreManager.getHighScore(position) == 0){
            button.setText(getItem(position) + "\nIncomplete");
        }else{
            button.setText(getItem(position) + "\nBest: " + highScoreManager.getHighScore(position) + " Moves");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Level.setCurrentLevelNum(position);
                Intent intent;
                intent = new Intent(c, GameplayActivity.class);
                c.startActivity(intent);
            }
        });

        return button;
    }
}

