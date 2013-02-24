package water;

import processing.core.*;
import toxi.geom.Vec3D;
import codeanticode.glgraphics.*;

class SkySky {
	PApplet parent;
	SkySky(PApplet p) {
		parent = p;
	}

	int NUM_LINES = 750;
	int NUM_PTS_PER_LINE = 2;
	int NUM_POINTS = NUM_LINES * NUM_PTS_PER_LINE;

	void startUpdate(GLModel model, int NUM_POINTS, float max) {

		model.beginUpdateColors();

		for (int i = 0; i < NUM_POINTS; i++) {
			model.updateColor(i, 0, 0, 100, max);
		}

		model.endUpdateColors();
		model.beginUpdateVertices();
	}

	void endUpdate(GLModel model) {
		model.endUpdateVertices();
		GLGraphics renderer = (GLGraphics) parent.g;
		renderer.beginGL();
		renderer.model(model);
		renderer.endGL();
	}
}

class Moon extends SkySky {
	GLModel moon;
	float bluMax = 1f;
	float bluMin = .4f;
	float blu = 0;

	float thetaMin = PApplet.PI / 2;
	float thetaMax = PApplet.TWO_PI * 1.25f;
	float thetasky = thetaMin;
	float thetaspeed = 1 / (PApplet.TWO_PI * 60);

	float jjMax = Global.mWidth*8;
	float jMax = Global.mWidth*10;
	
	Vec3D center = new Vec3D(Global.mWidth*.35f, Global.mHeight*.5f, -Global.MESH_DEPTH * .8f);

	float miss, polo, patha, smallsky, ouchsky, jj, j, x, y, x2, y2, misssky, skyx, skyy;
	
	Moon(PApplet parent) {
		super(parent);
		moon = new GLModel(parent, NUM_POINTS, GLModel.LINES, GLModel.DYNAMIC);
		moon.initColors();
	}

	void run() {
		if (Global.die)
			blu -= .1f;
		else
			blu = PApplet.map(Global.b, 0, 100, bluMin, bluMax);

		blu = PApplet.constrain(blu, bluMin, bluMax);
		startUpdate(moon, NUM_POINTS, blu);
		calc();
		endUpdate(moon);
	}

	void calc() {
		
		
		thetasky += thetaspeed;
		smallsky=thetasky/3;

		if (thetasky < thetaMin || thetasky > thetaMax)
			thetaspeed *= -1;

		int index = 0;

		for (int i = 0; i < NUM_LINES; i++) {

			miss += (PApplet.TWO_PI / NUM_LINES);
			ouchsky=thetasky+miss;
			
			jj = PApplet.map(PApplet.cos(ouchsky * 5), -1, 1,1, jjMax);
			j = PApplet.map(PApplet.sin(ouchsky * 9), -1, 1, 1, jMax);
  
			x = jj * PApplet.sin(smallsky) + 3000;
			y = j * PApplet.sin(smallsky) + 1500; 

			x2 = (jj * PApplet.sin(thetasky))* PApplet.sin(smallsky);
			y2 = (j * PApplet.sin(thetasky))* PApplet.cos(smallsky);

			misssky=miss+thetasky*2;
			skyx = x * PApplet.cos(misssky) + center.x;
			skyy = y * PApplet.sin(misssky) + center.y; 
			
			moon.updateVertex(index, skyx, skyy, center.z);
			index++;

			float x3 = x2 * PApplet.sin(misssky) + center.x;
			float y3 = y2 * PApplet.sin(misssky) + center.y; 

			moon.updateVertex(index, x3, y3, center.z);
			index++;
		}
	}
}

class Starfish extends SkySky {

	GLModel starfish;
	int NUM_LINES = 15;
	int NUM_PTS_PER_LINE = 2;
	int NUM_POINTS = NUM_LINES * NUM_PTS_PER_LINE;

	float theta = -1;;
	float max = 60;
	
	float starmin=Global.mHeight / 40;
	float starmax=Global.mWidth / 240;
	
	float xarm= 32;
	float yarm= 16; 
	
	float enterlocX=parent.random(-Global.mWidth*2.5f, Global.mWidth*2);
	float enterlocY=parent.random(-Global.mHeight*1f,Global.mHeight*.9f);

	int index;
	
	float miss, j, jj, polo, startime, x, y, x2, y2, xstar, ystar;
	
	Vec3D loc= new Vec3D(enterlocX,enterlocY,0);

	
	Starfish(PApplet parent) {
		super(parent);
		starfish = new GLModel(parent, NUM_POINTS, GLModel.LINES,
				GLModel.DYNAMIC);
		starfish.initColors();

	}

	void run() {
		if (Global.die)
			max -= 1;

		startUpdate(starfish, NUM_POINTS, max);
		calc();
		endUpdate(starfish);
	}

	void calc() {

		index = 0;
		theta += 1 / ((PApplet.TWO_PI) * 30);

		for (int i = 0; i < NUM_LINES; i++) {
			miss += ((PApplet.TWO_PI) / NUM_LINES);
			startime=miss+theta;

			x = PApplet.map(PApplet.sin((startime) * xarm), -1, 1, 0,
					starmin); 
			y  = PApplet.map(PApplet.cos((startime) * yarm), -1, 1, 0,
					starmax);

			x2 = ( x * PApplet.sin(startime ));
			y2 = (y * PApplet.sin(startime));
			
  			ystar=loc.y*PApplet.tan(theta*2)+i;
  			xstar=loc.x*PApplet.cos((theta))+i;
			
			starfish.updateVertex(index,xstar+x2, ystar+y2, 0);
			index++;
			starfish.updateVertex(index, xstar, ystar, 0);
			index++;

		}
	}
}






	
	
	
