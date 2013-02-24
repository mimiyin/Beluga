package water;

import processing.core.PApplet;
import toxi.geom.*;

public class Swimmer {

	PApplet parent;
	Vec3D loc;

	Swimmer(PApplet p) {
		parent = p;
	}
}

class Mouser extends Swimmer {
	PluckWake pluckMouse;
	StreamWake streamMouse;

	Mouser(PApplet parent) {
		super(parent);
		loc = new Vec3D(0, 0, 0);
		pluckMouse = new PluckWake(16, parent);
		streamMouse = new StreamWake(loc, 24, parent);
	}

	void run(Vec2D mouseLoc) {
		int mouseY = Global.horizon;
		int mouseZ = (int) PApplet.map(mouseLoc.y, -Global.mHeight,
				Global.mHeight, -Global.ROWS * Global.REST_LENGTH, 0);
		loc.set(mouseLoc.x, mouseY, mouseZ);
		pluckMouse.run(loc);
		streamMouse.run(loc, true);
		if (Global.debug)
			display();
	}

	void display() {
		parent.stroke(0, 0, 100);
		parent.strokeWeight(10);
		parent.point(loc.x, loc.y, loc.z);
	}

}

class TrampSwimmer extends Swimmer {
	PluckWake pluckTramp;

	TrampSwimmer(PApplet parent) {
		super(parent);
		loc = new Vec3D(0, 0, 0);
		pluckTramp = new PluckWake(10, parent);
	}

	void run(Vec3D trampLoc) {
		loc.set(trampLoc);
		pluckTramp.run(loc);
		if (Global.debug)
			display();
	}

	void display() {
		parent.stroke(0, 0, 100);
		parent.strokeWeight(100);
		parent.point(loc.x, loc.y, loc.z);
	}
}

class SpaceshipSwimmer extends Swimmer {
	StreamWake spaceshipSwimmer;
	Spaceship whale;
	Vec3D start;
	float zfactor = 0.5f;
	float swimmerDir;

	SpaceshipSwimmer(PApplet parent) {
		super(parent);
		if (Global.goingLeft)
			loc = new Vec3D(Global.MESH_WIDTH * zfactor, Global.horizon,
					-Global.MESH_DEPTH * .1f);
		else
			loc = new Vec3D(-Global.MESH_WIDTH * zfactor, Global.horizon,
					-Global.MESH_DEPTH * .1f);

		start = new Vec3D(loc.x, loc.y, loc.z);
		spaceshipSwimmer = new StreamWake(loc, 5, parent);
		whale = new Spaceship(parent);

		if (Global.goingLeft)
			swimmerDir = -9;
		else
			swimmerDir = 9;
	}

	void run() {
		spaceshipSwimmer.run(loc, false);
		float diff = spaceshipSwimmer.buoy().y - loc.y;
		// PApplet.println("Diff: " + diff);
		loc.y += diff / 100;
		float theta = PApplet.map(loc.x, -Global.MESH_WIDTH * zfactor,
				Global.MESH_WIDTH * zfactor, 0, PApplet.PI);
		loc.z = -Global.MESH_DEPTH * 0.875f * PApplet.sin(theta);
		float xdelta = swimmerDir * PApplet.sin(theta)-1;
		loc.addSelf(xdelta, 0, 0);
		//PApplet.println("Where is swimmer? " + xdelta);

		// Display
		whale.run(loc);

	}
}

class BabySwimmer extends Swimmer {
	StreamWake babySwimmer;
	Baby whale;
	boolean swimming = false;
	
//	int y = (int)parent.random(0, 5);
//	int x = (int)parent.random(0, 99);
//	
//	Vec3D loc = new Vec3D(Global.myMesh[y][x].x, Global.horizon, Global.myMesh[y][x].z);

	Vec3D loc = new Vec3D((int) parent.random(-Global.MESH_WIDTH * .67f,
			Global.MESH_WIDTH * .67f), Global.horizon, (int) parent.random(
			-Global.MESH_DEPTH*.875f, -Global.MESH_DEPTH * 0.5f));
	
	float swimmerDir;

	BabySwimmer(PApplet parent) {
		super(parent);
		babySwimmer = new StreamWake(loc, 5, parent);
		whale = new Baby(parent);
		
		if(loc.x == 14617.0) loc.x = 18000.0f;

		if (Global.goingLeft)
			swimmerDir = -10;
		else
			swimmerDir = 10;
		
		//PApplet.println(loc.x);
	}

	void run(boolean swimming) {
		babySwimmer.run(loc, false);
		float diff = babySwimmer.buoy().y - loc.y;
		// PApplet.println("Diff: " + diff);
		loc.y += diff / 100;
		if (swimming) {
			loc.addSelf(swimmerDir, 0, 0);
		}
		// PApplet.println("Where is swimmer? " + loc.x);

		// Display
		whale.run(loc);

	}
}
