package com.example.android.unyapps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

public class PostViewActivity extends AppCompatActivity {
    //WEDAN
    ProgressDialog mProgressDialog;
    SharedPreferences sp;
    String url = "";
    String title = "";
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);


        tv = findViewById(R.id.postViewContent);

        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("postTitle", "");
        url = "http://www.uny.ac.id" + bundle.getString("postLink", "");
        new ProcessData().execute();
    }

    private class ProcessData extends AsyncTask<Void, Void, Void> {
        String title;
        Bitmap bitmap;
        String content="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(PostViewActivity.this);
            mProgressDialog.setTitle("UNY Apps");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(url).get();
                Elements epostTitle = document.select("h1[class=title] a[href]");
                title = epostTitle.text();
                Elements img = document.select("a[class=colorbox] img[src]");
                InputStream is = new java.net.URL(img.attr("src")).openStream();
                bitmap = BitmapFactory.decodeStream(is);
                Elements econtent = document.select("div[class=field-item even] p");
                for(Element item : econtent) {
                    content += "\t\t\t"+item.text()+"\n\n";
                }
                // Get the html document title
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            TextView titleTextView = findViewById(R.id.postViewTitleText);
            titleTextView.setText(title);
            ImageView postImage = findViewById(R.id.postImage);
            postImage.setImageBitmap(bitmap);
            TextView textContent = findViewById(R.id.postViewContent);
            textContent.setText(content);
            mProgressDialog.dismiss();
        }
    }
}
