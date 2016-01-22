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
	
	//���翪��״̬�ı�ļ���
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
				AppListYPLog.e("DEMO","����״̬�л�...");
				//ע��ӿ�
				new Thread(){
					@Override
					public void run(){
						//��ʼע��
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
		
		//���������˿�
		

		//ע��㲥
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, filter);
		
	}
	
	/**
	 * ע��ip��ַ
	 * @return
	 */
	public boolean register(){
		//ƴ�Ӳ���
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
        
		 //�ϱ�����
        if(Utils.checkNetWorkState()){
        	try {
				flag = NetUtils.uploadRequest(Consts.registerUrl, param.toString());
				if(flag){
					AppListYPLog.i("ע������ɹ�");
					//Toast.makeText(getApplicationContext(), "ע��ɹ�", Toast.LENGTH_LONG).show();
				}else{
					AppListYPLog.e("ע�����ʧ��");
					//Toast.makeText(getApplicationContext(), "ע��ʧ��", Toast.LENGTH_LONG).show();
				}
			} catch (ClientProtocolException e) {
				AppListYPLog.e("�쳣:"+e.getMessage()+",ע��ʧ��");
			} catch (IOException e) {
				AppListYPLog.e("�쳣:"+e.getMessage()+",ע��ʧ��");
			} catch(Exception e){
				AppListYPLog.e("�쳣:"+e.getMessage()+",ע��ʧ��");
			}
        }
        
        return flag;
	}
	
}
