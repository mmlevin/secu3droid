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
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

abstract class BaseGauge {
	private int id;
	private float x;
	private float y;
	private float value;

	int getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
	}

	float getX() {
		return x;
	}

	void setX(float x) {
		this.x = x;
	}

	float getY() {
		return y;
	}

	void setY(float y) {
		this.y = y;
	}
	
	protected abstract void load (BaseGameActivity activity) throws IOException;
	protected abstract void attach (Scene scene, VertexBufferObjectManager vertexBufferObjectManager);

	float getValue() {
		return value;
	}

	void setValue(float value, float delay) {
		this.value = value;
	}
}
