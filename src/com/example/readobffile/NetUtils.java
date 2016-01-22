package com.example.readobffile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public final class NetUtils {
	
	/**
	 * �ϴ�����
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static String postData(String url, HashMap<String, String> param) {

		HttpPost httpPost = new HttpPost(url);
		// ����HTTP POST�������������NameValuePair����
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (param != null) {
			Set<Entry<String, String>> set = param.entrySet();
			Iterator<Entry<String, String>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> tempEntry = iterator.next();
				params.add(new BasicNameValuePair(tempEntry.getKey(), tempEntry.getValue()));
			}
		} else {
			AppListYPLog.e("DEMO:", "�ϴ���������");
			return null;
		}
		HttpResponse httpResponse = null;
		try {
			// ����httpPost�������
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpClient httpClient = new DefaultHttpClient();
			// ����ʱ
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
			// ��ȡ��ʱ
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
			httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// ��������ʹ��getEntity������÷��ؽ��
				String result = EntityUtils.toString(httpResponse.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			AppListYPLog.e("�쳣:" + e.getMessage());
			return null;
		} catch (IOException e) {
			AppListYPLog.e("�쳣:" + e.getMessage());
			return null;
		}
		return null;
	}
	
	/**
	 * @return ͨ��Request��ʽ�ع�
	 */
	public static boolean uploadRequest(String baseUrl, String param) throws ClientProtocolException, IOException {
		HttpGet httpGet = null;
		if (param != null && !"".equals(param)) {
			httpGet = new HttpGet(baseUrl + "?" + param);
		} else {
			httpGet = new HttpGet(baseUrl);
		}
		AppListYPLog.e("URL:"+httpGet.getURI()+"");
		HttpParams params = httpGet.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSoTimeout(params, 3000);
		HttpClient httpClient = new DefaultHttpClient(params);
		HttpResponse response = httpClient.execute(httpGet);
		int statusCode = response.getStatusLine().getStatusCode();
		AppListYPLog.e("��Ӧ��:" + statusCode);
		if (statusCode < 300 && statusCode >= 200) {
			return true;
		}
		return false;
	}


}
