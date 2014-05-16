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

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

public class LedGauge extends BaseGauge {
	private String textureName;
	private ITexture texture;
	private ITextureRegion textureRegion;
	private Sprite sprite;
	private AlphaModifier modifier;
	private boolean oldFlag;
	private boolean flag;
	
	public LedGauge (int Id, String textureName, float xPos, float yPos) {
		setId (Id);
		this.textureName = textureName;
		setX(xPos);
		setY(yPos);
		flag = getValue() >= 1;
		oldFlag = getValue() >= 1;
	}
	
	public void load (BaseGameActivity activity) throws IOException {
		this.texture = new AssetBitmapTexture(activity.getTextureManager(),activity.getAssets(), this.textureName, TextureOptions.BILINEAR);
		this.textureRegion = TextureRegionFactory.extractFromTexture(this.texture);
		this.texture.load();			
	}
	
	public void attach (Scene scene, VertexBufferObjectManager vertexBufferObjectManager) {
		sprite = new Sprite(getX(), getY(), textureRegion, vertexBufferObjectManager);
		sprite.setAlpha(0);
		scene.attachChild(sprite);		
	}
	
	@Override
	public void setValue(float value, float delay) {
		super.setValue(value, delay);
		flag = value >= 1;
		if (flag != oldFlag) {
			oldFlag = flag;
			if (modifier != null) sprite.unregisterEntityModifier(modifier);
			modifier = new AlphaModifier(delay, flag?0:1, flag?1:0);
			sprite.registerEntityModifier(modifier);			
		}
	}
}
