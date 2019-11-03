package com.example.jsouppractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button getBtn, saveBtn;
    private TextView result;
    String catstr;
    private EditText category;
    private String userId, descriptiontext;
    private static final String TAG = MainActivity.class.getSimpleName();

    String postTitle, postDesc, postoverImage;
    FirebaseDatabase database;
    DatabaseReference reference, postsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        result = (TextView) findViewById(R.id.result);
        getBtn = (Button) findViewById(R.id.getBtn);


        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        postsRef = reference.child("Item").child("Activities").child("Spirituality");


        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWebsite();
            }
        });

        saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(userId)) {
                    createUser(postTitle, postoverImage, descriptiontext);
                }
                else {
                    updateUser(postTitle, postoverImage, descriptiontext);

                }


            }
        });





    }

    private void createUser(String postTitle, String  postoverImage, String descriptiontext) {

    if(TextUtils.isEmpty(userId)) {
        userId = postsRef.push().getKey();
    }

    ItemModel itemModel = new ItemModel(postTitle,postoverImage,descriptiontext);
    postsRef.push().setValue(itemModel);
    addUserChangeListener();


    }

    private void addUserChangeListener() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ItemModel itemModel = dataSnapshot.getValue(ItemModel.class);

                if (itemModel== null) {
                    Log.e(TAG, "User data is null!");
                    return;

                }

                Log.e(TAG, "User data is changed!" + itemModel.itemtitle + "," + itemModel.description);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e(TAG,"Failed to read user", databaseError.toException());



            }
        });

    }

    private void updateUser(String postTitle, String  postoverImage, String descriptiontext) {

        if(!TextUtils.isEmpty(postTitle)) {
            postsRef.child(userId).child("itemtitle").setValue(postTitle);

        }
        if(!TextUtils.isEmpty(postoverImage)) {
            postsRef.child(userId).child("overviewimg").setValue(postoverImage);

        }
        if(!TextUtils.isEmpty(postDesc)) {
            postsRef.child(userId).child("description").setValue(descriptiontext);

        }

    }


    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {




                final StringBuilder builder = new StringBuilder();
                final StringBuilder descriptionbuilder = new StringBuilder();

                try {

                    category = (EditText) findViewById(R.id.category);
                    String catstr = category.getText().toString();
                    catstr = catstr.replaceAll(" ","-").toLowerCase();
                    String categoryString = "https://www.welcomenepal.com/things-to-do/"+ catstr + ".html";



                    Document doc = Jsoup.connect(categoryString).get();

                    String title = doc.title();
                    postDesc = title;
                     builder.append(title).append("\n \n");

                     for (Element titlelink: doc.select("div.col-md-7 > h1")) {
                         builder.append(titlelink.text()).append("\n \n");
                         postTitle = titlelink.text();
                     }

                     for (Element carouselimage: doc.select("div.carousel-inner > div.item > img")) {
                         String carouselImageurl = carouselimage.absUrl("src");
                         builder.append(carouselImageurl).append("\n \n \n \n ");
                         postoverImage = carouselImageurl;

                     }

                     for (Element typeofit: doc.select(" div.container >  div.col-md-3 > span.mega-title > a") ) {
                         builder.append(typeofit.text()).append("\n \n \n \n \n \n");
                     }


                     for (Element imagelink: doc.select("div.col-md-7 > p > img")) {
                         String imgurl = imagelink.absUrl("src");
                         builder.append(imgurl).append("\n \n");
                     }

                    for (Element link:doc.select("div.col-md-7 > p")){
                        descriptionbuilder.append(link.text()).append("\n");
//                        postDesc = link.text();

                    }
                } catch (IOException e) {

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        result.setText(builder.toString());
                        descriptiontext = descriptionbuilder.toString();
                    }
                });
            }
        }).start();


    }


}
