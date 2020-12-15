package com.fixedit.fixeditstaff.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fixedit.fixeditstaff.Activities.ImageCrop.PickerBuilder;
import com.fixedit.fixeditstaff.Adapters.PicturesAdapter;
import com.fixedit.fixeditstaff.Callbacks.PicturesUploaded;
import com.fixedit.fixeditstaff.R;
import com.fixedit.fixeditstaff.Utils.CommonUtils;
import com.fixedit.fixeditstaff.Utils.CompressImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.watermark.androidwm.bean.WatermarkText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Assignemnt extends AppCompatActivity implements PicturesUploaded {

    ImageView back;
    List<Uri> mSelected2 = new ArrayList<>();

    Button pickBeforePictures, pickAfterPictures;

    LinearLayout beforeUploadBtn, afterUploabtn;
    private ArrayList<String> itemList = new ArrayList<>();
    private ArrayList<Uri> mSelected = new ArrayList<>();
    private PicturesAdapter adapter, adapter2;
    Button uploadBefore;
    StorageReference mStorageRef;

    DatabaseReference mDatabase;
    String orderId;
    WatermarkText watermarkText;
    RelativeLayout wholeLayout;
    int beforePicsCount = 0;
    int beforePicUploadedCount = 0;
    int afterPicsCount = 0;
    PicturesUploaded callback;
    private ArrayList<String> itemList2 = new ArrayList<>();
    Button uploadAfter;
    ImageView shopBill, clientBill;


    Button uploadShopBill, uploadClientBill;
    private String shopBillImg, clientBillImg;
    boolean canViewClientBill = false, canViewShopBill = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        callback = (PicturesUploaded) this;
        orderId = getIntent().getStringExtra("orderId");
        back = findViewById(R.id.back);
        clientBill = findViewById(R.id.clientBill);
        pickBeforePictures = findViewById(R.id.pickBeforePictures);
        shopBill = findViewById(R.id.shopBill);
        uploadShopBill = findViewById(R.id.uploadShopBill);
        uploadClientBill = findViewById(R.id.uploadClientBill);
        wholeLayout = findViewById(R.id.wholeLayout);
        beforeUploadBtn = findViewById(R.id.beforeUploadBtn);
        uploadBefore = findViewById(R.id.uploadBefore);
        afterUploabtn = findViewById(R.id.afterUploabtn);
        pickAfterPictures = findViewById(R.id.pickAfterPictures);
        uploadAfter = findViewById(R.id.uploadAfter);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uploadShopBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (shopBillImg != null) {
                    wholeLayout.setVisibility(View.VISIBLE);

                    uploadShopBill.setVisibility(View.GONE);
                    putPictures(shopBillImg, "shop", true);
                } else {
                    CommonUtils.showToast("Please select shop material bill");
                }
            }
        });
        uploadClientBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clientBillImg != null) {
                    wholeLayout.setVisibility(View.VISIBLE);
                    uploadClientBill.setVisibility(View.GONE);
                    putPictures(clientBillImg, "client", true);
                } else {
                    CommonUtils.showToast("Please select client signed bill");
                }
            }
        });


        watermarkText = new WatermarkText("OrderId: " + orderId + "\nTime: " + CommonUtils.getFormattedDate(System.currentTimeMillis()))
                .setPositionX(0.5)
                .setPositionY(0.5)
                .setTextAlpha(100)
                .setTextColor(Color.WHITE)
                .setTextShadow(0.1f, 5, 5, Color.BLUE);

        getPermissions();
        getDataFromDB();


        shopBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canViewShopBill) {
                    ArrayList<String> item = new ArrayList<>();
                    item.add(shopBillImg);
                    Intent i = new Intent(Assignemnt.this, PicturesSlider.class);
                    i.putExtra("list", item);
                    i.putExtra("position", 0);
                    startActivity(i);
                } else {
                    captureShopBill();
                }
            }
        });
        clientBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canViewClientBill) {
                    ArrayList<String> item = new ArrayList<>();
                    item.add(clientBillImg);
                    Intent i = new Intent(Assignemnt.this, PicturesSlider.class);
                    i.putExtra("list", item);
                    i.putExtra("position", 0);
                    startActivity(i);
                } else {
                    captureClientBill();
                }
            }
        });


        uploadBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setUploaded(true);
                wholeLayout.setVisibility(View.VISIBLE);
                beforePicsCount = itemList.size();
                beforeUploadBtn.setVisibility(View.GONE);
                for (String img : itemList) {
                    putPictures(img, "before", false);
                }
            }
        });
        uploadAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter2.setUploaded(true);
                wholeLayout.setVisibility(View.VISIBLE);
                afterPicsCount = itemList.size();
                afterUploabtn.setVisibility(View.GONE);
                adapter2.setUploaded(true);
                for (String img : itemList2) {
                    putPictures(img, "after", false);
                }
            }
        });
        pickBeforePictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGallery();

            }
        });
        pickAfterPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGallery2();

            }
        });
        setUpBeforeImgs();
        setUpAfterImgs();


    }

    private void getDataFromDB() {
        mDatabase.child("OrderPictures").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.child("before").exists()) {
                        itemList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.child("before").getChildren()) {
                            itemList.add(snapshot.getValue(String.class));
                        }
                        adapter.setUploaded(true);
                        beforeUploadBtn.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    } else {

                    }

                    if (dataSnapshot.child("after").exists()) {
                        itemList2.clear();
                        for (DataSnapshot snapshot : dataSnapshot.child("after").getChildren()) {

                            itemList2.add(snapshot.getValue(String.class));
                        }
                        adapter2.setUploaded(true);
                        afterUploabtn.setVisibility(View.GONE);
                        adapter2.notifyDataSetChanged();
                    } else {


                    }
                    if (dataSnapshot.child("shop").exists()) {
                        canViewShopBill = true;
                        uploadShopBill.setVisibility(View.GONE);
                        Glide.with(Assignemnt.this).load(dataSnapshot.child("shop").getValue(String.class)).into(shopBill);
                        shopBillImg = dataSnapshot.child("shop").getValue(String.class);
                    } else {
                    }
                    if (dataSnapshot.child("client").exists()) {
                        canViewClientBill = true;
                        clientBillImg = dataSnapshot.child("client").getValue(String.class);

                        uploadClientBill.setVisibility(View.GONE);
                        Glide.with(Assignemnt.this).load(dataSnapshot.child("client").getValue(String.class)).into(clientBill);
                    } else {
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Bitmap mark(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(32);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setUnderlineText(true);
        canvas.drawText("Order Id:" + orderId, 20, 80, paint);
        canvas.drawText("Time:" + CommonUtils.getFullDate(System.currentTimeMillis()), 20, 135, paint);

        return result;
    }

    public void putPictures(String path, final String type, final boolean abc) {
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        ;
        Uri file = Uri.fromFile(new File(path));


        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        String key = mDatabase.push().getKey();
//                        Uri downloadUrl = taskSnapshot.getre;
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri downloadUrl = urlTask.getResult();
                        beforePicUploadedCount++;
                        if (abc) {
                            mDatabase.child("OrderPictures").child(orderId).child(type).setValue("" + downloadUrl)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            CommonUtils.showToast("Pic uploaded");

                                            wholeLayout.setVisibility(View.GONE);
//                                progress.setVisibility(View.GONE);
                                        }
                                    });
                        } else {
                            mDatabase.child("OrderPictures").child(orderId).child(type).child(key).setValue("" + downloadUrl)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            CommonUtils.showToast("Pic uploaded");

                                            callback.onAllPicturesUploaded(beforePicsCount, beforePicsCount);

//                                progress.setVisibility(View.GONE);
                                        }
                                    });

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(Assignemnt.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    private void startGallery2() {
        new PickerBuilder(Assignemnt.this, PickerBuilder.SELECT_FROM_CAMERA)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        mSelected.add(imageUri);
                        try {

                            String commpress = CompressImage.compressImage("" + imageUri, Assignemnt.this);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(Assignemnt.this.getContentResolver(), Uri.fromFile(new File(commpress)));

                            Uri tempUri = getImageUri(mark(bitmap));
                            File finalFile = new File(getRealPathFromURI(tempUri));

                            String actualPath = "" + finalFile;
                            itemList2.add("" + actualPath);
                            adapter2.notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                            CommonUtils.showToast(e.getMessage());
                        }

                    }
                })
                .start();
    }

    private void captureClientBill() {
        new PickerBuilder(Assignemnt.this, PickerBuilder.SELECT_FROM_CAMERA)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        mSelected.add(imageUri);
                        try {

                            String commpress = CompressImage.compressImage("" + imageUri, Assignemnt.this);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(Assignemnt.this.getContentResolver(), Uri.fromFile(new File(commpress)));

                            Uri tempUri = getImageUri(mark(bitmap));
                            File finalFile = new File(getRealPathFromURI(tempUri));

                            clientBillImg = "" + finalFile;
                            Glide.with(Assignemnt.this).load(clientBillImg).into(clientBill);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                })
                .start();
    }

    private void captureShopBill() {
        new PickerBuilder(Assignemnt.this, PickerBuilder.SELECT_FROM_CAMERA)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        mSelected.add(imageUri);
                        try {

                            String commpress = CompressImage.compressImage("" + imageUri, Assignemnt.this);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(Assignemnt.this.getContentResolver(), Uri.fromFile(new File(commpress)));

                            Uri tempUri = getImageUri(mark(bitmap));
                            File finalFile = new File(getRealPathFromURI(tempUri));

                            String actualPath = "" + finalFile;
                            shopBillImg = actualPath;
                            Glide.with(Assignemnt.this).load(shopBillImg).into(shopBill);

                            adapter.notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .start();
    }


    private void startGallery() {
        new PickerBuilder(Assignemnt.this, PickerBuilder.SELECT_FROM_CAMERA)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        mSelected.add(imageUri);
                        try {
//                            mark(bitmap,"OrderId:+"+orderId+"\nTime: "+System.currentTimeMillis());
//                            image.setImageBitmap(mark(bitmap, "OrderId:+" + orderId + "\nTime: " + System.currentTimeMillis()));

                            String commpress = CompressImage.compressImage("" + imageUri, Assignemnt.this);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(Assignemnt.this.getContentResolver(), Uri.fromFile(new File(commpress)));

                            Uri tempUri = getImageUri(mark(bitmap));
                            File finalFile = new File(getRealPathFromURI(tempUri));

                            String actualPath = "" + finalFile;
                            itemList.add("" + actualPath);
                            adapter.notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        }
//                        Glide.with(EditProfile.this).load(mSelected.get(0)).into(image);
//                        putPictures(imageUrl.get(0));

                    }
                })
                .start();
    }

    public String getRealPathFromURI(Uri uri) {

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);

    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void getPermissions() {


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,


        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
//            Intent intent = new Intent(MapsActivity.this, GPSTrackerActivity.class);
//            startActivityForResult(intent, 1);
        }
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    private void setUpBeforeImgs() {
        RecyclerView beforeRecycler;
        beforeRecycler = findViewById(R.id.beforeRecycler);
        itemList = new ArrayList<>();
        adapter = new PicturesAdapter(this, itemList, new PicturesAdapter.PicturesAdapterCallback() {
            @Override
            public void onDelete(int position) {
//                CommonUtils.showToast("" + position);
                itemList.remove(position);
                mSelected.remove(position);
                adapter.notifyDataSetChanged();

            }
        });
        beforeRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        beforeRecycler.setAdapter(adapter);


    }

    private void setUpAfterImgs() {
        RecyclerView afterRecycler;
        afterRecycler = findViewById(R.id.afterRecycler);
        itemList2 = new ArrayList<>();
        adapter2 = new PicturesAdapter(this, itemList2, new PicturesAdapter.PicturesAdapterCallback() {
            @Override
            public void onDelete(int position) {
//                CommonUtils.showToast("" + position);
                itemList2.remove(position);
                mSelected2.remove(position);
                adapter2.notifyDataSetChanged();

            }
        });
        afterRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        afterRecycler.setAdapter(adapter2);


    }

    @Override
    public void onAllPicturesUploaded(int now, int total) {
        if (now == total) {
            wholeLayout.setVisibility(View.GONE);
        }
    }
}
