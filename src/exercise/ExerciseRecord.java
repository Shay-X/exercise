package exercise;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.Trace;
import com.example.exercise.R;

import android.app.Activity;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import baidu.trackutils.GsonService;
import baidu.trackutils.RealtimeTrackData;
import util.GetExerInfo;

public class ExerciseRecord extends Activity{
	//ҳ�����
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;	
	public Button btn1;
	public Button btn2;
	public Button btn3;
	public TextView speed;
	public TextView distance;
	public Chronometer time;
	private long recordingTime = 0;
	
	int gatherInterval = 2;  //λ�òɼ����� (s)
    int packInterval = 10;  //������� (s)
    
    static String entityName = "С��4c";  // entity��ʶ
    static long serviceId = 115376;// ӥ�۷���ID
    
    int traceType = 2;  //�켣��������
    private static OnStartTraceListener startTraceListener = null;  //�����켣���������
    private static OnStopTraceListener stopTraceListener = null;
    

	private static OnEntityListener entityListener = null;
	private RefreshThread refreshThread = null;  //ˢ�µ�ͼ�߳��Ի�ȡʵʱ��
	private static MapStatusUpdate msUpdate = null;
	private static BitmapDescriptor realtimeBitmap;  //ͼ��
	private static OverlayOptions overlay;  //������
	private static List<LatLng> pointList = new ArrayList<LatLng>();  //��λ��ļ���
	
	
	private static PolylineOptions polyline = null;  //·�߸�����
	
	
    private Trace trace;  // ʵ�����켣����
    private LBSTraceClient client;  // ʵ�����켣����ͻ���


	
    //*��λ���
	LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    BitmapDescriptor mCurrentMarker;
    private boolean isFirstLoc = true;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise_record);
		btn1 = (Button) findViewById(R.id.btn_exerRecordBegin);
		btn2 = (Button) findViewById(R.id.btn_exerSuspended);
		btn3 = (Button) findViewById(R.id.btn_exerRecordEnd);
		speed = (TextView) findViewById(R.id.text_speed);
		distance = (TextView) findViewById(R.id.text_distance);
		time = (Chronometer) findViewById(R.id.text_time);
				
		mMapView = (MapView)findViewById(R.id.map_record);
		mBaiduMap = mMapView.getMap();
		
		
		// ������λͼ���ʼ��
        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // ��gps
        option.setCoorType("bd09ll"); // ������������
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        
        init();
		button();
		

	}
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy(); 
        initOnStopTraceListener();
        
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();
        
        }

	/**
	 * ��ʼ����������
	 */
	 private void init() {
		 
		 mMapView.showZoomControls(false);
		 
		 
         client = new LBSTraceClient(getApplicationContext());  //ʵ�����켣����ͻ���
         
         trace = new Trace(getApplicationContext(), serviceId, entityName, traceType);  //ʵ�����켣����
         
         client.setInterval(gatherInterval, packInterval);  //����λ�òɼ��ʹ������
         
	 }
	 
	 
	 /**
	  * ��ʼ������ʵ��״̬������
	  */
	 private void initOnEntityListener(){
		 
		 //ʵ��״̬������
		 entityListener = new OnEntityListener(){

			@Override
			public void onRequestFailedCallback(String arg0) {
				Looper.prepare();
				Toast.makeText(
						getApplicationContext(), 
						"entity����ʧ�ܵĻص��ӿ���Ϣ��"+arg0, 
						Toast.LENGTH_SHORT)
						.show();
				Looper.loop();
			}
			
			@Override
			public void onQueryEntityListCallback(String arg0) {
				/**
				 * ��ѯʵ�弯�ϻص���������ʱ����ʵʱ�켣����
				 */
				showRealtimeTrack(arg0); 
				
			}
			
			
		 };
	 }
	 
	 
	 
	/** ׷�ٿ�ʼ */
	private void initOnStartTraceListener() {		
		// ʵ���������켣����ص��ӿ�
		startTraceListener = new OnStartTraceListener() {
			// �����켣����ص��ӿڣ�arg0 : ��Ϣ���룬arg1 : ��Ϣ���ݣ�����鿴��ο���
			@Override
			public void onTraceCallback(int arg0, String arg1) {
				Log.i("TAG", "onTraceCallback=" + arg1);
				if(arg0 == 0 || arg0 == 10006){
					startRefreshThread(true);
				}
			}

			// �켣�������ͽӿڣ����ڽ��շ����������Ϣ��arg0 : ��Ϣ���ͣ�arg1 : ��Ϣ���ݣ�����鿴��ο���
			@Override
			public void onTracePushCallback(byte arg0, String arg1) {
				Log.i("TAG", "onTracePushCallback=" + arg1);
			}
			
		};
		client.startTrace(trace, startTraceListener);
	}
	
	//׷�ٽ���
	private void initOnStopTraceListener(){
		stopTraceListener = new OnStopTraceListener() {
			
			@Override
			public void onStopTraceSuccess() {
				// TODO �Զ����ɵķ������
				Log.i("TAG", "onStopTraceSuccess is ok");
			}
			
			@Override
			public void onStopTraceFailed(int arg0, String arg1) {
				// TODO �Զ����ɵķ������
				Log.i("TAG", "onStopTraceFailed=" + arg1);
			}
		};
		client.stopTrace(trace, stopTraceListener);
		
	}
	
	
	/**
	 * �켣ˢ���߳�
	 * @author BLYang
	 */
	private class RefreshThread extends Thread{
		 
		protected boolean refresh = true;  
		
		public void run(){
			
			while(refresh){
				queryRealtimeTrack();
				try{
					Thread.sleep(packInterval * 1000);
				}catch(InterruptedException e){
					System.out.println("�߳�����ʧ��");
				}
			}
			
		}
	}
	 
	/**
	 * ��ѯʵʱ��·
	 */
	private void queryRealtimeTrack(){
		
		String entityName = this.entityName;
		String columnKey = "";
		int returnType = 0;
		int activeTime = 0;
		int pageSize = 10;
		int pageIndex = 1;
		
		this.client.queryEntityList(
				serviceId, 
				entityName, 
				columnKey, 
				returnType,
				activeTime, 
				pageSize, 
				pageIndex, 
				entityListener
				);
		
	}
	
	
	/**
	 * չʾʵʱ��·ͼ
	 * @param realtimeTrack
	 */
	protected void showRealtimeTrack(String realtimeTrack){
		
		if(refreshThread == null || !refreshThread.refresh){
			return;
		}
		
		//������JSON��ʽ��ȡ
		RealtimeTrackData realtimeTrackData = GsonService.parseJson(realtimeTrack, RealtimeTrackData.class);
		
		if(realtimeTrackData != null && realtimeTrackData.getStatus() ==0){
			
			LatLng latLng = realtimeTrackData.getRealtimePoint();
			
			if(latLng != null){
				pointList.add(latLng);
				drawRealtimePoint(latLng);
			}
			else{
				Toast.makeText(getApplicationContext(), "��ǰ�޹켣��", Toast.LENGTH_LONG).show();
			}
			
		}
		
	}
	
	/**
	 * ����ʵʱ��·��
	 * @param point
	 */
	private void drawRealtimePoint(LatLng point){
		
		mBaiduMap.clear();
		MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
		msUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
		realtimeBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		overlay = new MarkerOptions().position(point)
				.icon(realtimeBitmap).zIndex(9).draggable(true);
		
		if(pointList.size() >= 2  && pointList.size() <= 1000){
			polyline = new PolylineOptions().width(10).color(Color.RED).points(pointList);
		}
		
		addMarker();
		String distanceNUM = String.format("%.3f", GetExerInfo.lineDisetance(pointList));
		distance.setText(distanceNUM+"km");
		String avgSpeedNUM = String.format("%.2f", GetExerInfo.avgSpeedInfo(GetExerInfo.lineDisetance(pointList), time.getBase()));
		
	}
	
	private void stopDrawPoint() {
		mBaiduMap.clear();		
	}
	
	
	private void addMarker(){
		
		if(msUpdate != null){
			mBaiduMap.setMapStatus(msUpdate);
		}
		
		if(polyline != null){
			mBaiduMap.addOverlay(polyline);
		}
		
		if(overlay != null){
			mBaiduMap.addOverlay(overlay);
		}
		
		
	}
	
	
	/**
	 * ����ˢ���߳�
	 * @param isStart
	 */
	private void startRefreshThread(boolean isStart){
		
		if(refreshThread == null){
			refreshThread = new RefreshThread();
		}
		
		refreshThread.refresh = isStart;
		
		if(isStart){
			if(!refreshThread.isAlive()){
				refreshThread.start();
			}
		}
		else{
			refreshThread = null;
		}
		
		
	}

    
    private void button() {
    	//��ʱ������ת��ť
		//Ĭ����ͣ��ֹͣ������
    	
		btn2.setEnabled(false);
		btn3.setEnabled(false);
		btn1.setOnClickListener(new View.OnClickListener() {//��ʼ��ť			
			@Override
			public void onClick(View arg0) {
				time.setBase(SystemClock.elapsedRealtime()-recordingTime); //�����Ѿ���¼��ʱ�䣬������ʱ
				time.start();
				btn1.setEnabled(false);
				btn2.setEnabled(true);
				btn3.setEnabled(true);//���¿�ʼ�����ͣ��ֹͣ����	
				
				initOnEntityListener();				
				initOnStartTraceListener();			
				
				

			}
		});
		
		btn2.setOnClickListener(new View.OnClickListener() {//��ͣ��ť		
			@Override
			public void onClick(View arg0) {
				time.stop();
				recordingTime = SystemClock.elapsedRealtime()- time.getBase();//��ͣʱ�����Ѿ���¼��ʱ�䣬��ʼֵΪ0
				btn1.setEnabled(true);
				btn2.setEnabled(false);
				btn3.setEnabled(true);//��ͣʱ����ʼ����ͣ����
				initOnStopTraceListener();
			}
		});
		
		btn3.setOnClickListener(new View.OnClickListener() {//ֹͣ��ť			
			@Override
			public void onClick(View arg0) {
				recordingTime = 0;
				time.setBase(SystemClock.elapsedRealtime());
				time.stop();                                   //�Ѽ�¼ʱ�����㣬ʱ����0ͬʱֹͣ����
				btn1.setEnabled(true);
				btn2.setEnabled(false);
				btn3.setEnabled(false);//ֹͣʱ����ʼ����
				stopDrawPoint();
				initOnStopTraceListener();
				
			}
		});
		
	}

	public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view ���ٺ��ڴ����½��յ�λ��
            if (location == null || mMapView == null) {
                return;
            }
            if(location.getLocType() == BDLocation.TypeGpsLocation){
            	String speedText =  String.format("%.3f", location.getSpeed());
            	speed.setText("ʱ�٣�"+speedText+"km/h");
            }
            if(location.getLocType() != BDLocation.TypeGpsLocation){
            	speed.setText("�������˶�����ʾʱ��");
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));                              
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
	

}
