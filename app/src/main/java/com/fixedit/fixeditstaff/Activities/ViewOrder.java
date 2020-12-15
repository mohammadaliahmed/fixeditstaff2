package com.fixedit.fixeditstaff.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fixedit.fixeditstaff.Models.OrderModel;
import com.fixedit.fixeditstaff.R;
import com.fixedit.fixeditstaff.Utils.CommonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;


public class ViewOrder extends AppCompatActivity {

    String orderId;
    private String adIdFromLink;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        onNewIntent(getIntent());


    }

    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            adIdFromLink = data.substring(data.lastIndexOf("/") + 1);
            orderId = adIdFromLink;
            getOrderFromServer(orderId);
        }
    }

    private void getOrderFromServer(String orderId) {
        mDatabase.child("Orders").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                    if (orderModel != null) {
                        if (!orderModel.isJobDone()) {
                            if (orderModel.isArrived()) {
                                Intent i = new Intent(ViewOrder.this, BookingSumary.class);
                                i.putExtra("orderId", "" + orderModel.getOrderId());
                                startActivity(i);
                                finish();
                            } else {
                                Intent i = new Intent(ViewOrder.this, MapsActivity.class);
                                i.putExtra("orderId", "" + orderModel.getOrderId());
                                i.putExtra("latitude", orderModel.getLat());
                                i.putExtra("longitude", orderModel.getLon());
                                startActivity(i);
                                finish();
                            }
                        } else {
                            CommonUtils.showToast("Job already done");
                            Intent i = new Intent(ViewOrder.this, AssignmentHistory.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
