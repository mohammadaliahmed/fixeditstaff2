package com.fixedit.fixeditstaff.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fixedit.fixeditstaff.Models.OrderModel;
import com.fixedit.fixeditstaff.Models.ServicemanModel;
import com.fixedit.fixeditstaff.R;
import com.fixedit.fixeditstaff.Utils.CommonUtils;
import com.fixedit.fixeditstaff.Utils.PrefManager;
import com.fixedit.fixeditstaff.Utils.SharedPrefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class AssignedOrders extends AppCompatActivity {
    ImageView back;
    ArrayList<OrderModel> arrayList = new ArrayList<>();
    OrdersAdapter adapter;
    DatabaseReference mDatabase;

    Button arrived;
    FloatingActionButton takeMeToCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assigned_orders);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        @SuppressLint("WrongConstant") LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OrdersAdapter(this, arrayList, new OrdersAdapter.ChangeStatus() {
            @Override
            public void markOrderAsUnderProcess(OrderModel order, boolean b) {

            }

            @Override
            public void markOrderAsCancelled(OrderModel order) {
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.showDial(true);

        getMyOrdersFromDB();


    }

    private void getMyOrdersFromDB() {
        mDatabase.child("Servicemen").child(SharedPrefs.getUser().getUsername()).child("assignedOrders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    arrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String orderId = snapshot.getKey();
                        getDataFromServer(orderId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDataFromServer(String orderId) {

        mDatabase.child("Orders").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    OrderModel model = dataSnapshot.getValue(OrderModel.class);
                    if (model != null) {
                        if (!model.isJobDone()) {
                            arrayList.add(model);

                        }


                    }

                    Collections.sort(arrayList, new Comparator<OrderModel>() {
                        @Override
                        public int compare(OrderModel listData, OrderModel t1) {
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
