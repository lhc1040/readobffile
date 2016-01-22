package com.example.readobffile;

import gnu.trove.list.array.TIntArrayList;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;
import android.content.ContextWrapper;
import net.osmand.IndexConstants;
import net.osmand.binary.BinaryMapIndexReader;
import net.osmand.binary.BinaryMapIndexReader.MapIndex;
import net.osmand.binary.BinaryMapIndexReader.SearchRequest;
import net.osmand.binary.BinaryMapIndexReader.TagValuePair;
import net.osmand.render.RenderingRuleSearchRequest;
import net.osmand.render.RenderingRulesStorage;
import net.osmand.util.MapUtils;
import net.osmand.binary.BinaryMapDataObject;
import net.osmand.plus.render.RendererRegistry;






public class MainActivity<OsmandApplication> extends ActionBarActivity {
	
	private int port = Consts.defaultPort;
	
	private Map<String, BinaryMapIndexReader> files = new ConcurrentHashMap<String, BinaryMapIndexReader>();
	private SearchRequest<BinaryMapDataObject> searchRequest;
	
	//MyHandler myHandler; 
	
	//private final OsmandApplication context;
	RendererRegistry rendererRegistry = new RendererRegistry();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.d("mytag", "MainActivity");
        HttpServer hs = new HttpServer();
		try{
			AppListYPLog.i("监听开启...");
			for(int i=0;i<Consts.portAry.length;i++){
				if(Utils.checkPort(Consts.portAry[i])){
					port = Consts.portAry[i];
					break;
				}
			}
			Log.d("mytag", "listening on port :"+port);
			hs.execute(port);
			//register();
		}catch(Exception e){
			AppListYPLog.e("异常:"+e.getMessage());
		}
		//final EditText et = (EditText)findViewById(R.id.editText1);
		Button btn = (Button)findViewById(R.id.start_button);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				//et.append("start server!\n");
				
			}
		});
		
		
		Message msg = new Message(); 
        Bundle b = new Bundle();// 存放数据 
        b.putString("color","this is test!\n"); 
        msg.setData(b); 

        MyHandler myHandler = new MyHandler();
        myHandler.sendMessage(msg); // 向Handler发送消息，更新UI 
        }
    class MyHandler extends Handler { 
        public MyHandler() { 
        } 
 
        public MyHandler(Looper L) { 
            super(L); 
        } 
 
        // 子类必须重写此方法，接受数据 
        @Override 
        public void handleMessage(Message msg) { 
            // TODO Auto-generated method stub 
            super.handleMessage(msg); 
            // 此处可以更新UI 
            Bundle b = msg.getData(); 
            String color = b.getString("color"); 
            EditText et = (EditText)findViewById(R.id.editText1);
            et.append(color); 
 
        } 
    }
    
  


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
