package com.fixedit.fixeditstaff.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.fixedit.fixeditstaff.Models.OrderModel;
import com.fixedit.fixeditstaff.Models.RatingModel;
import com.fixedit.fixeditstaff.R;
import com.fixedit.fixeditstaff.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MyRatings extends AppCompatActivity {
    ImageView back;
    RatingsAdapter adapter;
    private ArrayList<RatingModel> itemList=new ArrayList<>();
    RecyclerView recyclerview;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ratings);
        recyclerview=findViewById(R.id.recyclerview);
        back=findViewById(R.id.back);
        mDatabase=FirebaseDatabase.getInstance().getReference();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter=new RatingsAdapter(this,itemList);
        recyclerview.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerview.setAdapter(adapter);
        getDataFromDB();





    }

    private void getDataFromDB() {
        mDatabase.child("Ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        RatingModel model=snapshot.getValue(RatingModel.class);
                        if(model!=null){
                            if(model.getRatingTo().equalsIgnoreCase(SharedPrefs.getUser().getUsername())){
                                itemList.add(model);
                            }
                        }
                    }
                    Collections.sort(itemList, new Comparator<RatingModel>() {
                        @Override
                        public int compare(RatingModel listData, RatingModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();

                            return ob2.compareTo(ob1);

                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
