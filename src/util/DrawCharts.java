package util;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.utils.DistanceUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class DrawCharts  {
    
	// ������ʾ����ʽ  
    public static void showChart(LineChart lineChart, LineData lineData) {
    	
        lineChart.setDrawBorders(false);  //�Ƿ�������ͼ����ӱ߿�  
        lineChart.setDescription("");// 
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");   
        lineChart.setDrawGridBackground(false); 
        lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); 
        lineChart.setTouchEnabled(true); 
        lineChart.setDragEnabled(true); 
        lineChart.setScaleEnabled(true);
        lineChart.setBackgroundColor(Color.rgb(255, 255, 255));        
        lineChart.setData(lineData);  
         
        Legend mLegend = lineChart.getLegend();         
        mLegend.setForm(LegendForm.CIRCLE); 
        mLegend.setFormSize(6f); 
        mLegend.setTextColor(Color.WHITE); 
        
        lineChart.invalidate();
        lineChart.animateXY(3000,3000);  
      }  
      
    //���Ա������
    public static LineData getTestData() {  
        ArrayList<String> xValues = new ArrayList<String>();  
        for (int i = 0; i < 100; i++) {  
         
            xValues.add("" + i);  
        }  
  
         
        ArrayList<Entry> yValues = new ArrayList<Entry>();  
        for (int i = 0; i < 100; i++) {  
            float value = (float) (Math.random() * 1000) + 3;  
            yValues.add(new Entry(value, i));  
        }  
  
          
        LineDataSet lineDataSet = new LineDataSet(yValues, "��������ͼ" );  
         
        lineDataSet.setLineWidth(1.75f); 
        lineDataSet.setColor(Color.WHITE); 
        lineDataSet.setDrawValues(false);
        
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.rgb(168,133,18));
  
        List<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();  
        lineDataSets.add(lineDataSet); 
        LineData lineData = new LineData(xValues, lineDataSets); 
  
        return lineData;  
    }
    
    //��������
    public static LineData getData(elevationAndPointList hAndPointList) {
    	//x������
        ArrayList<String> xValues = new ArrayList<String>(); 
        xValues.add("0");
        double dd = 0;
        for (int i = 0; i < hAndPointList.point.size()-1; i++) {   
        	double d = DistanceUtil.getDistance(hAndPointList.point.get(i),hAndPointList.point.get(i+1));
        	dd+=d;
            xValues.add((int)dd+"");  
        }  
  
        // y�������  
        ArrayList<Entry> yValues = new ArrayList<Entry>();  
        for (int i = 0; i < hAndPointList.h.size(); i++) {  
            float value = hAndPointList.h.get(i);
            yValues.add(new Entry((int)value, i));  
        }  
        
        LineDataSet lineDataSet = new LineDataSet(yValues, "����ͼ" );  
         
        lineDataSet.setLineWidth(1.75f);  
        lineDataSet.setColor(Color.WHITE); 
        lineDataSet.setDrawValues(false);
        
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.rgb(168,133,18));
  
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();  
        lineDataSets.add(lineDataSet);
        
        LineData lineData = new LineData(xValues, lineDataSets);  
  
        return lineData;  
    } 
}