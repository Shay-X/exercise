package exercise;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnTrackListener;
import com.example.exercise.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import baidu.trackutils.DateDialog;
import baidu.trackutils.DateDialog.CallBack;
import baidu.trackutils.DateDialog.PriorityListener;
import baidu.trackutils.DateUtils;
import baidu.trackutils.GsonService;
import baidu.trackutils.HistoryTrackData;

public class ExerciseInfo extends Activity implements OnClickListener{
	
	private int startTime = 0;
    private int endTime = 0;

    private int year = 0;
    private int month = 0;
    private int day = 0;

    private static BitmapDescriptor bmStart;
    private static BitmapDescriptor bmEnd;
    private static MarkerOptions startMarker = null;
    private static MarkerOptions endMarker = null;
    private static PolylineOptions polyline = null;
    private MapStatusUpdate msUpdate = null;
    private LBSTraceClient client;
    protected OnTrackListener trackListener = null;
    
    private static int isProcessed = 0;
    private Button btnDate = null;
    private Button btnProcessed = null;

	MapView mMapView = null;    // ��ͼView
	BaiduMap mBaiduMap = null;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise_info);

		mMapView = (MapView)findViewById(R.id.map_info);
		mBaiduMap = mMapView.getMap();
		
        btnDate = (Button) findViewById(R.id.btn_queryInfo);
        btnDate.setOnClickListener(this);
        btnProcessed = (Button) findViewById(R.id.btn_processed);
        btnProcessed.setOnClickListener(this);
        
        initOnTrackListener();
		
	}
	
	  /**
     * ��ʼ��OnTrackListener
     */
    private void initOnTrackListener() {

        trackListener = new OnTrackListener() {

            // ����ʧ�ܻص��ӿ�
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "track����ʧ�ܻص��ӿ���Ϣ : " + arg0, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            // ��ѯ��ʷ�켣�ص��ӿ�
            @Override
            public void onQueryHistoryTrackCallback(String arg0) {
                // TODO Auto-generated method stub
                super.onQueryHistoryTrackCallback(arg0);
                showHistoryTrack(arg0);
            }
        };
    }
    
    /**
     * ��ѯ��ʷ�켣
     */
    public void queryHistoryTrack() {

        // �Ƿ񷵻ؾ���Ľ����0 : ��1 : �ǣ�
        int simpleReturn = 0;
        // ��ʼʱ��
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        // ��ҳ��С
        int pageSize = 1000;
        // ��ҳ����
        int pageIndex = 1;
        
       

        client.queryHistoryTrack(ExerciseRecord.serviceId, ExerciseRecord.entityName, 
        		simpleReturn, startTime, endTime,pageSize,pageIndex,trackListener);
    }
    
    /**
     * ��ѯ��ƫ�����ʷ�켣
     */
    private void queryProcessedHistoryTrack() {

        // �Ƿ񷵻ؾ���Ľ����0 : ��1 : �ǣ�
        int simpleReturn = 0;
        // �Ƿ񷵻ؾ�ƫ��켣��0 : ��1 : �ǣ�
        int isProcessed = 1;
        // ��ʼʱ��
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        // ��ҳ��С
        int pageSize = 1000;
        // ��ҳ����
        int pageIndex = 1;

        client.queryProcessedHistoryTrack(ExerciseRecord.serviceId, ExerciseRecord.entityName, simpleReturn, isProcessed,
                startTime, endTime,
                pageSize,
                pageIndex,
                trackListener);
    }
    
    
    /**
     * �켣��ѯ(��ѡ�����ڣ��ٸ����Ƿ��ƫ����������)
     */
    private void queryTrack() {
        // ѡ������
        int[] date = null;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        if (year == 0 && month == 0 && day == 0) {
            String curDate = DateUtils.getCurrentDate();
            date = DateUtils.getYMDArray(curDate, "-");
        }

        if (date != null) {
            year = date[0];
            month = date[1];
            day = date[2];
        }

        DateDialog dateDiolog = new DateDialog(this, new PriorityListener() {

            public void refreshPriorityUI(String sltYear, String sltMonth,
                    String sltDay, CallBack back) {

                Log.d("TGA", sltYear + sltMonth + sltDay);
                year = Integer.parseInt(sltYear);
                month = Integer.parseInt(sltMonth);
                day = Integer.parseInt(sltDay);
                String st = year + "��" + month + "��" + day + "��0ʱ0��0��";
                String et = year + "��" + month + "��" + day + "��23ʱ59��59��";

                startTime = Integer.parseInt(DateUtils.getTimeToStamp(st));
                endTime = Integer.parseInt(DateUtils.getTimeToStamp(et));

                back.execute();
            }

        }, new CallBack() {

            public void execute() {

                // ѡ�������ڣ������Ƿ��ƫ���͹켣��ѯ����
                if (0 == isProcessed) {
                    Toast.makeText(getApplicationContext(), "���ڲ�ѯ��ʷ�켣�����Ժ�", Toast.LENGTH_SHORT).show();
                    queryHistoryTrack();
                } else {
                    Toast.makeText(getApplicationContext(), "���ڲ�ѯ��ƫ�����ʷ�켣�����Ժ�", Toast.LENGTH_SHORT).show();
                    queryProcessedHistoryTrack();
                }
            }
        }, year, month, day, width, height, "ѡ������", 1);

        Window window = dateDiolog.getWindow();
        window.setGravity(Gravity.CENTER); // �˴���������dialog��ʾ��λ��
        dateDiolog.setCancelable(true);
        dateDiolog.show();

    }
    
    /**
     * ��ʾ��ʷ�켣
     * 
     * @param trackData
     */
    public void showHistoryTrack(String historyTrack) {

        HistoryTrackData historyTrackData = GsonService.parseJson(historyTrack,
                HistoryTrackData.class);
        List<LatLng> latLngList = new ArrayList<LatLng>();
        if (historyTrackData != null && historyTrackData.getStatus() == 0) {
            if (historyTrackData.getListPoints() != null) {
                latLngList.addAll(historyTrackData.getListPoints());
            }

            // ������ʷ�켣
            drawHistoryTrack(latLngList);

        }

    }
    
    /**
     * ������ʷ�켣
     * 
     * @param points
     */
    public void drawHistoryTrack(final List<LatLng> points) {
        // �����¸�����ǰ�����֮ǰ�ĸ�����
        mBaiduMap.clear();

        if (points == null || points.size() == 0) {
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "��ǰ��ѯ�޹켣��", Toast.LENGTH_SHORT).show();
            Looper.loop();
            resetMarker();
        } else if (points.size() > 1) {

            LatLng llC = points.get(0);
            LatLng llD = points.get(points.size() - 1);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(llC).include(llD).build();

            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);

            bmStart = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
            bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);

            // ������ͼ��
            startMarker = new MarkerOptions()
                    .position(points.get(points.size() - 1)).icon(bmStart)
                    .zIndex(9).draggable(true);

            // ����յ�ͼ��
            endMarker = new MarkerOptions().position(points.get(0))
                    .icon(bmEnd).zIndex(9).draggable(true);

            // ���·�ߣ��켣��
            polyline = new PolylineOptions().width(10)
                    .color(Color.RED).points(points);

            addMarker();

        }

    }
    
    /**
     * ��Ӹ�����
     */
    public void addMarker() {

        if (null != msUpdate) {
            mBaiduMap.setMapStatus(msUpdate);
        }

        if (null != startMarker) {
            mBaiduMap.addOverlay(startMarker);
        }

        if (null != endMarker) {
            mBaiduMap.addOverlay(endMarker);
        }

        if (null != polyline) {
            mBaiduMap.addOverlay(polyline);
        }

    }
    
    /**
     * ���ø�����
     */
    private void resetMarker() {
        startMarker = null;
        endMarker = null;
        polyline = null;
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
	public void onClick(View v) {
		// TODO �Զ����ɵķ������
        switch (v.getId()) {
            case R.id.btn_queryInfo:
                // ��ѯ�켣
                queryTrack();
                break;

            case R.id.btn_processed:
                isProcessed = isProcessed ^ 1;
                if (0 == isProcessed) {
                    btnProcessed.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
                    btnProcessed.setTextColor(Color.rgb(0x00, 0x00, 0x00));
                    Toast.makeText(getApplicationContext(), "���ڲ�ѯ��ʷ�켣�����Ժ�", Toast.LENGTH_SHORT).show();
                    queryHistoryTrack();
                } else {
                    btnProcessed.setBackgroundColor(Color.rgb(0x99, 0xcc, 0xff));
                    btnProcessed.setTextColor(Color.rgb(0x00, 0x00, 0xd8));
                    Toast.makeText(getApplicationContext(), "���ڲ�ѯ��ƫ�����ʷ�켣�����Ժ�", Toast.LENGTH_SHORT).show();
                    queryProcessedHistoryTrack();
                }
                break;

            default:
                break;
        }
	} 
	


}
