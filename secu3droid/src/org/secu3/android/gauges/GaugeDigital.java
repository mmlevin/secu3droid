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
