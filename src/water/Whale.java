package water;

import processing.core.*;
import codeanticode.glgraphics.*;
import toxi.geom.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

class Whale {
	PApplet parent;
	float theta = 0;
	float thetaSpeed;
	float shake;
	float oFactor;
	Vec3D loc;

	float opacity = 0.5f;
	int arms, sparse, sizeMax;

	GLModel whaleDots;
	GLTexture whaleImage;
	int whaleDotsMax;

	float sparseTheta, armsTheta;

	ArrayList<Vec3D> whaleDotLocs = new ArrayList<Vec3D>();

	Whale(PApplet p) {
		parent = p;

	}

	void initGL(int max, String filename) {
		whaleDots = new GLModel(parent, max, GLModel.POINT_SPRITES,
				GLModel.DYNAMIC);
		initColors();
		initPositions();
		initSprites(filename);
	}

	void initColors() {
		whaleDots.initColors();
		whaleDots.setColors(1, 1);
	}

	void initPositions() {
		whaleDots.beginUpdateVertices();
		FloatBuffer vbuf = whaleDots.vertices;
		float pos[] = { 0, 0, 0, 0 };
		for (int n = 0; n < whaleDots.getSize(); n++) {
			vbuf.position(4 * n);
			vbuf.get(pos, 0, 3);

			pos[0] = 0;
			pos[1] = 0;
			pos[2] = 0;
			pos[3] = 1;

			vbuf.position(4 * n);
			vbuf.put(pos, 0, 4);
		}
		whaleDots.endUpdateVertices();

	}

	void initSprites(String filename) {
		whaleImage = new GLTexture(parent, filename);
		whaleDots.initTextures(1);
		whaleDots.setTexture(0, whaleImage);
		whaleDots.setMaxSpriteSize(sizeMax);
		whaleDots.setSpriteSize(sizeMax, 400);
		whaleDots.setBlendMode(PApplet.BLEND);
	}

	void updatePositions(Vec3D loc) {

		whaleDots.beginUpdateVertices();
		FloatBuffer vbuf = whaleDots.vertices;
		float pos[] = { 0, 0, 0 };
		for (int n = 0; n < whaleDots.getSize(); n++) {
			vbuf.position(4 * n);
			vbuf.get(pos, 0, 3);

			Vec3D thisDot = whaleDotLocs.get(n);
			pos[0] = thisDot.x;
			pos[1] = thisDot.y;
			pos[2] = thisDot.z;

			vbuf.position(4 * n);
			vbuf.put(pos, 0, 3);
		}

		vbuf.rewind();
		whaleDots.endUpdateVertices();
	}

	void updateColors(float oFactor) {
		if (Global.die)
		opacity -= .005f;
		opacity -= .00006f;

		whaleDots.beginUpdateColors();
		FloatBuffer cbuf = whaleDots.colors;
		float col[] = { 0, 0, 0, 0 };

		for (int n = 0; n < whaleDots.getSize(); n++) {

			col[0] = 1.0f;
			col[1] = 1.0f;
			col[2] = 1.0f;
			col[3] = parent.random(0, opacity * oFactor);

			cbuf.position(4 * n);
			cbuf.put(col, 0, 4);

		}
		cbuf.rewind();
		whaleDots.endUpdateColors();
	}
}

class Spaceship extends Whale {
	String filename = "fuzzy.png";
	float min = 500;
	float max = 2000;

	float shaky, jian, itheta, alpha, j1, j2, x1, y1, jtheta, beta, x2, y2;
	Vec3D rotateLoc;
	
	Spaceship(PApplet parent) {
		super(parent);
		thetaSpeed = 0.01f;
		sparse = 100;
		arms = 5;
		sparseTheta = PApplet.TWO_PI / sparse;
		armsTheta = PApplet.TWO_PI / arms;
		whaleDotsMax = (sparse * arms);

		if (Global.local)
			sizeMax = 24;
		else
			sizeMax = 63;

		oFactor = 1;

		initGL(whaleDotsMax, filename);
	}

	void run(Vec3D loc) {
		GLGraphics renderer = (GLGraphics) parent.g;
		renderer.beginGL();
		calcShifts(loc);
		updatePositions(loc);
		updateColors(oFactor);
		renderer.setDepthMask(false);
		whaleDots.render();
		renderer.setDepthMask(true);
		renderer.endGL();
	}

	void calcShifts(Vec3D loc) {
		whaleDotLocs = new ArrayList<Vec3D>();
		shake += PApplet.sin(theta) * 0.5f;
		shaky=parent.random(0,shake);

		jian = PApplet.map(PApplet.sin(theta), -1, 1, min, max);

		for (int i = 0; i < sparse; i++) {
			itheta = i * (sparseTheta);
			alpha = theta + itheta;
			j1 = jian * PApplet.sin(PApplet.tan(itheta))+shaky;
			j2 = jian * PApplet.cos(PApplet.tan(itheta))+shaky;

			x1 = j1 * PApplet.sin(alpha);
			y1 = jian * PApplet.cos(alpha);

			for (int j = 0; j < arms; j++) {
				rotateLoc = loc.add(j1, 0, 0);

				jtheta = j * (armsTheta);
				beta = jtheta - theta;
				x2 = x1 * PApplet.cos(-beta);
				y2 = (y1 - j2) * PApplet.sin(-beta);

				rotateLoc.addSelf(x2, y2, 0);
				whaleDotLocs.add(rotateLoc);
			}
		}
		theta += PApplet.map(PApplet.cos(parent.random(0.003f, 0.01f)), -1, 1,
				-0.01f, 0.01f);

	}
}

class Baby extends Whale {
	String filename = "crisp.png";
	int min = 20;
	int max = 400;
	int insideMax = max / 4;
	
	float jj, j1, j2, amotion, bmotion, itheta, alpha, x1, y1, jtheta, beta, onedi, twobx, twoby, xj, yj, x2, y2;
	Vec3D rotateLoc, inside;

	Baby(PApplet parent) {
		super(parent);
		thetaSpeed = 0.005f;
		sparse = 46;
		arms = 16;
		sparseTheta = PApplet.TWO_PI / sparse;
		armsTheta = PApplet.TWO_PI / arms;
		whaleDotsMax = (sparse * ((arms*4)/3));

		if (Global.local)
			sizeMax = 2;
		else
			sizeMax = 12;

		oFactor = 0;

		initGL(whaleDotsMax, filename);

	}

	void run(Vec3D loc) {
		GLGraphics renderer = (GLGraphics) parent.g;
		renderer.beginGL();
		calcShifts(loc);
		updatePositions(loc);
		
		oFactor += 0.01f;
		oFactor = PApplet.constrain(oFactor, 0, .5f);
		updateColors(oFactor);
		renderer.setDepthMask(false);
		whaleDots.render();
		renderer.setDepthMask(true);
		renderer.endGL();
	}

	void calcShifts(Vec3D loc) {
		whaleDotLocs = new ArrayList<Vec3D>();

		jj = PApplet.map(PApplet.sin(theta), -1, 1, min, max);
		j1 = jj * PApplet.sin(theta);							
		j2 = .5f * max * PApplet.sin(theta + PApplet.TWO_PI);
		
		amotion=j2*.1f;
		bmotion=j1*.1f;

		for (int i = 0; i < sparse; i++) {
			itheta = i * (sparseTheta);
			alpha = theta + itheta;

			x1 = j1 * PApplet.sin(alpha + PApplet.PI) - amotion*i;   
			y1 = j1 * PApplet.cos(alpha + PApplet.PI) + bmotion*i;       

			rotateLoc = loc.add(x1, y1, 0);

			onedi=theta+i; 
			twobx = insideMax * PApplet.sin(onedi);
			twoby = insideMax * PApplet.sin(onedi*2);
			
			for (int j = 0; j < arms; j++) {
				jtheta = j * (armsTheta);
				beta = theta * 2 + jtheta;

				if (j % 3 == 0) {
					inside = rotateLoc.add(twobx, twoby, 0);
					whaleDotLocs.add(inside);
				}

				xj=(j2 + j * 10);
				yj=(j1 + j * 2);
				
				x2 = xj * PApplet.sin(-beta);
				y2 = yj * PApplet.cos(-beta);

				rotateLoc.addSelf(x2, y2, 0);
				whaleDotLocs.add(new Vec3D(rotateLoc));
			}
		}
		
		theta += PApplet.map(PApplet.cos(parent.random(0.003f, 0.01f)), -1, 1,
				-0.01f, 0.01f);
	}
}
