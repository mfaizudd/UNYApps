package com.example.android.unyapps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
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

public class IndexBeritaActivity extends AppCompatActivity {

    final int PAGE_SIZE = 20;
    String url = "https://uny.ac.id/index-berita";
    ProgressDialog mProgressDialog;
    String postTitle[] = new String[PAGE_SIZE];
    Button loadButton;
    String postLink[] = new String[PAGE_SIZE];
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_berita);

        sp = getSharedPreferences("com.example.android.unyapps", MODE_PRIVATE);

        new Title().execute();
    }

    private class Title extends AsyncTask<Void, Void, Void> {
        String title;
        Bitmap bitmap;
        Bitmap[] postImage = new Bitmap[PAGE_SIZE];
        String[] postContent = new String[PAGE_SIZE];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(IndexBeritaActivity.this);
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
                // Get the html document title
                Elements img = document.select("a[title=Home] img[src]");
                Elements epostTitle = document.select("strong[class=field-content] a[href]");
                Elements ePostImage = document.select("div[class=views-field views-field-field-image] div[class=field-content] img[src]");
                Elements eContents = document.select("div[class=views-field views-field-body] div[class=field-content] p");
                int index = 0;
                for(Element item : epostTitle) {
                    postTitle[index] = item.ownText();
                    postLink[index] = item.attr("href");
                    index++;
                    if(index>=PAGE_SIZE) break;
                }
                index = 0;
                for(Element item : ePostImage) {
                    String src = item.attr("src");
                    InputStream srcinput = new java.net.URL(src).openStream();
                    postImage[index] = BitmapFactory.decodeStream(srcinput);
                    index++;
                    if(index>=PAGE_SIZE) break;
                }
                index = 0;
                for (Element item: eContents) {
                    postContent[index] = item.ownText();
                    index++;
                    if(index>=PAGE_SIZE) break;
                }
                String imgSrc = img.attr("src");
                InputStream input = new java.net.URL(imgSrc).openStream();
                bitmap = BitmapFactory.decodeStream(input);
                title = document.title();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            // Set title into TextView
            TextView titleText = findViewById(R.id.titleText);
            TextView postTitleText = findViewById(R.id.postTitleText);
            LinearLayout newsView = findViewById(R.id.newsView);
            int index = 0;
            for(String item : postTitle) {
                LinearLayout newsCard = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams matchAndWrap = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                newsCard.setLayoutParams(matchAndWrap);
                newsCard.setClickable(true);
                newsCard.setFocusable(true);
                newsCard.setOrientation(LinearLayout.VERTICAL);
                newsCard.setGravity(Gravity.CENTER);

                LinearLayout newsCardContainer = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                containerParams.setMargins(0,getDP(8), 0, getDP(8));
                newsCardContainer.setLayoutParams(containerParams);
                newsCardContainer.setBackgroundColor(getResources().getColor(R.color.white));
                newsCardContainer.addView(newsCard);

                ImageView cardImage = new ImageView(getApplicationContext());
                int[] attrs = new int[] {R.attr.selectableItemBackground};
                TypedArray ta = obtainStyledAttributes(attrs);
                Drawable cardDrawable = ta.getDrawable(0);
                ta.recycle();
                newsCard.setBackgroundDrawable(cardDrawable);
                LinearLayout.LayoutParams cardImageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardImageParams.height = getDP(250);
                cardImage.setLayoutParams(cardImageParams);
                cardImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                cardImage.setImageBitmap(postImage[index]);
                newsCard.addView(cardImage);

                LinearLayout newsDescription = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                newsDescription.setLayoutParams(descriptionParams);
                newsDescription.setPadding(getDP(8),getDP(8),getDP(8),getDP(8));
                newsDescription.setOrientation(LinearLayout.VERTICAL);
                newsCard.addView(newsDescription);

                TextView newsTitle = new TextView(getApplicationContext());
                newsTitle.setText(item);
                newsTitle.setLayoutParams(matchAndWrap);
                newsTitle.setTextColor(getResources().getColor(R.color.black));
                newsDescription.addView(newsTitle);

                TextView newsContent = new TextView(getApplicationContext());
                newsContent.setLayoutParams(matchAndWrap);
                newsContent.setText(postContent[index]);
                newsDescription.addView(newsContent);

                final int indexFinal = index;
                final String itemFinal = item;

                newsCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent postView = new Intent(IndexBeritaActivity.this, PostViewActivity.class);
                        postView.putExtra("postLink", postLink[indexFinal]);
                        postView.putExtra("postTitle", itemFinal);
                        startActivity(postView);
                    }
                });
//                Button mButton = new Button(getApplicationContext());
//                mButton.setText(item);
//                mButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent postView = new Intent(IndexBeritaActivity.this, PostViewActivity.class);
//                        postView.putExtra("postLink", postLink[indexFinal]);
//                        postView.putExtra("postTitle", itemFinal);
//                        startActivity(postView);
//                    }
//                });
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                newsView.addView(mButton, lp);
                newsView.addView(newsCardContainer);
                index++;
            }
            titleText.setText(title);
            ImageView logoImage = findViewById(R.id.logoImage);
            logoImage.setImageBitmap(bitmap);
            mProgressDialog.dismiss();
        }
    }
    int getDP(int dps){
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }
}
