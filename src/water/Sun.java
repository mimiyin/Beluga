package water;

import java.nio.FloatBuffer;

import processing.core.*;
import codeanticode.glgraphics.*;

public class Sun {
	PApplet parent;

	GLModel sys;
	GLTexture tex;

	float x = -Global.mWidth;
	float y = Global.mHeight / 3;
	float theta = 0;
	float thetaSpeed = PApplet.TWO_PI / 360;

	int npartTotal = 1000;
	int npartPerFrame = 10;
	float gravity = -0.5f;

	int partLifetime;

	PVector velocities[];
	float lifetimes[];

	Sun(PApplet p) {
		parent = p;
		partLifetime = npartTotal / npartPerFrame;

		sys = new GLModel(parent, npartTotal, GLModel.POINT_SPRITES,
				GLModel.DYNAMIC);
		initColors();
		initPositions();
		initSprites();

		initVelocities();
		initLifetimes();
	}

	void run() {
		GLGraphics renderer = (GLGraphics) parent.g;
		renderer.beginGL();

		updatePositions();
		updateColors();
		updateLifetimes();

		// translate(mouseX, mouseY, 0);

		// Disabling depth masking to properly render semitransparent
		// particles without need of depth-sorting them.
		renderer.setDepthMask(false);
		sys.render();
		renderer.setDepthMask(true);

		renderer.endGL();
	}

	void initSprites() {
		tex = new GLTexture(parent, "fuzzy.png");
		float pmax = sys.getMaxPointSize();
		PApplet.println("Maximum sprite size supported by the video card: "
				+ pmax + " pixels.");
		sys.initTextures(1);
		sys.setTexture(0, tex);
		// Setting the maximum sprite to the 90% of the maximum point size.
		sys.setMaxSpriteSize(0.1f * pmax);
		// Setting the distance attenuation function so that the sprite size
		// is 20 when the distance to the camera is 400.
		sys.setSpriteSize(20, 4000);
		sys.setBlendMode(PApplet.BLEND);
	}

	void initColors() {
		sys.initColors();
		sys.setColors(0, 0);
	}

	void initPositions() {
		sys.beginUpdateVertices();
		FloatBuffer vbuf = sys.vertices;
		float pos[] = { 0, 0, 0, 0 };
		for (int n = 0; n < sys.getSize(); n++) {
			vbuf.position(4 * n);
			vbuf.get(pos, 0, 3);

			pos[0] = x;
			pos[1] = y;
			pos[2] = 0;
			pos[3] = 1; // The W coordinate must be 1.

			vbuf.position(4 * n);
			vbuf.put(pos, 0, 4);
		}
		sys.endUpdateVertices();
	}

	void initVelocities() {
		velocities = new PVector[npartTotal];
		for (int n = 0; n < velocities.length; n++) {
			velocities[n] = new PVector();
		}
	}

	void initLifetimes() {
		// Initialzing particles with negative lifetimes so they are added
		// progresively into the scene during the first frames of the program
		lifetimes = new float[npartTotal];
		int t = -1;
		for (int n = 0; n < lifetimes.length; n++) {
			if (n % npartPerFrame == 0) {
				t++;
			}
			lifetimes[n] = -t;
		}
	}

	void updatePositions() {
		sys.beginUpdateVertices();
		FloatBuffer vbuf = sys.vertices;
		float pos[] = { 0, 0, 0 };
		for (int n = 0; n < sys.getSize(); n++) {
			vbuf.position(4 * n);
			vbuf.get(pos, 0, 3);

			theta += thetaSpeed;
			y -= PApplet.sin(theta)*0.01f;
			x += .001f;

			if (lifetimes[n] == 0) {
				// Respawn dead particle:
				float r = 500 * PApplet.cos(parent.random(0, PApplet.TWO_PI));
				pos[0] = x + parent.random(-r, r);
				pos[1] = y;
				pos[2] = 0;
				float a = parent.random(0, PApplet.TWO_PI);
				velocities[n].x = PApplet.tan(a);
				velocities[n].y = 1;
				velocities[n].z = 0;
			} else {
				// Update moving particle.
				pos[0] += velocities[n].x;
				pos[1] += velocities[n].y;
				pos[2] += velocities[n].z;
				// Updating velocity.
				velocities[n].y += gravity;
			}

			vbuf.position(4 * n);
			vbuf.put(pos, 0, 3);
		}
		vbuf.rewind();
		sys.endUpdateVertices();
	}

	void updateColors() {
		sys.beginUpdateColors();
		FloatBuffer cbuf = sys.colors;
		float col[] = { 0, 0, 0, 0 };
		for (int n = 0; n < sys.getSize(); n++) {
			if (0 <= lifetimes[n]) {
				// Interpolating between alpha 1 to 0:
				float a = 1.0f;
				if (n % 2 == 0)
					a = .5f - (float) (lifetimes[n]) / partLifetime;

				col[0] = .7f;
				col[1] = .9f;
				col[2] = .9f;
				col[3] = a;

				cbuf.position(4 * n);
				cbuf.put(col, 0, 4);
			}
		}
		cbuf.rewind();
		sys.endUpdateColors();
	}

	void updateLifetimes() {
		for (int n = 0; n < sys.getSize(); n += 2) {
			lifetimes[n] += .5f;
			if (lifetimes[n] == partLifetime) {
				lifetimes[n] = 0;
			}
		}
	}
}
