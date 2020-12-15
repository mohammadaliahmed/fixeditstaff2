package com.fixedit.fixeditstaff.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.fixedit.fixeditstaff.Adapters.ServicesBookedAdapter;
import com.fixedit.fixeditstaff.Models.OrderModel;
import com.fixedit.fixeditstaff.Models.ServiceCountModel;
import com.fixedit.fixeditstaff.Models.ServiceModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookingSumary extends AppCompatActivity implements NotificationObserver {

    RecyclerView recyclerview;

    ServicesBookedAdapter adapter;
    TextView date, time, buildingType;
    RelativeLayout next;
    TextView serviceType;
    ImageView back;
    String orderId;
    DatabaseReference mDatabase;
    private OrderModel orderModel;
    Button start;
    Button invoiceOk, invoiceModify, pictures;
    private String adminFcmKey;
    TextView timer;
    private Timer t;
    private ServiceModel parentServiceModel;
    private long finalTotalTime;
    private long finalTotalCost;
    private boolean peakHour;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        orderId = getIntent().getStringExtra("orderId");
        invoiceOk = findViewById(R.id.invoiceOk);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        adapter = new ServicesBookedAdapter(this, new ArrayList<ServiceCountModel>());
//        recyclerview.setAdapter(adapter);

        back = findViewById(R.id.back);
//        next = findViewById(R.id.next);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        invoiceModify = findViewById(R.id.invoiceModify);
        buildingType = findViewById(R.id.buildingType);
        timer = findViewById(R.id.timer);
        serviceType = findViewById(R.id.serviceType);
        start = findViewById(R.id.start);
        pictures = findViewById(R.id.pictures);
        getOrderFromDB();
        getAdminFCMkey();


        pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BookingSumary.this, Assignemnt.class);
                i.putExtra("orderId", orderId);
                startActivity(i);
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!orderModel.isJobStarted()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BookingSumary.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Start job?");

                    // add the buttons
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            timer.setVisibility(View.VISIBLE);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("jobStarted", true);
                            map.put("jobStartTime", System.currentTimeMillis());
                            mDatabase.child("Orders").child(orderId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    CommonUtils.showToast("Job started");
                                    start.setText("Finish");
                                }
                            });
                            NotificationAsync notificationAsync = new NotificationAsync(BookingSumary.this);
                            String notification_title = orderModel.getServiceName() + " Job Started";
                            String notification_message = "Click to view";
                            notificationAsync.execute("ali", orderModel.getUser().getFcmKey(), notification_title, notification_message, "jobStart", "" + orderId);

                            NotificationAsync notificationAsync1 = new NotificationAsync(BookingSumary.this);

                            notificationAsync1.execute("ali", SharedPrefs.getAdminFcmKey(), notification_title, notification_message, "jobStart", "" + orderId);


                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BookingSumary.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Finish job?");

                    // add the buttons
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            timer.setVisibility(View.GONE);

                            HashMap<String, Object> map = new HashMap<>();

                            map.put("jobEndTime", System.currentTimeMillis());
                            map.put("jobFinish", true);
                            map.put("serviceCharges", finalTotalCost);
                            map.put("peakHour", peakHour);
                            map.put("totalHours", finalTotalTime);
                            mDatabase.child("Orders").child(orderId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    CommonUtils.showToast("Job Finished");
                                    start.setVisibility(View.GONE);
                                    NotificationAsync notificationAsync = new NotificationAsync(BookingSumary.this);
                                    String notification_title = orderModel.getServiceName() + " Job Finished";
                                    String notification_message = "Click to view";
                                    notificationAsync.execute("ali", orderModel.getUser().getFcmKey(), notification_title, notification_message, "jobDone", "" + orderId);

                                    NotificationAsync notificationAsync1 = new NotificationAsync(BookingSumary.this);

                                    notificationAsync1.execute("ali", SharedPrefs.getAdminFcmKey(), notification_title, notification_message, "jobDone", "" + orderId);


                                }
                            });


                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });

        invoiceOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!orderModel.isJobFinish()) {
                    CommonUtils.showToast("Please finish job first");
                } else {
                    Intent i = new Intent(BookingSumary.this, FinishJob.class);
                    i.putExtra("orderId", orderId);
                    startActivity(i);
                }
            }
        });

        invoiceModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationAsync notificationAsync = new NotificationAsync(BookingSumary.this);
                String notification_title = "Order Change request";
                String notification_message = "Click to view";
                notificationAsync.execute("ali", adminFcmKey, notification_title, notification_message, "Modify", "" + orderId);
                CommonUtils.showToast("Request sent to admin for order change");

            }
        });

//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(BookingSumary.this, ChooseAddress.class));
//            }
//        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getParentServiceFromDB(final OrderModel order) {
        mDatabase.child("Services").child(order.getServiceId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    parentServiceModel = dataSnapshot.getValue(ServiceModel.class);
                    if (parentServiceModel != null) {
                        long hour = System.currentTimeMillis() - order.getJobStartTime();
                        hour = hour / 1000;
                        hour = hour / 60;
                        float hours = (float) hour / 60;
                        int h = (int) (hour / 60);
                        float dif = hours - h;
                        if (dif > 0.17) {
                            finalTotalTime = h + 1;
                        } else {
                            finalTotalTime = h;
                        }
                        if (finalTotalTime == 0) {
                            finalTotalTime = 1;
                        }
                        if (CommonUtils.getWhichRateToCharge(orderModel.getChosenTime())) {
                            if (order.isCommercialBuilding()) {
                                finalTotalCost = finalTotalTime * parentServiceModel.getCommercialServicePeakPrice();

                            } else {
                                finalTotalCost = finalTotalTime * parentServiceModel.getPeakPrice();
                            }

                            long finalCost = finalTotalCost;
                            if (orderModel.isCouponApplied()) {
                                float val = (float) (100 - orderModel.getDiscount()) / 100;
                                finalTotalCost = (long) (finalCost * val);
                            }


                            peakHour = true;
                        } else {
                            if (order.isCommercialBuilding()) {
                                finalTotalCost = finalTotalTime * parentServiceModel.getCommercialServicePrice();

                            } else {
                                finalTotalCost = finalTotalTime * parentServiceModel.getServiceBasePrice();
                            }
                            long finalCost = finalTotalCost;
                            if (orderModel.isCouponApplied()) {
                                float val = (float) (100 - orderModel.getDiscount()) / 100;
                                finalTotalCost = (long) (finalCost * val);
                            }
                            peakHour = false;
                        }

//                        CommonUtils.showToast("" + finalTotalCost);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAdminFCMkey() {
        mDatabase.child("Admin").child("fcmKey").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    adminFcmKey = dataSnapshot.getValue(String.class);
                    SharedPrefs.setAdminFcmKey(adminFcmKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getOrderFromDB() {
        mDatabase.child("Orders").child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    orderModel = dataSnapshot.getValue(OrderModel.class);
                    if (orderModel != null) {
                        serviceType.setText(orderModel.getServiceName());
                        date.setText(orderModel.getDate().replace("\n", " "));
                        time.setText(orderModel.getChosenTime());
                        buildingType.setText(orderModel.getBuildingType());
                        adapter = new ServicesBookedAdapter(BookingSumary.this, orderModel.getCountModelArrayList());
                        recyclerview.setAdapter(adapter);
                        if (orderModel.isJobStarted() && !orderModel.isJobFinish()) {
                            start.setText("Finish");
                            timer.setVisibility(View.VISIBLE);


                            updateTimer(orderModel.getJobStartTime());
                        } else if (!orderModel.isJobStarted() && orderModel.isJobFinish()) {
                            timer.setVisibility(View.GONE);

                            start.setText("Start");
                        }

                        if (orderModel.isJobFinish()) {
//                            timer.setVisibility(View.GONE);

                            start.setVisibility(View.GONE);
                        } else {
//                            timer.setVisibility(View.GONE);

                            start.setVisibility(View.VISIBLE);


                        }

                    }


                    getParentServiceFromDB(orderModel);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateTimer(final long jobStartTime) {
        t = new Timer();
//Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {
                                      //Called each time when 1000 milliseconds (1 second) (the period parameter)
//                                      CommonUtils.showToast("After 10");
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
//                                              CommonUtils.showToast("sdfdsfds");
                                              long time = System.currentTimeMillis() - jobStartTime;
                                              timer.setText("Elapsed Time: " + CommonUtils.elapsedTime(time / 1000));
                                          }
                                      });


                                  }

                              },
//Set how long before to start calling the TimerTask (in milliseconds)
                0,
//Set the amount of time between each execution (in milliseconds)
                1000);
    }

    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }
}
