package com.kevin.wilmingtonwishlist;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.http.Url;

public class ImagesActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {

    //View Post Stuff
    private ImageView mSoloImage;
    private TextView mSoloTitle;
    private TextView mSoloDescription;
    private TextView mSoloPrice;
    private TextView mSoloContactEmail;

    //Edit Post Stuff
    private ImageView mReviseImage;
    private TextView mReviseTitle;
    private TextView mReviseDescription;
    private TextView mRevisePrice;
    private TextView mReviseContactEmail;
    private Button mReviseButton;
    private StorageReference mStorageRef;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private StorageTask mUploadTask;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProgressBar mReviseProgressBar;
    private Button mReviseButtonChooseImage;
    private String mReviseImageUri;

    //Search Stuff


    //Upload stuff
    private String mUser;


    private Uri mImageUri;

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private FirebaseStorage mStorage;
    private DatabaseReference mDataRef;
    private ValueEventListener mDBListener;

    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Card View Stuff
        mRecyclerView = findViewById(R.id.recycleMe);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();

        mAdapter = new ImageAdapter(ImagesActivity.this, mUploads);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(ImagesActivity.this);

        mStorage = FirebaseStorage.getInstance();
        mDataRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDBListener = mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

        EditText editText = findViewById(R.id.edit_search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fiter(s.toString());
            }
        });
    }

    private void fiter(String text){
        ArrayList<Upload> filteredList = new ArrayList<>();
        for (Upload upload : mUploads) {
            if (upload.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(upload);
            }
        }

        mAdapter.filterList(filteredList);
    }

    @Override
    public void onItemClick(int position) {
        setContentView(R.layout.layout_view_post);

        mSoloImage = findViewById(R.id.solo_image_view);
        mSoloTitle = findViewById(R.id.solo_title);
        mSoloDescription = findViewById(R.id.solo_description);
        mSoloPrice = findViewById(R.id.solo_price);
        mSoloContactEmail = findViewById(R.id.solo_contact_email);
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
        final Upload selectedItem2 = mUploads.get(position);

        Picasso.with(this).load(selectedItem2.getImageUrl()).into(mSoloImage);
        mSoloTitle.setText(selectedItem2.getName());
        mSoloDescription.setText(selectedItem2.getDescription());
        mSoloPrice.setText("$" + selectedItem2.getPrice());
        mSoloContactEmail.setText(selectedItem2.getContactEmail());

        mSoloContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { selectedItem2.getContactEmail() });
                intent.putExtra(Intent.EXTRA_SUBJECT, selectedItem2.getName());
                intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                startActivity(Intent.createChooser(intent, ""));
            }
        });


    }

    @Override
    public void onEditClick(final int position) {
        Upload selectedItem3 = mUploads.get(position);
        if (mUser.equals(selectedItem3.getmUser())) {
            Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_edit_post);

            mReviseImage = findViewById(R.id.revise_image_view);
            mReviseTitle = findViewById(R.id.revise_text_file_name);
            mReviseDescription = findViewById(R.id.revise_text_description);
            mRevisePrice = findViewById(R.id.revise_text_price);
            mReviseContactEmail = findViewById(R.id.revise_text_contact_email);
            mReviseButton = findViewById(R.id.revise_button_upload);
            mReviseProgressBar = findViewById(R.id.revise_progress_bar);
            mReviseButtonChooseImage = findViewById(R.id.revise_button_choose_image);


            Picasso.with(this).load(selectedItem3.getImageUrl()).into(mReviseImage);
            mReviseTitle.setText(selectedItem3.getName());
            mReviseDescription.setText(selectedItem3.getDescription());
            mRevisePrice.setText(selectedItem3.getPrice());
            mReviseContactEmail.setText(selectedItem3.getContactEmail());

            mReviseButtonChooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFileChooser();
                }
            });

            mReviseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUploadTask != null && mUploadTask.isInProgress()) {
                        Toast.makeText(ImagesActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadFile();
                        onDeleteClick(position);
                    }
                }
            });

        }else {
            Toast.makeText(this, "You cannot edit someone else's post!!", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        if (mUser.equals(selectedItem.getmUser())){
            StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mDataRef.child(selectedKey).removeValue();
                    Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                }
            });}else {
            Toast.makeText(this, "You cannot delete other people's posts!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataRef.removeEventListener(mDBListener);
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

            Picasso.with(this).load(mImageUri).into(mReviseImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if (mImageUri != null) {
            mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
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
                                    mReviseProgressBar.setProgress(0);
                                }
                            }, 500);

                            //trying something new
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    Upload upload = new Upload(mReviseTitle.getText().toString().trim(),
                                            url,
                                            mReviseDescription.getText().toString().trim(),
                                            mRevisePrice.getText().toString().trim(),
                                            mReviseContactEmail.getText().toString().trim(),
                                            mUser.trim());
                                    String uploadId = mDataRef.push().getKey();
                                    mDataRef.child(uploadId).setValue(upload);
                                    Toast.makeText(ImagesActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                    resetFields();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ImagesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetFields() {
        Picasso.with(this).load(R.drawable.camera_icon).into(mReviseImage);
        mReviseTitle.setText("");
        mReviseDescription.setText("");
        mRevisePrice.setText("");
        mReviseContactEmail.setText("");
    }

}
