package com.kermekx.engine.renderer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glFlush;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexCoord3f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.awt.Rectangle;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.kermekx.engine.camera.Camera;
import com.kermekx.engine.drawable.Drawable;
import com.kermekx.engine.drawable.list.DisplayList;
import com.kermekx.engine.hud.HUD;
import com.kermekx.engine.mouse.MouseEvent;
import com.kermekx.engine.position.Vector;
import com.kermekx.engine.scene.Scene;

public class Renderer {

	/**
	 * Scene ? afficher
	 */
	private Scene scene;

	/**
	 * rendue de la scene
	 */
	public synchronized void render() {
		if (scene == null)
			return;

		Rectangle bounds = scene.getCamera().getBounds();
		scene.getCamera().setViewModel();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		for (DisplayList dl : scene.getDisplayLists())
			if (dl.should(bounds))
				glCallList(dl.getListID());

		for (int j = scene.getDrawables().length - 1; j >= 0; j--) {
			float k = 0.9f;
			float zPerK = 0.9f / scene.getDrawables()[j].size();
			for (Drawable drawable : scene.getDrawables()[j]) {
				if (drawable.shouldRender(bounds)) {
					float[] color = drawable.getColor();
					glColor3f(color[0], color[1], color[2]);

					int texture = drawable.getTextureId();
					if (texture != -1)
						;
					glBindTexture(GL_TEXTURE_2D, texture);
					int i = 0;
					float angle = drawable.getRotation();
					if (angle != 0) {
						glPushMatrix();
						Vector position = drawable.getPosition();
						glTranslatef(position.getX(), position.getY(), 0);
						glRotatef(angle, 0, 0, 1);
						glTranslatef(-position.getX(), -position.getY(), 0);
					}

					glBegin(GL_TRIANGLES);
					for (Vector vertex : drawable.getVertex()) {
						if (texture != -1 && i % 2 == 0)
							glTexCoord3f(vertex.getX(), vertex.getY(), vertex.getZ() + k);
						else
							glVertex3f(vertex.getX(), vertex.getY(), vertex.getZ() + k);
						i++;
					}
					glEnd();
					if (texture != -1)
						glBindTexture(GL_TEXTURE_2D, 0);
					if (angle != 0)
						glPopMatrix();
					k -= zPerK;
				}
			}
		}

		scene.getCamera().setViewModelHUD();

		for (HUD hud : scene.getHuds()) {
			for (DisplayList dl : hud.getDisplayLists())
				glCallList(dl.getListID());

			for (Drawable drawable : hud.getDrawables()) {
				float[] color = drawable.getColor();
				glColor3f(color[0], color[1], color[2]);

				int texture = drawable.getTextureId();
				if (texture != -1)
					;
				glBindTexture(GL_TEXTURE_2D, texture);
				int i = 0;
				float angle = drawable.getRotation();
				if (angle != 0) {
					glPushMatrix();
					Vector position = drawable.getPosition();
					glTranslatef(position.getX(), position.getY(), 0);
					glRotatef(angle, 0, 0, 1);
					glTranslatef(-position.getX(), -position.getY(), 0);
				}

				glBegin(GL_TRIANGLES);
				for (Vector vertex : drawable.getVertex()) {
					if (texture != -1 && i % 2 == 0)
						glTexCoord3f(vertex.getX(), vertex.getY(), 1);
					else
						glVertex3f(vertex.getX(), vertex.getY(), 1);
					i++;
				}
				glEnd();
				if (texture != -1)
					glBindTexture(GL_TEXTURE_2D, 0);
				if (angle != 0)
					glPopMatrix();
			}
		}

		glFlush();
	}

	/**
	 * modifie la scene ? afficher (null pour rien afficher)
	 * 
	 * @param scene
	 *            scene ? afficher
	 */
	public void setScene(Scene scene) {
		if (scene.getCamera() == null)
			scene.setCamera(new Camera());
		this.scene = scene;
	}

	private boolean click = false;

	/**
	 * Mise ? jour de la scene (?l?ments de la scene)
	 * 
	 * @param delta
	 *            temps depuis la derni?re mise ? jour
	 */
	public synchronized void update(int delta) {
		if (scene != null)
			scene.update(delta);
		
		MouseEvent me = new MouseEvent(
				new Vector(Mouse.getX() * 1920 / Display.getWidth(),
						(Display.getHeight() - Mouse.getY()) * 1080 / Display.getHeight()),
				(Mouse.isButtonDown(0) ? MouseEvent.LEFT_BUTTON : 0)
						| (Mouse.isButtonDown(1) ? MouseEvent.RIGHT_CLICK : 0));
		
		if (me.getClick() == 0) {
			click = false;
			return;
		}
		
		if (!click)
			for (HUD hud : scene.getHuds())
				hud.mouseEvent(me);
		click = true;
	}

	public synchronized void updateAI(int delta) {
		if (scene != null)
			scene.updateAI(delta);
	}

}
