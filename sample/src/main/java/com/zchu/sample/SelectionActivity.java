package com.zchu.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Skean on 2022/2/17.
 */
 public   class SelectionActivity extends AppCompatActivity {
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_selection);
      findViewById(R.id.btnSingle).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startActivity(new Intent(SelectionActivity.this, SingleActivity.class));
         }
      });
      findViewById(R.id.btnObservable).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startActivity(new Intent(SelectionActivity.this, MainActivity.class));
         }
      });
   }
}
