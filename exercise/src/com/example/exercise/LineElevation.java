package com.example.exercise;

import java.util.ArrayList;
import java.util.List;

import com.baidu.a.a.a.b;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.platform.comapi.map.r;
import com.baidu.trace.TraceLocation;
import com.example.exercise.PointElevation.MyLocationListenner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class LineElevation extends Activity implements OnMapClickListener {
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;
	private static List<LatLng>touchLine = new ArrayList<LatLng>();
	private static List<LatLng>drawLine = new ArrayList<LatLng>();
	
    boolean isFirstLoc = true;
    
	
	LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    BitmapDescriptor mCurrentMarker;
    
	public Button btn1;
	public Button btn2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_line_elevation);
		btn1 = (Button) findViewById(R.id.btn_lineSearch);
		btn2 = (Button) findViewById(R.id.btn_lineClean);
		
		
		
		mMapView = (MapView)findViewById(R.id.map_line);
		mBaiduMap = mMapView.getMap();
		
		mBaiduMap.setOnMapClickListener(this);
		
		// ������λͼ��
        mBaiduMap.setMyLocationEnabled(true);
        // ��λ��ʼ��
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // ��gps
        option.setCoorType("bd09ll"); // ������������
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

		Button();
		
	}

	
	private void Button() {
		// TODO �Զ����ɵķ������
		btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				
			}
		});
		btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				mBaiduMap.clear();
				touchLine.clear();
				drawLine.clear();
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
 	
 
 	@Override
 	public boolean onMapPoiClick(MapPoi arg0) {
 		// TODO �Զ����ɵķ������
 		return false;
 	}
 	@Override
 	public void onMapClick(LatLng arg0) {
 		// TODO �Զ����ɵķ������
		
	 		mBaiduMap.clear();	 		
	 		touchLine.add(arg0);
	 		if (touchLine.size()>1){	 			
	 			OverlayOptions ooPolyline = new PolylineOptions().width(4)
	 					.color(0xAAFF0000).points(touchLine);
	 			mBaiduMap.addOverlay(ooPolyline);
	 		}
	 }
 	
 	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy(); 
        
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
 	
 
 }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  