package org.secu3.android.gauges;

import java.io.IOException;

import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

public abstract class BaseGauge {
	private int id;
	private float x;
	private float y;
	private float value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public abstract void load (BaseGameActivity activity) throws IOException;	
	public abstract void attach (Scene scene, VertexBufferObjectManager vertexBufferObjectManager);

	public float getValue() {
		return value;
	}

	public void setValue(float value, float delay) {
		this.value = value;
	}
}
