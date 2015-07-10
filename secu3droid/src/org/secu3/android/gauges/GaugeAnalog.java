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

import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

public class GaugeAnalog extends BaseGauge {	
	private float degreesPerUnit;
	private float beginAngle;
	private float minValue;
	private float maxValue;
	private float value;
	
	private String scaleTextureName;
	private ITextureRegion scaleTextureRegion;
	private float scaleX;
	private float scaleY;
	
	private String labelsTextureName;
	private ITextureRegion labelsTextureRegion;
	private float labelsX;
	private float labelsY;
	
	private String arrowTextureName;
	private ITextureRegion arrowTextureRegion;
	private Sprite arrowSprite;
	private float arrowX;
	private float arrowY;
	private float arrowAnchorX;
	private float arrowAnchorY;
	private RotationModifier modifier = null;

	public GaugeAnalog (int Id, float degreesPerUnit, float beginAngle, float minValue, float maxValue,
					String scaleTexture, String labelsTexture, String arrowTexture, float scaleX, float scaleY, float labelsX, float labelsY,
					float arrowX, float arrowY, float arrowAnchorX, float arrowAnchorY)
	{
		setId(Id);
		this.degreesPerUnit = degreesPerUnit;
		this.beginAngle = beginAngle;
		this.minValue = minValue;
		this.maxValue = maxValue;
		
		this.value = this.minValue;
		
		this.scaleTextureName = scaleTexture;
		this.labelsTextureName = labelsTexture;
		this.arrowTextureName = arrowTexture;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.labelsX = labelsX;
		this.labelsY = labelsY;
		this.arrowX = arrowX;
		this.arrowY = arrowY;
		this.arrowAnchorX = arrowAnchorX;
		this.arrowAnchorY = arrowAnchorY;
	}
	
	
	@Override
	public void load(BaseGameActivity activity) throws IOException {
		ITexture scaleTexture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), scaleTextureName, TextureOptions.BILINEAR);
		this.scaleTextureRegion = TextureRegionFactory.extractFromTexture(scaleTexture);
		scaleTexture.load();
		ITexture labelsTexture = new AssetBitmapTexture(activity.getTextureManager(),activity.getAssets(), this.labelsTextureName, TextureOptions.BILINEAR);
		this.labelsTextureRegion = TextureRegionFactory.extractFromTexture(labelsTexture);
		labelsTexture.load();
		ITexture arrowTexture = new AssetBitmapTexture(activity.getTextureManager(),activity.getAssets(), this.arrowTextureName, TextureOptions.BILINEAR);
		this.arrowTextureRegion = TextureRegionFactory.extractFromTexture(arrowTexture);
		arrowTexture.load();
	}

	@Override
	public void attach(Scene scene, VertexBufferObjectManager vertexBufferObjectManager) {
		Sprite scaleSprite = new Sprite(scaleX, scaleY, scaleTextureRegion, vertexBufferObjectManager);
		scene.attachChild(scaleSprite);				
		Sprite labelsSprite = new Sprite(labelsX, labelsY, labelsTextureRegion, vertexBufferObjectManager);
		scene.attachChild(labelsSprite);
		arrowSprite = new Sprite(arrowX, arrowY, arrowTextureRegion, vertexBufferObjectManager);
		arrowSprite.setAnchorCenterX(arrowAnchorX/arrowSprite.getWidth());
		arrowSprite.setAnchorCenterY(arrowAnchorY/arrowSprite.getHeight());
		arrowSprite.setRotation(calc_angle());
		scene.attachChild(arrowSprite);
	}

	public float getValue() {
		return value;
	}

	private float calc_angle () {
		return beginAngle+(value-minValue)*degreesPerUnit;
	}
	
	@Override
	public void setValue(float value, float delay) {
		if (value <= minValue) value = minValue;
		if (value >= maxValue) value = maxValue;
		this.value = value;		
		if (modifier != null) arrowSprite.unregisterEntityModifier(modifier);
		modifier = new RotationModifier(delay, arrowSprite.getRotation() , calc_angle());
		arrowSprite.registerEntityModifier(modifier);
	}

}
