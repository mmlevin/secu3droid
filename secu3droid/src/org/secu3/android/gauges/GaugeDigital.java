package org.secu3.android.gauges;

import java.io.IOException;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.align.HorizontalAlign;


public class GaugeDigital extends BaseGauge {
	int type;
	
	private float value;
	
	private Font font;
	private String fontName;
	private float fontSize;
	private Text text;
	private int color;
	
	private String format;
	
	public GaugeDigital (int Id, float x, float y, String font, float Size, String format, int Color) {
		setId (Id);
		setX(x);
		setY(y);
		this.fontName = font;
		this.fontSize = Size;
		this.color = Color;
		this.format = format;
		this.value = 0;
	}
	
	@Override
	public void load(BaseGameActivity activity) throws IOException {
		FontFactory.setAssetBasePath("font/");
		this.font = FontFactory.createFromAsset(activity.getFontManager(),activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR, activity.getAssets(), fontName, fontSize, true, color);
		this.font.load();
	}

	@Override
	public void attach(Scene scene, VertexBufferObjectManager vertexBufferObjectManager) {
		text = new Text (getX(),getY(),font,String.format(format, value),15,new TextOptions(HorizontalAlign.LEFT),vertexBufferObjectManager);
		text.setAnchorCenterX(0);
		text.setPosition(getX()-text.getWidth()/2, getY());
		scene.attachChild(text);
	}

	public float getValue() {
		return value;
	}

	@Override
	public void setValue(float value, float delay) {
		this.value = value;
		text.setText(String.format(format, value));
	}

}
