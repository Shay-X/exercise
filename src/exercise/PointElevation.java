package exercise;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.platform.comapi.map.r;
import com.example.exercise.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import util.BDtoWGS84;
import util.VolleyHttp;
import util.isNumeric;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class PointElevation extends Activity implements OnGetGeoCoderResultListener, OnMapLongClickListener {
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;
	GeoCoder mSearch = null;
	boolean isFirstLoc = true;
	
	LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    BitmapDescriptor mCurrentMarker;
    
	public Button btn1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_point_elevation);
		btn1 = (Button) findViewById(R.id.pointSerach);
		mMapView = (MapView)findViewById(R.id.map_point);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapLongClickListener(this);
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
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		Button();
		
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
	
	private void Button() {
		// TODO �Զ����ɵķ������
		btn1.setOnClickListener(new  View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				EditText editCity = (EditText) findViewById(R.id.pointCity);
				EditText editGeoCodeKey = (EditText) findViewById(R.id.pointAddress);
				// Geo����
				if (isNumeric.isNumeric(editCity.getText().toString())==false&isNumeric.isNumeric(editGeoCodeKey.getText().toString())==false)
				{
				mSearch.geocode(new GeoCodeOption().city(
						editCity.getText().toString()).address(
						editGeoCodeKey.getText().toString()));
				}
				else if (isNumeric.isNumeric(editCity.getText().toString())==true&isNumeric.isNumeric(editGeoCodeKey.getText().toString())==true){
					EditText lat = (EditText) findViewById(R.id.pointCity);
					EditText lon = (EditText) findViewById(R.id.pointAddress);
					LatLng ptCenter = new LatLng((Float.valueOf(lat.getText()
							.toString())), (Float.valueOf(lon.getText().toString())));
					// ��Geo����
					mSearch.reverseGeoCode(new ReverseGeoCodeOption()
							.location(ptCenter));
				}
				else Toast.makeText(PointElevation.this, "��Ǹ��δ���ҵ����", Toast.LENGTH_LONG)
				.show();
				
			}
			
		});
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
    
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(PointElevation.this, "��Ǹ��δ���ҵ����", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		
		LatLng pointWGS84 = BDtoWGS84.bd09ToWGS84(result.getLocation());
        float h = VolleyHttp.getPointElevation(pointWGS84.latitude, pointWGS84.longitude);
        
		String strInfo = String.format("γ�ȣ�%3.3f ���ȣ�%3.3f ���Σ�%3.3f",
				result.getLocation().latitude, result.getLocation().longitude, h);
		Toast.makeText(PointElevation.this, strInfo, Toast.LENGTH_LONG).show();
		
		LatLng llText = new LatLng(result.getLocation().latitude, result.getLocation().longitude);
        OverlayOptions ooText = new TextOptions().bgColor(0x00FFFF00)
                .fontSize(30).fontColor(0xFFFF00FF).text(strInfo).rotate(0)
                .position(llText);
        mBaiduMap.addOverlay(ooText);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(PointElevation.this, "��Ǹ��δ���ҵ����", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		
		LatLng pointWGS84 = BDtoWGS84.bd09ToWGS84(result.getLocation());
        float h = VolleyHttp.getPointElevation(pointWGS84.latitude, pointWGS84.longitude);
		
		String strInfo = String.format(" ���Σ�%3.3f",
				 h);
		Toast.makeText(PointElevation.this, result.getAddress()+strInfo, Toast.LENGTH_LONG).show();
		
		LatLng llText = new LatLng(result.getLocation().latitude, result.getLocation().longitude);
        OverlayOptions ooText = new TextOptions().bgColor(0x00FFFF00)
                .fontSize(30).fontColor(0xFFFF00FF).text(result.getAddress()+strInfo).rotate(0)
                .position(llText);
        mBaiduMap.addOverlay(ooText);		

	}

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO �Զ����ɵķ������
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(point)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		
		LatLng pointWGS84 = BDtoWGS84.bd09ToWGS84(point);
        float h = VolleyHttp.getPointElevation(pointWGS84.latitude, pointWGS84.longitude);
        
		String strInfo = String.format("γ�ȣ�%3.3f ���ȣ�%3.3f ���Σ�%3.3f",point.latitude, point.longitude, h);
        OverlayOptions ooText = new TextOptions().bgColor(0x00FFFF00)
                .fontSize(30).fontColor(0xFFFF00FF).text(strInfo).rotate(0)
                .position(point);
        mBaiduMap.addOverlay(ooText);
        
	}

	
}
