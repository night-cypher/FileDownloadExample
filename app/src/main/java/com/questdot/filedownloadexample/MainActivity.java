package com.questdot.filedownloadexample;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    long id;
    long idPro;
    private ProgressBar progressBar;
    private ListView mlistView;
    private SimpleAdapter adapter;
    private List<Map<String, String>> data ;
    DownloadManager downManager ;
    private DownLoadCompleteReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnDown).setOnClickListener(this);
        findViewById(R.id.btndisplay).setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);
        findViewById(R.id.btnDProgress).setOnClickListener(this);

        mlistView = (ListView)findViewById(R.id.mylist);

        downManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        progressBar = (ProgressBar) (findViewById(R.id.downProgress));

        data = new ArrayList<Map<String,String>>();
        adapter = new SimpleAdapter(this, data,
                R.layout.down_load_task_item,
                new String[]{"downid","title","address","status"},
                new int[]{R.id.downid,R.id.title,R.id.address,R.id.status});

        mlistView.setAdapter(adapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, String> map = data.get(position);



                downManager.remove(Long.valueOf(map.get("downid")));
                //adapter.notifyDataSetChanged();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        receiver = new DownLoadCompleteReceiver(this);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onClick(View v) {
        DownloadManager.Request request =
                new DownloadManager.Request(Uri.parse("http://www.stephaniequinn.com/Music/Allegro%20from%20Duet%20in%20C%20Major.mp3"));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("Download");
        request.setDescription("Your file is downloading...");
        request.setAllowedOverRoaming(false);

        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "mydown");
        switch (v.getId()) {
            case R.id.btnDown:

                id= downManager.enqueue(request);
                break;

            case R.id.btndisplay:
                data.clear();
                queryDownTask(downManager,DownloadManager.STATUS_FAILED);
                queryDownTask(downManager,DownloadManager.STATUS_PAUSED);
                queryDownTask(downManager,DownloadManager.STATUS_PENDING);
                queryDownTask(downManager,DownloadManager.STATUS_RUNNING);
                queryDownTask(downManager,DownloadManager.STATUS_SUCCESSFUL);
                adapter.notifyDataSetChanged();
                break;
            case R.id.btnDelete:
                downManager.remove(id);

                break;
            case R.id.btnDProgress:
                idPro= downManager.enqueue(request);

                ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
                ses.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        queryTaskByIdandUpdateView(idPro);
                    }
                }, 0, 2, TimeUnit.SECONDS);
                break;
            default:
                break;
        }
    }

    private void queryDownTask(DownloadManager downManager,int status) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(status);
        Cursor cursor= downManager.query(query);

        while(cursor.moveToNext()){
            String downId= cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
            String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            //String statuss = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            String size= cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            String sizeTotal = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            Map<String, String> map = new HashMap<String, String>();
            map.put("downid", downId);
            map.put("title", title);
            map.put("address", address);
            map.put("status", sizeTotal+":"+size);
            this.data.add(map);
        }
        cursor.close();
    }

    private void queryTaskByIdandUpdateView(long id){
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor= downManager.query(query);
        String size="0";
        String sizeTotal="0";
        if(cursor.moveToNext()){
            size= cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            sizeTotal = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        }
        cursor.close();
        progressBar.setMax(Integer.valueOf(sizeTotal));
        progressBar.setProgress(Integer.valueOf(size));

    }



    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(receiver);

        // Intent intent = new Intent(Intent.ACTION_VIEW);
        //   intent.setDataAndType(Uri.fromFile(new File(mUrl)),
        //           "application/vnd.android.package-archive");
    }




}
