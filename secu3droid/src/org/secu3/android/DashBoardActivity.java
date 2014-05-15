package org.secu3.android;

import java.io.IOException;

import org.andengine.AndEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.secu3.android.api.utils.ResourcesUtils;
import org.secu3.android.gauges.DashBoard;
import org.secu3.android.gauges.GaugeAnalog;
import org.secu3.android.gauges.GaugeDigital;
import org.secu3.android.gauges.LedGauge;
import org.secu3.android.gauges.SpriteGauge;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.os.Bundle;

public class DashBoardActivity extends SimpleBaseGameActivity {	
	private DashBoard dashBoard = null;
		
	private Camera mCamera;
		
	protected int odometer;
	
	public void createDashboardFromXml (int xmlId)
	{
		String attr, attrValue;
		try {
			XmlPullParser xpp = getResources().getXml(xmlId);

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()){
				case XmlPullParser.START_TAG:
					String name = xpp.getName();
					if (name.equals("DashBoard")) {		
						String textureName = null;
						String width = null;
						String height = null;
						
						int count = xpp.getAttributeCount();
						for (int i = 0; i != count; i++) {
							attr = xpp.getAttributeName(i);
							attrValue = xpp.getAttributeValue(i);
							if (attr.equals("width")) {
								width = attrValue;
							} else
							if (attr.equals("height")) {
								height = attrValue;
							} else
							if (attr.equals("texture")) {
								textureName = attrValue;
							}
						}
						dashBoard = new DashBoard(Float.parseFloat(width), Float.parseFloat(height), textureName);
					} else
					if (name.equals("GaugeAnalog")) {				
						String scaleTexture = null;
						String labelsTexture = null;
						String arrowTexture = null;
						String degreesPerUnit = null;
						String beginAngle = null;
						String scaleX = null;
						String scaleY = null;
						String labelsX = null;
						String labelsY = null;
						String arrowX = null;
						String arrowY = null;
						String arrowAnchorX = null;
						String arrowAnchorY = null;
						String minValue = null;
						String maxValue = null;
						
						int id = 0;
						
						int count = xpp.getAttributeCount();
						for (int i = 0; i != count; i++) {
							attr = xpp.getAttributeName(i);
							attrValue = xpp.getAttributeValue(i);
							if (attr.equals("id")) {
								if (ResourcesUtils.isResource(attrValue)) id = ResourcesUtils.referenceToInt(attrValue);
							} else
							if (attr.equals("scale_texture")) {
								scaleTexture = attrValue;
							} else
							if (attr.equals("labels_texture")) {
								labelsTexture = attrValue;
							} else
							if (attr.equals("arrow_texture")) {
								arrowTexture = attrValue;
							} else	
							if (attr.equals("degrees_per_unit")) {
								degreesPerUnit = attrValue;
							} else				
							if (attr.equals("begin_angle")) {
								beginAngle = attrValue;
							} else			
							if (attr.equals("scale_x")) {
								scaleX = attrValue;
							} else								
							if (attr.equals("scale_y")) {
								scaleY = attrValue;
							} else
							if (attr.equals("labels_x")) {
								labelsX = attrValue;
							} else								
							if (attr.equals("labels_y")) {
								labelsY = attrValue;
							} else		
							if (attr.equals("arrow_x")) {
								arrowX = attrValue;
							} else								
							if (attr.equals("arrow_y")) {
								arrowY = attrValue;
							} else
							if (attr.equals("arrow_anchor_x")) {
								arrowAnchorX = attrValue;
							} else								
							if (attr.equals("arrow_anchor_y")) {
								arrowAnchorY = attrValue;
							} else	
							if (attr.equals("min_value")) {
								minValue = attrValue;
							} else								
							if (attr.equals("max_value")) {
								maxValue = attrValue;
							};							
						}
						dashBoard.addGauge(
								new GaugeAnalog(id, Float.parseFloat(degreesPerUnit), Float.parseFloat(beginAngle), Float.parseFloat(minValue), Float.parseFloat(maxValue),
										scaleTexture, labelsTexture, arrowTexture,
										Float.parseFloat(scaleX), Float.parseFloat(scaleY), Float.parseFloat(labelsX), Float.parseFloat(labelsY), Float.parseFloat(arrowX), Float.parseFloat(arrowY), Float.parseFloat(arrowAnchorX), Float.parseFloat(arrowAnchorY))
								);
					} else
						if (name.equals("GaugeDigital")) {				
							String font = null;
							String format = null;
							String color = null;
							String size = null;
							String x = null;
							String y = null;

							int id = 0;
							
							int count = xpp.getAttributeCount();
							for (int i = 0; i != count; i++) {
								attr = xpp.getAttributeName(i);
								attrValue = xpp.getAttributeValue(i);
								if (attr.equals("id")) {
									if (ResourcesUtils.isResource(attrValue)) id = ResourcesUtils.referenceToInt(attrValue);
								} else
								if (attr.equals("x")) {
									x = attrValue;
								} else
								if (attr.equals("y")) {
									y = attrValue;
								} else
								if (attr.equals("size")) {
									size = attrValue;
								} else
								if (attr.equals("color")) {
									color = attrValue;
								} else	
								if (attr.equals("font")) {
									font = attrValue;
								} else
								if (attr.equals("format")) {
									format = attrValue;
								};
								
							}
							dashBoard.addGauge(
									new GaugeDigital(id, Float.parseFloat(x), Float.parseFloat(y), font, Float.parseFloat(size), format, Integer.parseInt(color))
									);
					} else		
					if (name.equals("Sprite")) {
						String textureName = null;
						String x = null;
						String y = null;
						int id = 0;
						
						int count = xpp.getAttributeCount();
						for (int i = 0; i != count; i++) {
							attr = xpp.getAttributeName(i);
							attrValue = xpp.getAttributeValue(i);
							if (attr.equals("id")) {
								if (ResourcesUtils.isResource(attrValue)) id = ResourcesUtils.referenceToInt(attrValue);
							} else
							if (attr.equals("texture")) {
								textureName = attrValue;
							} else
							if (attr.equals("x")) {
								x = attrValue;								
							} else
							if (attr.equals("y")) {
								y = attrValue;
							}
						}						
						dashBoard.addGauge(new SpriteGauge(id, textureName, Float.parseFloat(x), Float.parseFloat(y)));
					} else
					if (name.equals("Led")) {
						String textureName = null;
						String x = null;
						String y = null;
						int id = 0;
						
						int count = xpp.getAttributeCount();
						for (int i = 0; i != count; i++) {
							attr = xpp.getAttributeName(i);
							attrValue = xpp.getAttributeValue(i);
							if (attr.equals("id")) {
								if (ResourcesUtils.isResource(attrValue)) id = ResourcesUtils.referenceToInt(attrValue);
							} else
							if (attr.equals("texture")) {
								textureName = attrValue;
							} else
							if (attr.equals("x")) {
								x = attrValue;								
							} else
							if (attr.equals("y")) {
								y = attrValue;
							}
						}						
						dashBoard.addGauge(new LedGauge(id, textureName, Float.parseFloat(x), Float.parseFloat(y)));						
					}
				}
				xpp.next();				
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		createDashboardFromXml(R.xml.nfs_portrait);		
		this.mCamera = new Camera(0, 0, dashBoard.getWidth(), dashBoard.getHeight());
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new RatioResolutionPolicy(dashBoard.getWidth(), dashBoard.getHeight()), this.mCamera);
	}

	@Override
	protected void onCreateResources() throws IOException {
		dashBoard.load(this);					
	}

	@Override
	protected Scene onCreateScene() {
		odometer = 0;
		this.mEngine.registerUpdateHandler(new FPSLogger());
		final Scene scene = new Scene();
				
		dashBoard.attach(scene, this.getVertexBufferObjectManager());
				
		scene.registerUpdateHandler(new TimerHandler(1 / 10.0f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				odometer++;
				if (odometer >= 999999) odometer = 0;
				float speed = (float) (110 - 110*Math.cos(odometer/50.0f));
				float press = speed / 2;
				float temp = speed / 2;
				float voltage = speed / 10;
				float rpm = speed * 25;
				dashBoard.setGaugeValue(R.id.GaugeOdometer, odometer,1/10f);
				dashBoard.setGaugeValue(R.id.GaugeSpeedometer, speed,1/10f);
				dashBoard.setGaugeValue(R.id.GaugeManometer, press,1/10f);
				dashBoard.setGaugeValue(R.id.GaugeTermometer, temp,1/10f);
				dashBoard.setGaugeValue(R.id.GaugeVoltmeter, voltage,1/10f);
				dashBoard.setGaugeValue(R.id.GaugeTachometer, rpm,1/10f);
				
				int lamp = (int) (speed / 120);
				dashBoard.setGaugeValue(R.id.LedCheckEngine, lamp, 0.3f);
				dashBoard.setGaugeValue(R.id.LedGasoline, lamp, 0.3f);
				dashBoard.setGaugeValue(R.id.LedEco, lamp, 0.3f);
				dashBoard.setGaugeValue(R.id.LedPower, lamp, 0.3f);
				dashBoard.setGaugeValue(R.id.LedChoke, lamp, 0.3f);
			}
		}));
					
		return scene;
	}

}
