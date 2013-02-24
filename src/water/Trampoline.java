package water;

import processing.core.*;
import toxi.processing.ToxiclibsSupport;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.constraints.SphereConstraint;

public class Trampoline {

	PApplet parent;
	// Toxi physics stuff
	ToxiclibsSupport gfx;

	SphereConstraint tramp;
	int whichTramp;
	int diameter;
	Vec3D loc;
	float velocity;
	float min, max;

	Trampoline(int whichTramp_, PApplet p) {
		parent = p;
		
		gfx = new ToxiclibsSupport(parent);
		whichTramp = whichTramp_;
		if (whichTramp == 1)
			loc = new Vec3D(-Global.MESH_WIDTH * 0.33f, 0,
					-Global.MESH_DEPTH * 0.33f);
		else if (whichTramp == 2)
			loc = new Vec3D(0, 0, -Global.MESH_DEPTH);
		else
			loc = new Vec3D(Global.MESH_WIDTH * 0.66f, 0,
					-Global.MESH_DEPTH * 0.33f);
		diameter = 1000;

		min = Global.mHeight * 5;
		max = -Global.mHeight;

		initTramp();
	}

	void initTramp() {
		tramp = new SphereConstraint(new Sphere(loc, diameter), false);
		VerletPhysics.addConstraintToAll(tramp, Global.physics.particles);
		
		parent.fill(360, 100, 100);
		Sphere s = tramp.sphere;
		s.radius *= 0.99;
		gfx.sphere(s, 12);
	}

	void run(int dir) {
		loc.addSelf(0, velocity, 0);
		if (loc.y < min)
			loc.y = min;
		else if (loc.y > max)
			loc.y = max;

		if (loc.y != min || loc.y != max) {
			if (dir > 0)
				velocity += 0.1f;
			if (dir < 0)
				velocity -= 0.1f;
		}
		initTramp();
	}
}
