package org.secu3.android.gauges;

import java.io.IOException;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

public class SpriteGauge extends BaseGauge {
	private String textureName;
	private ITexture texture;
	private ITextureRegion textureRegion;
	private Sprite sprite;
	
	public SpriteGauge (int Id, String textureName, float xPos, float yPos) {
		setId (Id);
		this.textureName = textureName;
		setX(xPos);
		setY(yPos);
	}
	
	public void load (BaseGameActivity activity) throws IOException {
		this.texture = new AssetBitmapTexture(activity.getTextureManager(),activity.getAssets(), this.textureName, TextureOptions.BILINEAR);
		this.textureRegion = TextureRegionFactory.extractFromTexture(this.texture);
		this.texture.load();			
	}
	
	public void attach (Scene scene, VertexBufferObjectManager vertexBufferObjectManager) {
		sprite = new Sprite(getX(), getY(), textureRegion, vertexBufferObjectManager);
		scene.attachChild(sprite);		
	}
}
