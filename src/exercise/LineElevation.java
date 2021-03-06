package exercise;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.model.LatLng;
import com.example.exercise.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;  
import com.github.mikephil.charting.components.Legend;  
import com.github.mikephil.charting.components.Legend.LegendForm;  
import com.github.mikephil.charting.data.Entry;  
import com.github.mikephil.charting.data.LineDataSet;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.Touch;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import util.DrawCharts;
import util.VolleyHttp;
import util.elevationAndPointList;
public class LineElevation extends Activity implements OnMapClickListener {
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;
	private static List<LatLng>touchLine = new ArrayList<LatLng>();
	private static List<LatLng>drawLine = new ArrayList<LatLng>();
	
    boolean isFirstLoc = true;
    
	
	LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    BitmapDescriptor mCurrentMarker;
    
	private Button btn1;
	private Button btn2;
	private LineChart mLineChart;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_line_elevation);
		btn1 = (Button) findViewById(R.id.btn_lineSearch);
		btn2 = (Button) findViewById(R.id.btn_lineClean);
	    mLineChart = (LineChart)findViewById(R.id.chart_line);
		
		
		mMapView = (MapView)findViewById(R.id.map_line);
		mBaiduMap = mMapView.getMap();
		
		
		mBaiduMap.setOnMapClickListener(this);
		
		// 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

		Button();
		
		
		
	}

	
	private void Button() {
		// TODO 自动生成的方法存根
		btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				elevationAndPointList HAndPoint = new elevationAndPointList();
				try {
					HAndPoint = VolleyHttp.postLineElevation(drawLine);
					
				} catch (JSONException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				if (touchLine.size()>1){
					//LineData mLineData = DrawCharts.getData(HAndPoint);  //
					LineData mLineData = DrawCharts.getTestData();  //此行为测试数据
					
					DrawCharts.showChart(mLineChart, mLineData);
					}
				}
		});
		btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				mBaiduMap.clear();
				touchLine.clear();
				drawLine.clear();
			}
		}); 		     
	} 
 
 	public class MyLocationListenner implements BDLocationListener {
 
         @Override
         public void onReceiveLocation(BDLocation location) {
             // map view 销毁后不在处理新接收的位置
             if (location == null || mMapView == null) {
                 return;
             }
             MyLocationData locData = new MyLocationData.Builder()
                     .accuracy(location.getRadius())
                             // 此处设置开发者获取到的方向信息，顺时针0-360
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
 		// TODO 自动生成的方法存根
 		return false;
 	}
 	@Override
 	public void onMapClick(LatLng arg0) {
 		// TODO 自动生成的方法存根
		
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
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy(); 
        
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();
        
        }
 	 
 }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  