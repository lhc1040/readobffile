package com.example.readobffile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 工具类
 * 
 * @author weijiang204321
 * 
 */
public class Utils {

	public static Context mContext;

	public static void init(Context context) {
		mContext = context;
	}

	/**
	 * 
	 * @param str
	 * @return 字符串是否为空
	 */
	public static boolean isNotEmpty(String str) {
		if (str != null && !"".equals(str)) {
			return true;
		}
		return false;
	}


	/**
	 * 获取手机的Mac地址
	 * 
	 * @return
	 */
	public static String getMacAddress() {
		String result = "";
		try {
			WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			result = wifiInfo.getMacAddress();
		} catch (Exception e) {
			result = "";
		}
		return result;
	}

	/**
	 * 获取手机的imei
	 * 
	 * @return
	 */
	public static String getImeiInfo() {
		try {
			TelephonyManager mTm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			return mTm.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 获取手机的imsi
	 * 
	 * @return
	 */
	public static String getImsiInfo() {
		try {
			String imsi = "";
			TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			if (telephonyManager != null) {
				imsi = telephonyManager.getSubscriberId();
			}
			if (TextUtils.isEmpty(imsi)) {
				imsi = "UNKNOWN";
			}
			return imsi;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 获取手机型号
	 * 
	 * @return
	 */
	public static String getTypeInfo() {
		return android.os.Build.MODEL; // 手机型号
	}

	/**
	 * 获取手机系统版本
	 * 
	 * @return
	 */
	public static String getOsVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 获取路由器的SSID
	 * 
	 * @return
	 */
	public static String getRouteSSID() {
		try {
			WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wm.getConnectionInfo();
			if(info.getSSID().contains("<")){
				return "";
			}else{
				return info.getSSID().replace("\"","") + "";
			}
		} catch (Exception e) {
			AppListYPLog.e("异常:" + e.getMessage() + ",获取SSID失败!");
			return "";
		}
	}

	/**
	 * 获取路由器的Mac地址
	 * 
	 * @return
	 */
	public static String getRouteMac() {
		try {
			WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wm.getConnectionInfo();
			if(info.getBSSID() == null){
				return "";
			}else{
				return info.getBSSID() + "";
			}
		} catch (Exception e) {
			AppListYPLog.e("异常:" + e.getMessage() + ",获取Mac地址失败!");
			return "";
		}
	}
	
	/**
	 * 解析uri参数
	 * @param uri
	 * @return
	 */
	public static Map<String,String> getParam(String uri){
		Map<String,String> params = new HashMap<String,String>();
		try{
			if(isNotEmpty(uri)){
				String subStr = uri.substring(uri.indexOf("?")+1);
				String[] ary = subStr.split("&");
				for(int i=0;i<ary.length;i++){
					String[] temp = ary[i].split("=");
					if(temp.length<2){
						params.put(temp[0], "");
					}else{
						params.put(temp[0], temp[1]);
					}
					
				}
				return params;
			}else{
				return null;
			}
		}catch(Exception e){
			return null;
		}
		
	}

	/**
	 * 判断网络
	 * @param ctx
	 * @return
	 */
	public static boolean checkNetWorkState() {
		boolean isnetwork = false;
		ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();

		if (info != null && (info.isAvailable() || info.isConnected())) {
			isnetwork = true;
		}
		return isnetwork;
	}

	
	/**
	 * 获取本机的ip
	 * @return
	 */
    public static String getLocalHostIp(){
        String ipaddress = "";
        try{
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()){
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()){
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()&& InetAddressUtils.isIPv4Address(ip.getHostAddress())){
                        return ip.getHostAddress();
                    }
                }
            }
        }catch (SocketException e){
            AppListYPLog.e("feige", "获取本地ip地址失败");
        }
        return ipaddress;
    }
    
    /**
     * 检查端口port是否被占用了
     * @param port
     * @return
     */
    public static boolean checkPort(int port){
		try{
			InetAddress theAddress=InetAddress.getByName("127.0.0.1");
			try {
				Socket theSocket = new Socket(theAddress,port);
				theSocket.close();
				theSocket = null;
				theAddress = null;
				return false;
			}catch (IOException e) {
				AppListYPLog.e("异常:"+e.getMessage()+"检查端口号是否被占用");
			}catch(Exception e){
				AppListYPLog.e("异常:"+e.getMessage()+"检查端口号是否被占用");
			}
		}catch(UnknownHostException e) {
			AppListYPLog.e("异常:"+e.getMessage()+"检查端口号是否被占用");
		}
		return true;
    }
	
}
