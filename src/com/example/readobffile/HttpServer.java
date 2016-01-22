package com.example.readobffile;

import gnu.trove.list.array.TIntArrayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;  
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;  
import java.io.RandomAccessFile;
import java.net.ServerSocket;  
import java.net.Socket;  
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;  
  
  



import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.osmand.IndexConstants;
import net.osmand.binary.BinaryMapDataObject;
import net.osmand.binary.BinaryMapIndexReader;
import net.osmand.binary.BinaryMapIndexReader.MapIndex;
import net.osmand.binary.BinaryMapIndexReader.SearchRequest;
import net.osmand.binary.BinaryMapIndexReader.TagValuePair;
import net.osmand.plus.render.RendererRegistry;
import net.osmand.render.RenderingRuleSearchRequest;
import net.osmand.render.RenderingRulesStorage;
import net.osmand.util.MapUtils;

import org.apache.http.ConnectionClosedException;  
import org.apache.http.HttpException;  
import org.apache.http.HttpRequest;  
import org.apache.http.HttpResponse;  
import org.apache.http.HttpResponseInterceptor;  
import org.apache.http.HttpServerConnection;  
import org.apache.http.HttpStatus;  
import org.apache.http.MethodNotSupportedException;  
import org.apache.http.entity.StringEntity;  
import org.apache.http.impl.DefaultConnectionReuseStrategy;  
import org.apache.http.impl.DefaultHttpResponseFactory;  
import org.apache.http.impl.DefaultHttpServerConnection;  
import org.apache.http.params.BasicHttpParams;  
import org.apache.http.params.CoreConnectionPNames;  
import org.apache.http.params.CoreProtocolPNames;  
import org.apache.http.params.HttpParams;  
import org.apache.http.protocol.BasicHttpContext;  
import org.apache.http.protocol.HttpContext;  
import org.apache.http.protocol.HttpProcessor;  
import org.apache.http.protocol.HttpRequestHandler;  
import org.apache.http.protocol.HttpRequestHandlerRegistry;  
import org.apache.http.protocol.HttpService;  
import org.apache.http.protocol.ImmutableHttpProcessor;  
import org.apache.http.protocol.ResponseConnControl;  
import org.apache.http.protocol.ResponseContent;  
import org.apache.http.protocol.ResponseDate;  
import org.apache.http.protocol.ResponseServer;  

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

public class HttpServer {
	
	private static Map<String, BinaryMapIndexReader> files = new ConcurrentHashMap<String, BinaryMapIndexReader>();
	private static SearchRequest<BinaryMapDataObject> searchRequest;
	
	//private final OsmandApplication context;
	static RendererRegistry rendererRegistry = new RendererRegistry();
	
	//获取来访者的ip地址
	private static String ipAddress = "";
	
	private static int tilex ;
	private static int tiley ;
	private static int tilezoom ;
	private static String filepath = Environment.getExternalStorageDirectory()+"/lhc/";
	
	private static int loopnum =1 ;
	
	//开启监听
    public void execute(int port) throws Exception{
    	Log.d("mytag", "1 :"+port);
    	Thread t = new RequestListenerThread(port);
    	Log.d("mytag", "2 :"+port);
        t.setDaemon(false);  
        Log.d("mytag", "3 :"+port);
        t.start(); 
    }
    
    //自定义HttpRequest的处理类，我们需要继承HttpRequestHandler接口
    static class WebServiceHandler implements HttpRequestHandler {  
  
        public WebServiceHandler() {  
            super();  
        }  
        
        //在这个方法中我们就可以处理请求的业务逻辑
        public void handle(final HttpRequest request,  
                final HttpResponse response, final HttpContext context)  
                throws HttpException, IOException {
        	
        	Log.d("mytag", loopnum+" 次请求处理");
        	
        	WriteFileThread loop1=new WriteFileThread(loopnum+" 次请求处理\n",filepath+"loop_result.txt");
			Thread loop11=new Thread(loop1);
			loop11.start(); 
        	loopnum++;
        	//获取请求方法
            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);  
            //获取请求的uri  
            String target = request.getRequestLine().getUri();
            
            WriteFileThread loop5=new WriteFileThread("target: "+target+"\n",filepath+"target.txt");
 			Thread loop55=new Thread(loop5);
 			loop55.start();
            
            if(target ==" "||target =="/"){
            	Log.d("mytag", "canshu buhefa");
            }
          //test
            //String temp = "/v/2/4/7";
            
//    		int first = target.indexOf("/");
//    		//int last = target.indexOf(".");
//    		String subString = target.substring(first+2);
//    		//String subString = target.substring(first, last);
//    		int secend = subString.indexOf("/", first+1);
//    		int third = subString.indexOf("/", secend+1);
//    		
//    		String num1 = subString.substring(first+1, secend);
//    		String num2 = subString.substring(secend+1, third);
//    		String num3 = subString.substring(third+1);
//    		
//    		int number1 = Integer.valueOf(num1).intValue();
//    		int number2 = Integer.valueOf(num2).intValue();
//    		int number3 = Integer.valueOf(num3).intValue();
            /*
             * number1-----Z
             * number2-----X
             * number3-----Y
             */
            
            int first = target.indexOf("/");
    		
    		int sec = target.indexOf("/", first+1);
    		int thir = target.indexOf("/", sec+1);
    		int four = target.indexOf("/", thir+1);
    		
    		
    		String numz = target.substring(sec+1, thir);
    		String numx = target.substring(thir+1, four);
    		String numy = target.substring(four+1);
    		
    		
    		int numberx = Integer.valueOf(numx).intValue();
    		int numbery = Integer.valueOf(numy).intValue();
    		int numberz = Integer.valueOf(numz).intValue();
            //demo
            tilex = numberx;
            tiley = numbery;
            tilezoom = numberz;
           // array = {tilex,tiley,tilezoom};
            Log.d("mytag", "method:"+method+"  target: "+target);
            
            
            //get请求方式(我们这里只处理get方式)
            if (method.equals("GET") ) {  
            	
            	String sdDir = Environment.getExternalStorageDirectory()+"/lhc/";//鑾峰彇璺熺洰褰?            	
            	
                 
                 //create geojson data
                 StringBuffer json_result = new StringBuffer();
                 
                 String head = "{\"type\": \"FeatureCollection\",\"totalFeatures\": 130,\"features\": [";
                 //String body = "{\"type\": \"Feature\",\"id\": \"forywli.fid-5624317b_14fafc3a71f_-7a63\",\"geometry\": {\"type\": \"MultiLineString\",\"coordinates\": [[[116.1253049,39.7356748],[116.1250359,39.7355164],[116.1254782,39.7350788],[116.1257532,39.7352388]]]},\"geometry_name\": \"st_transform\",\"properties\": {\"name\": null,\"osm_id\": 183596047,\"highway\": \"footway\",\"ref\": null,\"oneway\": null,\"bridge\": \"yes\",\"layer\": \"1\",\"railway\": null,\"service\": null,\"building\": null,\"water\": null,\"leisure\": null,\"sport\": null,\"amenity\": null,\"operator\": null,\"natural\": null}},";
                 String tail = "],\"crs\": {\"type\": \"name\",\"properties\": {\"name\": \"urn:ogc:def:crs:EPSG::900913\"}}}";
                 //构造头部
                 json_result.append(head);
                 
                 //read obf file
                 
                 double top = tile2lat(tiley, tilezoom);
                 double bottom = tile2lat(tiley + 1, tilezoom);
                 double left = tile2lon(tilex, tilezoom);
                 double right = tile2lon(tilex + 1, tilezoom);
                 
                 long timenow = System.currentTimeMillis();
                WriteFileThread loop3=new WriteFileThread("start_time: "+new Date(timenow)+"\n",filepath+"loop_result.txt");
     			Thread loop33=new Thread(loop3);
     			loop33.start();
                 Log.d("mytag", "mark:1");
                 startApplicationBackground();
                 
                 //创建自己的需求范围
                 Log.d("mytag", "mark:2");
                int leftX = MapUtils.get31TileNumberX(left);
         		int rightX = MapUtils.get31TileNumberX(right);
         		int bottomY = MapUtils.get31TileNumberY(bottom);
         		int topY = MapUtils.get31TileNumberY(top);
         		
         		
         		final int zoom = tilezoom;
         		 Log.d("mytag", "mark:3");
         		//OsmandApplication app = ((OsmandApplication) context.getApplicationContext());
         		//boolean nightMode = app.getDaynightHelper().isNightMode();
         		// boolean moreDetail = prefs.SHOW_MORE_MAP_DETAIL.get();
         		//OsmandApplication app = (OsmandApplication) rendererRegistry.getCurrentSelectedRenderer();
         		//RenderingRulesStorage storage = app.getRendererRegistry().getCurrentSelectedRenderer();
         		
         		RenderingRulesStorage storage = rendererRegistry.getCurrentSelectedRenderer();
         		final RenderingRuleSearchRequest renderingReq = new RenderingRuleSearchRequest(storage);
         		 Log.d("mytag", "mark:4");
         		
         		BinaryMapIndexReader.SearchFilter searchFilter = new BinaryMapIndexReader.SearchFilter() {
         			@Override
         			public boolean accept(TIntArrayList types, BinaryMapIndexReader.MapIndex root) {
         				for (int j = 0; j < types.size(); j++) {
         					int type = types.get(j);
         					TagValuePair pair = root.decodeType(type);
         					if (pair != null) {
         						// TODO is it fast enough ?
         						for (int i = 1; i <= 3; i++) {
         							renderingReq.setIntFilter(renderingReq.ALL.R_MINZOOM, zoom);
         							renderingReq.setStringFilter(renderingReq.ALL.R_TAG, pair.tag);
         							renderingReq.setStringFilter(renderingReq.ALL.R_VALUE, pair.value);
         							if (renderingReq.search(i, false)) {
         								return true;
         							}
         						}
         						renderingReq.setStringFilter(renderingReq.ALL.R_TAG, pair.tag);
         						renderingReq.setStringFilter(renderingReq.ALL.R_VALUE, pair.value);
         						if (renderingReq.search(RenderingRulesStorage.TEXT_RULES, false)) {
         							return true;
         						}
         					}
         				}
         				return false;
         			}

         		};
         		 Log.d("mytag", "mark:5");
                 MapIndex mi = null;
         		searchRequest = BinaryMapIndexReader.buildSearchRequest(leftX, rightX, topY, bottomY, zoom, searchFilter);
         		
         		 Log.d("mytag", "mark:6");
         		 List<BinaryMapDataObject> res = null;
         		for (BinaryMapIndexReader c : files.values()) {
//         			for(int filenum = 0;filenum<files.size();filenum++){
//         				BinaryMapIndexReader o = files.values().;
//         				
//         			}
         			files.values().remove(c);
         			boolean conti = true;
         			boolean basemap = c.isBasemap();
         			Log.d("mytag", "mark:66667");
         			searchRequest.clearSearchResults();
         			
         			try {
         				res = c.searchMapIndex(searchRequest);
         				Log.d("mytag", "mark:7 "+res.size());
         	    		
         	    		//for(BinaryMapDataObject o : res){
         	    			for(int loop2 =0;loop2<res.size();loop2++){
         	    				
         	    				BinaryMapDataObject o = res.get(loop2);
         	    			
         	    			Log.d("lhc", "new object!");
         	    			
         	    			
         	    			// 
         	    			String obj_type = null;
         	    			String propertity = null;
         	    			boolean legal = true;
         	    			for (int j = 0; j < o.getTypes().length; j++) {
         						int wholeType = o.getTypes()[j];
         						int layer = 0;
         						if (o.getPointsLength() > 1) {
         							layer = o.getSimpleLayer();
         						}

         						TagValuePair pair = o.getMapIndex().decodeType(wholeType);
         						
         					
         						if (pair != null) {
         							renderingReq.setTagValueZoomLayer(pair.tag, pair.value, zoom, layer, o);
         							
         							Log.d("lhc", "tag: "+pair.tag+" value: "+pair.value);
//         							WriteFileThread m2=new WriteFileThread(json_result.toString(),filepath+"pair_value.txt");
//         							Thread t2=new Thread(m2);
//         							t2.start();  
         							if(pair.tag=="admin_level"&&zoom>6){
         								legal = false;
         								break;
         							}
         							if(pair.value==null){
         								if(pair.tag=="building"){
         									propertity = "\""+pair.tag+"\": "+"\""+"yes"+"\""; 
         								}else{
         									propertity = "\""+pair.tag+"\": "+"null"; 
         								}
         								
         							}else{
         								propertity = "\""+pair.tag+"\": "+"\""+pair.value+"\" "; 
         							}
         							
         							renderingReq.setBooleanFilter(renderingReq.ALL.R_AREA, o.isArea());
         							renderingReq.setBooleanFilter(renderingReq.ALL.R_POINT, o.getPointsLength() == 1);
         							renderingReq.setBooleanFilter(renderingReq.ALL.R_CYCLE, o.isCycle());
         							if (renderingReq.search(RenderingRulesStorage.ORDER_RULES)) {
         								int objectType = renderingReq.getIntPropertyValue(renderingReq.ALL.R_OBJECT_TYPE);
         								
         								int order = renderingReq.getIntPropertyValue(renderingReq.ALL.R_ORDER);
         								
         																										
         								if(objectType == 3) {
         									obj_type = "Polygon";
         									
         									Log.d("lhc", "MultiPolygon");
         									
         									break;
         									
         									
         								} else if(objectType == 1) {
         									
         									
         									obj_type = "Point";
         									Log.d("lhc", "Point");
         									if(pair.tag.equals("admin_level"))
         									{
         										
         									}
         									break;
         									
         				
         								} else {
         									Log.d("lhc", "MultiLineString");
         									obj_type = "MultiLineString";
         									break;
         									
         								}
         								
         							}

         						}
         					}
         	    			
         	    			if(legal){
         	    				
         	    				//对象范围信息的构造过程
             	    			DecimalFormat df = new DecimalFormat("######0.0000000"); 
             	    			StringBuffer coordinate = new StringBuffer();
             	    			coordinate.append("[[");
             	    			int coordinates[] = o.coordinates;
             	    			Log.d("mytag", "coordinates.length"+coordinates.length);
             	    			for(int loop = 0;loop<coordinates.length;loop=loop+2){
             	    				if(loop+2==coordinates.length){
             	    					coordinate.append("["+df.format(pro2lon(coordinates[loop]))+","+df.format(pro2lat(coordinates[loop+1]))+"]");
             	    				}else{
             	    					coordinate.append("["+df.format(pro2lon(coordinates[loop]))+","+df.format(pro2lat(coordinates[loop+1]))+"],");
             	    				}
             	    				
             	    			}
             	    			coordinate.append("]]");
             	    			long id = o.getId();
             	    			String temp1 = o.getName();
             	    			json_result.append("{\"type\": \"Feature\",");
             	    			json_result.append("\"id\":"+ "\""+id+"\",");
             	    			json_result.append("\"geometry\": {\"type\": "+"\""+obj_type+"\",");
             	    			json_result.append("\"coordinates\":"+coordinate.toString()+"},");
             	    			json_result.append("\"geometry_name\": \"st_transform\",");
             	    			json_result.append("\"properties\":");
             	    			if(temp1==""){
             	    				json_result.append("{\"name\": "+"null"+",\"osm_id\": "+id+","+propertity+"}");
             	    				
             	    			}else{
             	    				json_result.append("{\"name\": "+"\""+temp1+"\""+",\"osm_id\": "+id+","+propertity+"}");
             	    			}
             	    			
             	    			if(loop2+1==res.size()&&files.values().isEmpty()){
             	    				json_result.append("}");
             	    			}else{
             	    				json_result.append("},");
             	    			}
         	    				
         	    			}else{
         	    				Log.d("mytag", "object is illegal!");
         	    			}
         	    			
         	    			
         	    			
         	    			
         	    		}
         			} catch (IOException e) {
         				Log.d("mytag", "error while read obf file");
         			
         			}
         		}
         		long timeend = System.currentTimeMillis();
         		
                WriteFileThread loop4=new WriteFileThread("end_time: "+new Date(timeend)+"\n",filepath+"loop_result.txt");
     			Thread loop44=new Thread(loop4);
     			loop44.start();
                 
                 //StringBuffer json_result = new StringBuffer();
                 
                 //String head = "{\"type\": \"FeatureCollection\",\"totalFeatures\": 130,\"features\": [";
                 //String body = "{\"type\": \"Feature\",\"id\": \"forywli.fid-5624317b_14fafc3a71f_-7a63\",\"geometry\": {\"type\": \"MultiLineString\",\"coordinates\": [[[116.1253049,39.7356748],[116.1250359,39.7355164],[116.1254782,39.7350788],[116.1257532,39.7352388]]]},\"geometry_name\": \"st_transform\",\"properties\": {\"name\": null,\"osm_id\": 183596047,\"highway\": \"footway\",\"ref\": null,\"oneway\": null,\"bridge\": \"yes\",\"layer\": \"1\",\"railway\": null,\"service\": null,\"building\": null,\"water\": null,\"leisure\": null,\"sport\": null,\"amenity\": null,\"operator\": null,\"natural\": null}},";
                 //String tail = "],\"crs\": {\"type\": \"name\",\"properties\": {\"name\": \"urn:ogc:def:crs:EPSG::900913\"}}}";
                 
                 
                 //json_result.append(body);
         		
                 
                 json_result.append(tail);
                response.setStatusCode(HttpStatus.SC_OK);  
                StringEntity entity = new StringEntity("Request Success!!");  
                String result ="[{\"recoID\":1,\"userID\":1,\"startTm\":\"20151001\",\"startPos\":\"北京\",\"startLot\":\"116.3912\",\"startLat\":\"39.9059\",\"arrTm\":\"20151002\",\"arrPos\":\"香港\",\"arrLot\":\"114.1628\",\"arrLat\":\"22.2793\",\"tripMode\":\"飞机\",\"vehNum\":\"SB200\",\"remark\":\"北京很冷\"},{\"recoID\":2,\"userID\":1,\"startTm\":\"20150901\",\"startPos\":\"石家庄\",\"startLot\":\"114.4627\",\"startLat\":\"38.0359\",\"arrTm\":\"20151001\",\"arrPos\":\"北京\",\"arrLot\":\"116.3912\",\"arrLat\":\"39.9059\",\"tripMode\":\"飞机\",\"vehNum\":\"SB200\",\"remark\":\"北京很冷\"},{\"recoID\":3,\"userID\":1,\"startTm\":\"20151002\",\"startPos\":\"香港\",\"startLot\":\"114.1628\",\"startLat\":\"22.2793\",\"arrTm\":\"20151010\",\"arrPos\":\"兰州\",\"arrLot\":\"103.82\",\"arrLat\":\"36.07\",\"tripMode\":\"飞机\",\"vehNum\":\"SB200\",\"remark\":\"东野老师说陌生的人请给我一支兰州\"}]";
                       
                Log.d("mytag", "result content: "+result);
                
    			WriteFileThread m1=new WriteFileThread(json_result.toString(),filepath+"result_"+(loopnum-1)+".txt");
				Thread t1=new Thread(m1);
				t1.start();         
                StringEntity temp1 = new StringEntity(json_result.toString(), "utf-8");
                
                response.setEntity(temp1);  
                String tt = ""+(loopnum-1)+"次返回数据\n";
                WriteFileThread loop2=new WriteFileThread(tt,filepath+"loop_result.txt");
    			Thread loop22=new Thread(loop2);
    			loop22.start(); 
                
                //response.setEntity(entity);  
            } else if (method.equals("POST") ) {  
                response.setStatusCode(HttpStatus.SC_OK);  
                StringEntity entity = new StringEntity("Request Success!!");  
                response.setEntity(entity);  
            } else {  
                throw new MethodNotSupportedException(method + " method not supported");  
            }  
        }  
  
    } 
    
    //longitude 还原方法
  		/*
  		 * @res int型源数据
  		 */
    static double pro2lon(int longi){
    	long l = 1L << 31;
//		System.out.println("l: "+l);
//		double longitude = 116.21803283;
//		System.out.println("longitude: "+longitude);
//		int res = (int)((longitude + 180d)/360d * l);
//		double res1 = (longitude + 180d)/360d;
//		System.out.println("result: "+res1* l);
		
		double longitude1 = longi*360d/l-180d;
		System.out.println("longitude1: "+longitude1);
		return longitude1;
    	
    }
    
  //longitude 还原方法
	/*
	 * @res lati_resu型源数据
	 */
    static double pro2lat(int lati){
    	
    	long l = 1L << 31;
    	double rr = Math.PI-2*Math.PI*lati/l;
    	
    			
    	double b = Math.pow(Math.E, rr);
    	double latitude1 = Math.acos(b*2/(b*b+1));
    	//System.out.println("latitude1: "+latitude1*180.0/Math.PI);
		return latitude1*180.0/Math.PI;
    	
    }
    static double tile2lon(int x, int z) {
	     return x / Math.pow(2.0, z) * 360.0 - 180;
	  }
	 
	static double tile2lat(int y, int z) {
	    double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
	    return Math.toDegrees(Math.atan(Math.sinh(n)));
	  }
	
    public RendererRegistry getRendererRegistry() {
  		return rendererRegistry;
  	}
      
      private static void startApplicationBackground() {

  		long val = System.currentTimeMillis();
  		
  		String dir = Environment.getExternalStorageDirectory()+"/osmand/";
  		
  		ArrayList<File> files = new ArrayList<File>();
  		File appPath = new File(dir);
  		collectFiles(appPath, IndexConstants.BINARY_MAP_INDEX_EXT, files);
//  		
  		
  		int count = 0;
  		for (File f : files) {
  			try {
  				Log.d("mytag", "filepath: "+f.getAbsolutePath());
  				BinaryMapIndexReader index = getReader(f);
  				
  				if (index != null) {
  					initializeNewResource( f, index);
  				}
  			}catch (IOException e) {
  				
  			}
  		}
  			
  	
      	
      }
      private static List<File> collectFiles(File dir, String ext, List<File> files) {
  		if(dir.exists() && dir.canRead()) {
  			File[] lf = dir.listFiles();
  			if(lf == null) {
  				lf = new File[0];
  			}
  			for (File f : lf) {
  				if (f.getName().endsWith(ext)) {
  					files.add(f);
  				}
  			}
  		}
  		return files;
  	}
      
      public static BinaryMapIndexReader getReader(File f) throws IOException {
  		RandomAccessFile mf = new RandomAccessFile(f.getPath(), "r");
  		
  		BinaryMapIndexReader reader = null;
  		
  			long val = System.currentTimeMillis();
  			

  			 
  			reader = new BinaryMapIndexReader(mf);
  			//addToCache(reader, f);
  			
  		
  		return reader;
  	}
      
      public static void initializeNewResource(File file, BinaryMapIndexReader reader) {
  		if (files.containsKey(file.getAbsolutePath())) {
  			closeConnection(files.get(file.getAbsolutePath()), file.getAbsolutePath());
  		
  		}
  		
  		files.put(file.getAbsolutePath(), reader);
//  		NativeOsmandLibrary nativeLib = NativeOsmandLibrary.getLoadedLibrary();
//  		if (nativeLib != null) {
//  			if (!nativeLib.initMapFile(file.getAbsolutePath())) {
//  				log.error("Initializing native db " + file.getAbsolutePath() + " failed!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//  			} else {
//  				nativeFiles.add(file.getAbsolutePath());
//  			}
//  		}
  	}
      
      protected static void closeConnection(BinaryMapIndexReader c, String file) {
  		files.remove(file);
//  		if(nativeFiles.contains(file)){
//  			NativeOsmandLibrary lib = NativeOsmandLibrary.getLoadedLibrary();
//  			if(lib != null) {
//  				lib.closeMapFile(file);
//  				nativeFiles.remove(file);
//  			}
//  		}
  		try {
  			c.close();
  		} catch (IOException e) {
  			e.printStackTrace();
  		}
  	}
  
  
    /**
     * 自定一个监听的线程
     * @author weijiang204321
     *
     */
    static class RequestListenerThread extends Thread {  
  
        private final ServerSocket serversocket;  
        private final HttpParams params;  
        private final HttpService httpService;  
  
        public RequestListenerThread(int port)  
                throws IOException {  
        	
        	/**********************下面就是模板代码了***********************/
            this.serversocket = new ServerSocket(port);  
  
            HttpProcessor httpproc = new ImmutableHttpProcessor(  
                    new HttpResponseInterceptor[] {  
                            new ResponseDate(), new ResponseServer(),  
                            new ResponseContent(), new ResponseConnControl() });  
            this.params = new BasicHttpParams();  
            this.params  
                    .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)  
                    .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,8 * 1024)  
                    .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)  
                    .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)  
                    .setParameter(CoreProtocolPNames.ORIGIN_SERVER,"HttpComponents/1.1");  
  
  
            //这只请求头信息 
            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry(); 
            //WebServiceHandler用来处理webservice请求,这里我们可以注册多个处理器Handler的
            reqistry.register("*", new WebServiceHandler());    
  
            this.httpService = new HttpService(httpproc,  
                    new DefaultConnectionReuseStrategy(),  
                    new DefaultHttpResponseFactory());  
            httpService.setParams(this.params);
            //为http服务设置注册好的请求处理器。 
            httpService.setHandlerResolver(reqistry);        
  
        }  
  
        @Override  
        public void run() {  
            AppListYPLog.i("Listening on port " + this.serversocket.getLocalPort());  
            AppListYPLog.i("Thread.interrupted = " + Thread.interrupted());   
            while (!Thread.interrupted()) {  
                try {  
                    // 建立Http连接  
                    Socket socket = this.serversocket.accept();  
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();  
                    ipAddress = socket.getInetAddress().getHostAddress();
                    AppListYPLog.i("Incoming connection from " + socket.getInetAddress());  
                    conn.bind(socket, this.params);  
                    //开启工作线程  
                    Thread t = new WorkerThread(this.httpService, conn);  
                    t.setDaemon(true);  
                    t.start();  
                } catch (InterruptedIOException ex) {  
                	AppListYPLog.e("异常:InterruptedIOException");
                    break;  
                } catch (IOException e) {  
                    AppListYPLog.e("I/O error initialising connection thread: " + e.getMessage());  
                    break;  
                }  
            }  
        }  
    }
    
    static class WriteFileThread implements Runnable{
       
        private String con;
        private String filepath;
        
        WriteFileThread(String con, String filepath){
            this.con=con;
            this.filepath=filepath;
        }
        public void run(){
            
        	//write con to file
        	
        	//String filepath = Environment.getExternalStorageDirectory()+"/lhc/result.txt";
        	File filename = new File(filepath);
        	if(!filename.exists()){
        		try {
					filename.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	try{ 

                //FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);

               FileOutputStream fout = new FileOutputStream(filepath,true);

                byte [] bytes = con.getBytes(); 

                fout.write(bytes); 

                 fout.close(); 

                } 

               catch(Exception e){ 

                e.printStackTrace(); 

               } 
        	
        }
    }
  
  
    /**
     * 后台工作线程
     * @author weijiang204321
     *
     */
    static class WorkerThread extends Thread {  
  
        private final HttpService httpservice;  
        private final HttpServerConnection conn;  
  
  
        public WorkerThread(final HttpService httpservice,final HttpServerConnection conn) {  
            super();  
            this.httpservice = httpservice;  
            this.conn = conn;  
        }  
  
  
        @Override  
        public void run() {  
            System.out.println("New connection thread");  
            HttpContext context = new BasicHttpContext(null);  
            try {  
                while (!Thread.interrupted() && this.conn.isOpen()) {  
                    this.httpservice.handleRequest(this.conn, context);  
                }  
            } catch (ConnectionClosedException ex) {  
                System.err.println("Client closed connection");  
            } catch (IOException ex) {  
                System.err.println("I/O error: " + ex.getMessage());  
            } catch (HttpException ex) {  
                System.err.println("Unrecoverable HTTP protocol violation: "+ ex.getMessage());  
            } finally {  
                try {  
                    this.conn.shutdown();  
                } catch (IOException ignore) {  
                }  
            }  
        }  
    } 
    
}  