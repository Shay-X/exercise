package com.example.exercise;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

import android.app.Activity;
import android.os.Bundle;

public class ExerciseInfo extends Activity{

	MapView mMapView = null;    // ��ͼView
	BaiduMap mBaidumap = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise_info);

		mMapView = (MapView)findViewById(R.id.map_info);
		mBaidumap = mMapView.getMap();
		
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
