package com.example.android.unyapps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PdfViewer extends AppCompatActivity {
    private String url = "https://www.uny.ac.id/sites/www.uny.ac.id/files/Pengumuman%20Tes%20ProTEFL%20PPs%20angkatan%202015.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //Intent intent = getIntent();

        //this.url = intent.getStringExtra("url");

        PDFView pdf = (PDFView) findViewById(R.id.pdfViewer);

        String fileName = Uri.parse(url).getLastPathSegment();
        fileName = fileName.contains(".pdf") ? fileName : fileName + ".pdf";


        getSupportActionBar().setTitle(fileName);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading File...");

        new FileDownloader(getBaseContext(), dialog, fileName, pdf).execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdf_viewer, menu);
        return true;
    }
}

class FileDownloader extends AsyncTask<String, String, String> {
    private Context con;
    private ProgressDialog dialog;
    private String fileName;
    private PDFView pdf;

    public FileDownloader(Context con, ProgressDialog dialog, String fileName, PDFView pdf){
        this.con = con;
        this.dialog = dialog;
        this.fileName = fileName;
        this.pdf = pdf;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected String doInBackground(String... urls) {
        try{
            URL url = new URL(urls[0]);
            InputStream in = new BufferedInputStream(url.openStream());

            OutputStream out = new FileOutputStream(con.getCacheDir() + "/" + fileName);

            byte[] data = new byte[1024];
            int count = 0;

            while((count = in.read(data)) != -1){
                out.write(data, 0, count);
            }
            out.flush();
            out.close();
            in.close();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return con.getCacheDir()+"/"+fileName;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("error bang", s);
        Log.d("ada bos", new File(s).exists() ? "ada" : "nope");
        pdf.fromFile(new File(s)).load();


        dialog.dismiss();
    }
}

