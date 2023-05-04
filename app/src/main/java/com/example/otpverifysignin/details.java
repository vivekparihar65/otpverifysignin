package com.example.otpverifysignin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class details extends AppCompatActivity {

    EditText first,last,phn,user,age;
    ImageView image;
    Button upload;
    String userid;
    AppCompatButton submit;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String currentphotopath;
    StorageReference storageReference;
    private final int GALLERY_REQ_CODE=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        firebaseDatabase=FirebaseDatabase.getInstance();
       databaseReference = firebaseDatabase.getReference();
       storageReference= FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        first=findViewById(R.id.frstname);
        last=findViewById(R.id.lstname);
        phn=findViewById(R.id.phno);
        user=findViewById(R.id.uname);
        age=findViewById(R.id.ag);
        image=findViewById(R.id.imgupload);
        upload=findViewById(R.id.btnupload);

        //for uploading the image from gallery
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,GALLERY_REQ_CODE);
            }
        });
        submit=findViewById(R.id.btnsubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                String getName= first.getText().toString();
                String getlstName= last.getText().toString();
                String getuser=user.getText().toString();
                String getPhn=phn.getText().toString();
                String getage=age.getText().toString();

                HashMap<String,Object>hashMap = new HashMap<>();
                hashMap.put("First Name",getName);
                hashMap.put("Last Name",getlstName);
                hashMap.put("User Name",getuser);
                hashMap.put("Phone number",getPhn);
                hashMap.put("Age",getage);
                userid=mAuth.getCurrentUser().getUid();
                FirebaseFirestore.getInstance().collection("User")
                        .document(userid)
                        .set(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(details.this, "Data added succesfully!!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(details.this,account.class ));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(details.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==GALLERY_REQ_CODE)
            {
                // for gallery

                image.setImageURI(data.getData());

                uploadimagetofirebase(data.getData());
            }
        }
    }
//uploading the image in firebase
    private void uploadimagetofirebase(Uri data) {

        StorageReference s=storageReference.child("images/");
        s.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                s.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag","uploaded"+uri.toString());
                    }

                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(details.this, "upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}