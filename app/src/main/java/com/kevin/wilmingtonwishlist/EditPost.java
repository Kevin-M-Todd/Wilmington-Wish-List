package com.kevin.wilmingtonwishlist;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EditPost extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private EditText mEditTextFileName;
    private ImageView mImageView;
    private EditText mDescription;
    private EditText mPrice;
    private EditText mContactemail;
    private ProgressBar mProgressBar;
    private String mUser;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDataRef;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        mButtonChooseImage = findViewById(R.id.revise_button_choose_image);
        mButtonUpload = findViewById(R.id.revise_button_upload);
        mEditTextFileName = findViewById(R.id.revise_text_file_name);
        mImageView = findViewById(R.id.revise_image_view);
        mDescription = findViewById(R.id.revise_text_description);
        mPrice = findViewById(R.id.revise_text_price);
        mContactemail = findViewById(R.id.revise_text_contact_email);
        mProgressBar = findViewById(R.id.revise_progress_bar);
        mUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDataRef = FirebaseDatabase.getInstance().getReference("uploads");

        setupFirebaseListener();

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(EditPost.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            //trying something new
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
                                            url,
                                            mDescription.getText().toString().trim(),
                                            mPrice.getText().toString().trim(),
                                            mContactemail.getText().toString().trim(),
                                            mUser.trim());
                                    String uploadId = mDataRef.push().getKey();
                                    mDataRef.child(uploadId).setValue(upload);
                                    Toast.makeText(EditPost.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                    resetFields();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditPost.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        }else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagesActivity(){
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }

    private void setupFirebaseListener(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                }else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    private void resetFields() {
        Picasso.with(this).load(R.drawable.camera_icon).into(mImageView);
        mEditTextFileName.setText("");
        mDescription.setText("");
        mPrice.setText("");
        mContactemail.setText("");
    }
}


