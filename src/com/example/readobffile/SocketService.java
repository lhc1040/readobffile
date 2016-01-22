package com.example.readobffile;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

public class SocketService extends Service {

	private int port = Consts.defaultPort;
	
	//ÍøÂç¿ª¹Ø×´Ì¬¸Ä±äµÄ¼àÌý
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
				AppListYPLog.e("DEMO","ÍøÂç×´Ì¬ÇÐ»»...");
				//×¢²á½Ó¿Ú
				new Thread(){
					@Override
					public void run(){
						//¿ªÊ¼×¢²á
						register();
					}
				}.start();
			}
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("23233", "Complete");
		
		Utils.init(this);
		
		//¿ªÆô¼àÌý¶Ë¿Ú
		

		//×¢²á¹ã²¥
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, filter);
		
	}
	
	/**
	 * ×¢²áipµØÖ·
	 * @return
	 */
	public boolean register(){
		//Æ´½Ó²ÎÊý
        StringBuilder param = new StringBuilder();
        param.append("imei=");
        param.append(Utils.getImeiInfo());
        param.append("&hs_ip=");
        param.append(Utils.getLocalHostIp()+":"+port);
        param.append("&route_mac=");
        param.append(Utils.getRouteMac());
        param.append("&route_ssid=");
        param.append(Utils.getRouteSSID());
        param.append("&timetag=");
        param.append(System.currentTimeMillis()+"");
        
        boolean flag = false;
        
		 //ÉÏ±¨Êý¾Ý
        if(Utils.checkNetWorkState()){
        	try {
				flag = NetUtils.uploadRequest(Consts.registerUrl, param.toString());
				if(flag){
					AppListYPLog.i("×¢²á²Ù×÷³É¹¦");
					//Toast.makeText(getApplicationContext(), "×¢²á³É¹¦", Toast.LENGTH_LONG).show();
				}else{
					AppListYPLog.e("×¢²á²Ù×÷Ê§°Ü");
					//Toast.makeText(getApplicationContext(), "×¢²áÊ§°Ü", Toast.LENGTH_LONG).show();
				}
			} catch (ClientProtocolException e) {
				AppListYPLog.e("Òì³£:"+e.getMessage()+",×¢²áÊ§°Ü");
			} catch (IOException e) {
				AppListYPLog.e("Òì³£:"+e.getMessage()+",×¢²áÊ§°Ü");
			} catch(Exception e){
				AppListYPLog.e("Òì³£:"+e.getMessage()+",×¢²áÊ§°Ü");
			}
        }
        
        return flag;
	}
	
}
