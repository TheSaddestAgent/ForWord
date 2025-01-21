package com.example.forword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class ReviewingActivity extends AppCompatActivity {
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewing);


        tabLayout = findViewById(R.id.tab_layout);

        // Вкладки
        tabLayout.addTab(tabLayout.newTab().setText("Первая активность"));
        tabLayout.addTab(tabLayout.newTab().setText("Вторая активность"));

        // Listener для вкладок
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {// Переход к первой активности
                    startActivity(new Intent(ReviewingActivity.this, LearningActivity.class));
                }
                else{
                    startActivity(new Intent(ReviewingActivity.this, ReviewingActivity.class));

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Не требуется
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Не требуется
            }
        });

    }
}