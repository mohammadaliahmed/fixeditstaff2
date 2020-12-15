package com.fixedit.fixeditstaff.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fixedit.fixeditstaff.Models.OrderModel;
import com.fixedit.fixeditstaff.R;
import com.fixedit.fixeditstaff.Utils.CommonUtils;
import com.fixedit.fixeditstaff.Utils.NotificationAsync;
import com.fixedit.fixeditstaff.Utils.NotificationObserver;
import com.fixedit.fixeditstaff.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class FinishJob extends AppCompatActivity implements NotificationObserver {

    String orderId;
    DatabaseReference mDatabase;
    OrderModel orderModel;
    private ImageView back;
    TextView totalTime, serviceCharges, totalBill;
    EditText materialBill;
    Button paymentReceived;
    private long totl;
    TextView percentText;
    Button notifyClient;
    TextView couponApplied;
    long prvTotl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_job);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        orderId = getIntent().getStringExtra("orderId");
        totalTime = findViewById(R.id.totalTime);
        totalBill = findViewById(R.id.totalBill);
        serviceCharges = findViewById(R.id.serviceCharges);
        paymentReceived = findViewById(R.id.paymentReceived);
        notifyClient = findViewById(R.id.notifyClient);
        materialBill = findViewById(R.id.materialBill);
        couponApplied = findViewById(R.id.couponApplied);
        percentText = findViewById(R.id.tax);

        back = findViewById(R.id.back);

        getOrderFromDB();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        paymentReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CommonUtils.showToast("Received");
                showAlert();


            }
        });


        notifyClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotiAlert();

            }
        });


        materialBill.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    calculateTotal(s.toString());
                } else {
                    calculateTotal("" + 0);
                }
            }
        });


    }

    private void showNotiAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FinishJob.this);
        builder.setTitle("Alert");
        builder.setMessage("Notify customer about invoice?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NotificationAsync notificationAsync = new NotificationAsync(FinishJob.this);
                String notification_title = "Your total bill is Rs " + totl;
                prvTotl = totl;
                String notification_message = "Please pay to the servicemen. Thank you!";
                notificationAsync.execute("ali", orderModel.getUser().getFcmKey(), notification_title, notification_message, "totalBill", "" + orderId);


            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FinishJob.this);
        builder.setTitle("Alert");
        builder.setMessage("Finish Job?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("totalPrice", totl);
                prvTotl = totl;
                map.put("materialBill", Long.parseLong(materialBill.getText().length() == 0 ? "0" : materialBill.getText().toString()));
                map.put("jobDone", true);
                map.put("paymentReceived", true);
                mDatabase.child("Orders").child(orderId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        NotificationAsync notificationAsync = new NotificationAsync(FinishJob.this);
                        String notification_title = "Thanks for the payment";
                        String notification_message = "Please rate the service";
                        notificationAsync.execute("ali", orderModel.getUser().getFcmKey(), notification_title, notification_message, "paymentReceived", "" + orderId);

                        NotificationAsync notificationAsync1 = new NotificationAsync(FinishJob.this);

                        notificationAsync1.execute("ali", SharedPrefs.getAdminFcmKey(), "Rs " + prvTotl + " has been received", "", "paymentReceived", "" + orderId);
                        CommonUtils.showToast("Payment Received");
                        Intent i = new Intent(FinishJob.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();

                    }
                });


            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void calculateTotal(String s) {
        float amount = Integer.parseInt(s);
        float percent = amount + (amount / 10);
        long finalCost = orderModel.getServiceCharges();
        totl = (int) percent + finalCost;
        double abc = (double) orderModel.getTax() / 100;
        totl = totl + (int) (totl * abc);

        totalBill.setText("Total Bill Amount: Rs " + totl);
    }

    private void getOrderFromDB() {
        mDatabase.child("Orders").child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    orderModel = dataSnapshot.getValue(OrderModel.class);
                    if (orderModel != null) {
                        long finalCost = orderModel.getServiceCharges();
                        serviceCharges.setText("Rs " + finalCost);
                        percentText.setText(orderModel.getTax() + "%");
                        totalTime.setText("" + orderModel.getTotalHours() + " hrs");
                        totalBill.setText("" + orderModel.getTotalPrice());
                        calculateTotal("" + 0);
                        if (orderModel.isCouponApplied()) {
                            couponApplied.setVisibility(View.VISIBLE);
                            couponApplied.setText("" + orderModel.getDiscount() + "%" + " discount has been applied from coupon");
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }
}
