package com.example.nixo.videostudy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView ;
    ArrayAdapter<String> arrayAdapter;
    List<String> list = new ArrayList<>();
    public static final int MY_READ_EXTERNAL_STORAGE = 0X111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.menu_listview);
        list.add("Intent方法");
        list.add("MediaManager");
        list.add("SurfaceView+MediaView");
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //Intent
                        checkPermission();
                        break;
                    case 1:
                        //MediaMannager
                        Main2Activity.start(MainActivity.this);
                        break;
                    case 2:
                        //SurfaceView+MediaView;
                        MediaSurfaceActivity.start(MainActivity.this);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    private void checkPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_READ_EXTERNAL_STORAGE);
            }else{
            playVideoWithIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    playVideoWithIntent();
                } else {
                    Toast.makeText(this, "该功能需要此权限", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void playVideoWithIntent(){
        File file = new File(Environment.getExternalStorageDirectory() + "/baka.mp4");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT >= 24){
            Uri contentUri = FileProvider.getUriForFile(this, "com.example.nixo.videostudy.fileprovider", file);
            Log.i("Nixo",""+contentUri);
            Log.i("Nixo", ""+contentUri);
            intent.setDataAndType(contentUri,"video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }else{
            intent.setDataAndType(Uri.fromFile(file),"video/*");
        }
        startActivity(intent);

    }

}
