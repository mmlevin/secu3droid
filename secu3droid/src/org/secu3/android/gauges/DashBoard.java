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
import java.util.ArrayList;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

public class DashBoard extends BaseGauge {
	
	private ArrayList<BaseGauge> gauges = null;
	
	private float width;
	private float height;
	String textureName;
	private ITexture texture;
	private ITextureRegion textureRegion;
	private Sprite sprite;
	private int color;
	
	public DashBoard(float width, float height, String texture, int color) {
		
		gauges = new ArrayList<BaseGauge>();
		this.textureName = texture;
		this.width = width;
		this.height = height;
	}

	@Override
	public void load(BaseGameActivity activity) throws IOException {
		if (textureName != null) {
			this.texture = new AssetBitmapTexture(activity.getTextureManager(),activity.getAssets(), this.textureName, TextureOptions.BILINEAR);
			this.textureRegion = TextureRegionFactory.extractFromTexture(this.texture);
			this.texture.load();	
		}		
		
		int count = gauges.size();
		for (int i = 0;  i != count; i++) {
			gauges.get(i).load(activity);
		}			
	}
	
	@Override
	public void attach(Scene scene, VertexBufferObjectManager vertexBufferObjectManager) {
		scene.getBackground().setColor((color & 0xff)/255.0f,((color >> 8)&0xff)/255.0f,((color >> 16)&0xff)/255.0f,((color >> 24)&0xff)/255.0f);
		if (textureName != null) {
			sprite = new Sprite(width/2, height/2, textureRegion, vertexBufferObjectManager);
			scene.attachChild(sprite);
		}
		
		int count = gauges.size();
		for (int i = 0;  i != count; i++) {
			gauges.get(i).attach(scene, vertexBufferObjectManager);
		}		
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
	
	public boolean addGauge (BaseGauge gauge) {
		return gauges.add(gauge);
	}	
	
	public void setGaugeValue (int id, float value, float delay) {
		int count = gauges.size();
		for (int i=0; i != count; i++)
			if (gauges.get(i).getId() == id) gauges.get(i).setValue(value, delay);
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
