package water;

import java.util.ArrayList;
import java.nio.FloatBuffer;

import processing.core.*;
import codeanticode.glgraphics.*;

public class Flock {
	PApplet parent;
	GLModel birdies;
	GLTexture birdImage;
	int ind = 0;
	int birdsMax = 500;
	float theta = 0;
	float pmax = 63;
	float diam = 20;

	ArrayList<Bird> birds = new ArrayList<Bird>();
	PVector[] birdDots = new PVector[birdsMax];

	Flock(PApplet p) {
		parent = p;
		birdies = new GLModel(parent, birdsMax, GLModel.POINT_SPRITES,
				GLModel.DYNAMIC);
		initColors();
		initPositions();
		initSprites();
		initFlock();		
	}

	void run() {
		GLGraphics renderer = (GLGraphics) parent.g;
		renderer.beginGL();

		updatePositions();
		updateColors();
		updateSprites();

		// Disabling depth masking to properly render semitransparent
		// particles without need of depth-sorting them.
		renderer.setDepthMask(false);
		birdies.render();
		renderer.setDepthMask(true);

		renderer.endGL();
	}

	void initFlock() {
		for (int b = 0; b < birdsMax; b++) {
			PVector loc = new PVector(0,Global.mWidth);
			birds.add(new Bird(loc, 30.0f, .5f, false, parent));
			birdDots[b] = new PVector(loc.x, loc.y);
		}
	}

	void updatePositions() {
		theta += 0.01f;

		if (ind < birdsMax) {
			Bird thisBird = birds.get(ind);
			thisBird.flying = true;
			ind++;
		}

		for (Bird b : birds) {
			b.run(birds); // Passing the entire list of boids to each boid
							// individually
		}
		for (int b = 0; b < birds.size(); b++) {
			Bird thisBird = (Bird) birds.get(b);
			birdDots[b].set(thisBird.loc);
		}

		birdies.beginUpdateVertices();
		FloatBuffer vbuf = birdies.vertices;
		float pos[] = { 0, 0, 0 };

		for (int n = 0; n < birdies.getSize(); n++) {
			PVector thisBirdDot = (PVector) birdDots[n];
			vbuf.position(4 * n);
			vbuf.get(pos, 0, 3);

			// Update moving particle.
			pos[0] = thisBirdDot.x;
			pos[1] = thisBirdDot.y;
			pos[2] = -500;

			vbuf.position(4 * n);
			vbuf.put(pos, 0, 3);
		}

		vbuf.rewind();
		birdies.endUpdateVertices();
	}

	void updateColors() {
		birdies.beginUpdateColors();
		FloatBuffer cbuf = birdies.colors;
		float col[] = { 0, 0, 0, 0 };

		for (int n = 0; n < birdies.getSize(); n++) {

			col[0] = 1;
			col[1] = 1;
			col[2] = 1;
			col[3] = parent.noise(Global.t) * .5f;

			cbuf.position(4 * n);
			cbuf.put(col, 0, 4);
		}
		cbuf.rewind();
		birdies.endUpdateColors();
	}
	
	void updateSprites() {
		diam = PApplet.sin(theta)*pmax;
		birdies.setMaxSpriteSize(diam);
		// Setting the distance attenuation function so that the sprite size
		// is 20 when the distance to the camera is 400.
		birdies.setSpriteSize(diam, 400);
	}

	void initColors() {
		birdies.initColors();
		birdies.setColors(0, 0);
	}

	void initPositions() {
		birdies.beginUpdateVertices();
		FloatBuffer vbuf = birdies.vertices;
		float pos[] = { 0, 0, 0, 0 };
		for (int n = 0; n < birdies.getSize(); n++) {
			vbuf.position(4 * n);
			vbuf.get(pos, 0, 3);

			pos[0] = 0;
			pos[1] = 0;
			pos[2] = 0;
			pos[3] = 1; // The W coordinate must be 1.

			vbuf.position(4 * n);
			vbuf.put(pos, 0, 4);
		}
		birdies.endUpdateVertices();

	}

	void initSprites() {
		birdImage = new GLTexture(parent, "fuzzy.png");
		float pmax = birdies.getMaxPointSize();
		PApplet.println("Maximum sprite size supported by the video card: "
				+ pmax + " pixels.");
		birdies.initTextures(1);
		birdies.setTexture(0, birdImage);
		// Setting the maximum sprite to the 90% of the maximum point size.
		birdies.setMaxSpriteSize(pmax);
		// Setting the distance attenuation function so that the sprite size
		// is 20 when the distance to the camera is 400.
		birdies.setSpriteSize(pmax, 400);
		birdies.setBlendMode(PApplet.BLEND);
	}
}
