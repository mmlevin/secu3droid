/* Secu3Droid - An open source, free manager for SECU-3 engine
 * control unit
 * Copyright (C) 2013 Maksim M. Levin. Russia, Voronezh
 * 
 * SECU-3  - An open source, free engine control unit
 * Copyright (C) 2007 Alexey A. Shabelnikov. Ukraine, Gorlovka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * contacts:
 *            http://secu-3.org
 *            email: mmlevin@mail.ru
*/

package org.secu3.android.fragments;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.secu3.android.R;
import org.secu3.android.api.io.Secu3Dat;
import org.secu3.android.api.io.Secu3Dat.DiagInpDat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.LinearLayout;

public class DiagnosticsChartFragment extends Secu3Fragment implements ISecu3Fragment {
	private GraphicalView mChartView;
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

	DiagInpDat packet = null;		

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) return null;
		
		return inflater.inflate(R.layout.diagnostics_chart, null);
	}
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    // set some properties on the main renderer
	    //mRenderer.setApplyBackgroundColor(true);
	    //mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
	    //mRenderer.setAxisTitleTextSize(16);
	    //mRenderer.setChartTitleTextSize(20);
	    //mRenderer.setLabelsTextSize(15);
	    //mRenderer.setLegendTextSize(15);
	    //mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
	    mRenderer.setZoomButtonsVisible(true);
	    mRenderer.setPointSize(5);		
	    if (mChartView == null) {
	        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.diagnosticsChartLinearLayout);
	        mChartView = ChartFactory.getLineChartView(this.getActivity(), mDataset, mRenderer);
	        // enable the chart click events
	        mRenderer.setClickEnabled(true);
	        mRenderer.setSelectableBuffer(10);
	        mDataset.addSeries(new XYSeries("KS_1 Data"));	        
	        mDataset.addSeries(new XYSeries("KS_2 Data"));
	        XYSeriesRenderer seriesRenderer = new XYSeriesRenderer(); 
	        mRenderer.addSeriesRenderer(seriesRenderer);
	        seriesRenderer.setPointStyle(PointStyle.CIRCLE);
	        seriesRenderer.setFillPoints(true);
	        seriesRenderer.setDisplayChartValues(true);
	        seriesRenderer.setDisplayChartValuesDistance(10);	        
	        seriesRenderer = new XYSeriesRenderer(); 
	        mRenderer.addSeriesRenderer(seriesRenderer);	        
	        seriesRenderer.setPointStyle(PointStyle.CIRCLE);
	        seriesRenderer.setFillPoints(true);
	        seriesRenderer.setDisplayChartValues(true);
	        seriesRenderer.setDisplayChartValuesDistance(10);	        
	        mChartView.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
	            // handle the click event on the chart
	            SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
	            if (seriesSelection == null) {
	              //Toast.makeText(getActivity(), "No chart element", Toast.LENGTH_SHORT).show();
	            } else {
	              // display information of the clicked point
	              Toast.makeText(
	                  DiagnosticsChartFragment.this.getActivity(),
	                  "Chart element in series index " + seriesSelection.getSeriesIndex()
	                      + " data point index " + seriesSelection.getPointIndex() + " was clicked"
	                      + " closest point value X=" + seriesSelection.getXValue() + ", Y="
	                      + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
	            }
	          }
	        });
	        //layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));	        
	        layout.addView(mChartView);
	        mChartView.repaint();	        
	      } else {
	        mChartView.repaint();
	      }	    		
	}
	
	@Override
	public void onResume() {		
		updateData();		
		super.onResume();
	}

	@Override
	public void updateData() {
		if (packet != null) {						
			mDataset.getSeriesAt(0).add(mDataset.getSeriesAt(0).getItemCount(), packet.ks_1);
			mDataset.getSeriesAt(1).add(mDataset.getSeriesAt(1).getItemCount(), packet.ks_2);
	        mChartView.repaint();			
		}
	}

	@Override
	public void setData(Secu3Dat packet) {
		this.packet = (DiagInpDat) packet;		
	}

	@Override
	public Secu3Dat getData() {
		return packet;
	}
}
