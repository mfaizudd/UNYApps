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

public class IndexPengumumanActivity extends AppCompatActivity {

    final int PAGE_SIZE = 20;
    String url = "https://www.uny.ac.id/index-pengumuman";
    ProgressDialog mProgressDialog;
    String postTitle[] = new String[PAGE_SIZE];
    String postLink[] = new String[PAGE_SIZE];
    String postDate[] = new String[PAGE_SIZE];
    SharedPreferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_pengumuman);

        Button buttonBerita = findViewById(R.id.buttonBerita);
        buttonBerita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IndexPengumumanActivity.this, IndexBeritaActivity.class);
                startActivity(i);
                finish();
            }
        });

       new Title().execute();

    }

    private class Title extends AsyncTask<Void, Void, Void> {
        String title;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(IndexPengumumanActivity.this);
            mProgressDialog.setTitle("Pengumuman");
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
                Elements epostTitle = document.select("strong a[href]");
                Elements epostDate = document.select("td[class=views-field views-field-created]");
                int index = 0;
                for(Element item : epostTitle) {
                    postTitle[index] = item.ownText();
                    postLink[index] = item.attr("href");
                    index++;
                    if(index>=PAGE_SIZE) break;
                }
                index = 0;
                for(Element item : epostDate) {
                    postDate[index] = item.ownText();
                    index++;
                    if(index>=PAGE_SIZE) break;
                }
                title = document.title();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            TextView titleText = findViewById(R.id.titleNotice);
            TextView postTitleText = findViewById(R.id.noticeTitle);
            TextView postDateText = findViewById(R.id.noticeDate);

            titleText.setText(title);
            LinearLayout noticeView = findViewById(R.id.noticeView);
            int index = 0;
            for(String item : postTitle) {
                LinearLayout noticeCard = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams matchAndWrap = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                noticeCard.setLayoutParams(matchAndWrap);
                noticeCard.setClickable(true);
                noticeCard.setFocusable(true);
                noticeCard.setOrientation(LinearLayout.VERTICAL);
                noticeCard.setGravity(Gravity.CENTER);

                LinearLayout noticeCardContainer = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                containerParams.setMargins(0,getDP(4), 0, getDP(4));
                noticeCardContainer.setLayoutParams(containerParams);
                noticeCardContainer.setBackgroundColor(getResources().getColor(R.color.white));
                noticeCardContainer.addView(noticeCard);

                LinearLayout noticeDescription = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                noticeDescription.setLayoutParams(descriptionParams);
                noticeDescription.setPadding(getDP(8),getDP(8),getDP(8),getDP(8));
                noticeDescription.setOrientation(LinearLayout.VERTICAL);
                noticeCard.addView(noticeDescription);

                TextView noticeDate = new TextView(getApplicationContext());
                noticeDate.setLayoutParams(matchAndWrap);
                noticeDate.setText(postDate[index]);
                noticeDescription.addView(noticeDate);

                TextView noticeTitle = new TextView(getApplicationContext());
                noticeTitle.setText(item);
                noticeTitle.setLayoutParams(matchAndWrap);
                noticeTitle.setTextColor(getResources().getColor(R.color.black));
                noticeDescription.addView(noticeTitle);


                final int indexFinal = index;
                final String itemFinal = item;

                noticeCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent postView = new Intent(IndexPengumumanActivity.this, NoticeViewActivity.class);
                        postView.putExtra("postLink", postLink[indexFinal]);
                        postView.putExtra("postTitle", itemFinal);
                        startActivity(postView);
                    }
                });
////                Button mButton = new Button(getApplicationContext());
////                mButton.setText(item);
////                mButton.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        Intent postView = new Intent(IndexBeritaActivity.this, PostViewActivity.class);
////                        postView.putExtra("postLink", postLink[indexFinal]);
////                        postView.putExtra("postTitle", itemFinal);
////                        startActivity(postView);
////                    }
////                });
////                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                newsView.addView(mButton, lp);
                noticeView.addView(noticeCardContainer);
                index++;

            }
            mProgressDialog.dismiss();
        }
    }

    int getDP(int dps){
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }
}
