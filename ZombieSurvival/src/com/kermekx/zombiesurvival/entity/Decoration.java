package com.kermekx.zombiesurvival.entity;

import com.kermekx.engine.drawable.Rectangle2D;
import com.kermekx.engine.position.Vector;
import com.kermekx.engine.scene.Scene;

public class Decoration extends Entity {

	public Decoration(Scene context, Vector position, Vector size, int textureId) {
		super(context, position, size);
		addDrawable(new Rectangle2D(position.getX(), position.getY(), size.getX(), size.getY(), 5, textureId));
	}

	public Decoration(Scene context, Vector position, Vector size, int life, int textureId) {
		super(context, position, size, life);
		addDrawable(new Rectangle2D(position.getX(), position.getY(), size.getX(), size.getY(), 5, textureId));
	}

}