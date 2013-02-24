package water;

import java.util.ArrayList;

import processing.core.*;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;
import toxi.physics.behaviors.GravityBehavior;
import toxi.physics.constraints.SphereConstraint;
import toxi.processing.ToxiclibsSupport;

public class Mesh {

	PApplet parent;
	// Toxi physics stuff
	ToxiclibsSupport gfx;
	TriangleMesh mesh;
	BoxConstraint ground;
	ArrayList<SphereConstraint> spheres = new ArrayList<SphereConstraint>();

	Mesh(PApplet p) {
		parent = p;
		gfx = new ToxiclibsSupport(parent);
		initPhysics();
	}

	// create the physical world by constructing all
	// obstacles/constraints, particles and connecting them
	// in the correct order using springs
	void initPhysics() {
		Global.physics = new VerletPhysics();
		Global.physics.addBehavior(new GravityBehavior(new Vec3D(Global.xgrav,
				Global.ygrav, Global.zgrav)));

		for (int y = 0, idx = 0; y < Global.ROWS; y++) {
			for (int x = 0; x < Global.COLS; x++) {
				VerletParticle p = new VerletParticle(x * Global.REST_LENGTH
						- ((Global.MESH_WIDTH) * .5f), Global.horizon / 2, y
						* Global.REST_LENGTH - Global.MESH_DEPTH * .67f);
				Global.physics.addParticle(p);
				Global.myMesh[y][x] = p;
				if (x > 0) {
					VerletParticle q = (VerletParticle) Global.physics.particles
							.get(idx - 1);
					VerletSpring s = new VerletSpring(p, q, Global.REST_LENGTH,
							Global.STRENGTH);
					Global.physics.addSpring(s);
				}
				if (y > 0) {
					VerletParticle r = (VerletParticle) Global.physics.particles
							.get(idx - Global.COLS);
					VerletSpring s = new VerletSpring(p, r, Global.REST_LENGTH,
							Global.STRENGTH);
					Global.physics.addSpring(s);
				}

				idx++;
			}
		}
		// Pin top edge
		float theta = PApplet.PI/Global.COLS;
		for (int x = 0; x < Global.COLS; x++) {
			VerletParticle t = (VerletParticle) Global.myMesh[0][x];
			t.addSelf(0, 0, -(5 * Global.REST_LENGTH * PApplet.sin(x * theta) + Global.REST_LENGTH));
			t.lock();
		}

		// Pin bottom edge
		for (int x = 0; x < Global.COLS; x++) {
			VerletParticle t = (VerletParticle) Global.myMesh[Global.ROWS - 1][x];
			t.addSelf(0, 100, 0);
			t.lock();
		}

		// Pin left edge
		for (int y = 0; y < Global.ROWS; y += 2) {
			VerletParticle t = (VerletParticle) Global.myMesh[y][0];
			t.lock();
		}
		// Pin right edge
		for (int y = 0; y < Global.ROWS; y += 2) {
			VerletParticle t = (VerletParticle) Global.myMesh[y][Global.COLS - 1];
			t.lock();
		}
	}

	// iterates over all particles in the grid order
	// they were created and constructs triangles
	void updateMesh() {
		Global.physics.update();

		mesh = new TriangleMesh();
		for (int y = 0; y < Global.ROWS - 1; y++) {
			for (int x = 0; x < Global.COLS - 1; x++) {
				VerletParticle a = (VerletParticle) Global.myMesh[y][x];
				VerletParticle b = (VerletParticle) Global.myMesh[y][x + 1];
				VerletParticle c = (VerletParticle) Global.myMesh[y + 1][x + 1];
				VerletParticle d = (VerletParticle) Global.myMesh[y + 1][x];
				mesh.addFace(a, d, c);
				mesh.addFace(a, c, b);
			}
		}

	}

	void renderMesh() {
		parent.fill(210, Global.s, Global.b * Global.bdelta, 33);
		parent.noStroke();
		gfx.mesh(mesh, true);
	}
}
