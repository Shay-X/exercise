package exercise;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class AppApplication extends Application {

	public static RequestQueue requestQueue;
	@Override
	public void onCreate() {
		super.onCreate();
		// ��ʹ�� SDK �����֮ǰ��ʼ�� context ��Ϣ������ ApplicationContext
		SDKInitializer.initialize(this);
		requestQueue = Volley.newRequestQueue(this);
	}
	public static RequestQueue getHttpRequestQueue() {
		return requestQueue;
	}
}
