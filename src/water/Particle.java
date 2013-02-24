package water;

import processing.core.*;

public class Particle {

	PApplet parent;
	PVector location;
	
	PVector a, b,c,d,e;

	PVector velocity;
	PVector angle;
	float lifespan;
	float theta = 0;
	float bian = 10;
	int ratio;

	Particle(PVector l, float lifespan_, int ratio_, PApplet p) {
		parent = p;
		location = l.get();
		lifespan = lifespan_;
		ratio = ratio_;
		
		velocity = new PVector(parent.random(-2, -1), parent.random(-1, 2), parent.random(-1, 1));
		angle = new PVector(PApplet.sin(PApplet.PI / 6), PApplet.tan(PApplet.PI / 9), 0);
	}

	void run() {
		update();
		//display();
	}

	// Method to update location
	void update() {
		location.add(velocity);		
		//location.add(angle);
		
		//a = new PVector(-location.y * ratio, (Global.mWidth / ratio) - (location.x / ratio), location.z);
		a = new PVector(-(ratio*Global.mHeight  - location.y), -location.x / ratio, location.z);
		b = new PVector((ratio*Global.mHeight  - location.y), -location.x / ratio, location.z);
		c = new PVector(-ratio*(Global.mHeight  - location.y), -location.x / ratio, location.z);
		d = new PVector(ratio*(Global.mHeight  - location.y), -location.x / ratio, location.z);
		e = new PVector(ratio*(Global.mHeight  - location.y), -location.x / ratio, location.z);

		
		/*b = new PVector(Global.mHeight * ratio - location.y, -location.x / ratio, location.z);
		c = new PVector(Global.mWidth - location.x, -ratio*location.y, location.z);
		d = new PVector(-(Global.mWidth + location.x), -(Global.mWidth / ratio + location.y), location.z);
		e = new PVector(Global.mHeight * ratio - location.y, -((Global.mWidth / ratio) + (location.x / ratio)), location.z);
		*/
		
		lifespan -= 0.001f;
	}

	// Method to display
	void display() {
		
		bian = parent.random(10, parent.random(20, 50));

		parent.noStroke();
		parent.fill(Global.h, Global.s, Global.b, lifespan/2);

		parent.ellipse(location.x, location.y, lifespan / 10, lifespan / 10);	// mirror
		parent.ellipse(a.x, a.y, lifespan, lifespan);

		parent.ellipse(b.x, b.y, lifespan / 10, lifespan / 10);	// mirror b
		parent.ellipse(c.x, c.y, bian, bian); 	// mirror a

		parent.ellipse(d.x, d.y, bian/2, bian);
		parent.ellipse(e.x, e.y, lifespan, lifespan);
	}

	boolean isDead() {
		if (lifespan < 0) {
			return true;
		} else {
			return false;
		}
	}
}
