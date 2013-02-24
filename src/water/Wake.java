package water;

import processing.core.PApplet;
import toxi.geom.*;
import toxi.physics.*;

public class Wake {

	PApplet parent;
	VerletParticle[] selected;
	Vec3D[] attenuator;
	Vec3D loc, newLoc;
	float distance = 0;
	float closest = 0;
	float PULL, pullTH;
	int wakeLength;
	boolean locked = false;

	Wake(int wakeLength_, PApplet p) {
		parent = p;

		wakeLength = wakeLength_;
		selected = new VerletParticle[wakeLength];
		attenuator = new Vec3D[wakeLength];
		newLoc = new Vec3D(0, 0, 0);

		PULL = 100;
		pullTH = Global.REST_LENGTH*4;
	}

	void select(int[] selection) {
		for (int i = 0; i < wakeLength; i++) {
			selected[i] = (VerletParticle) Global.myMesh[selection[0]][selection[1]];

			// CHANGE DIRECTION HERE
			if (Global.goingLeft) {
				if (selection[1] < Global.COLS - 2)
					selection[1]++;
			} else {
				if (selection[1] > 1)
					selection[1]--;
			}
		}

	}

	void lock() {
		for (int i = 0; i < wakeLength; i++) {
			selected[i].lock();
		}
		locked = true;
	}

	void unlock() {
		for (int i = 0; i < selected.length; i++) {
			selected[i].unlock();
		}
		locked = false;
	}

	void attenuate() {
		// Adjust the extent of the pulling
		for (int i = 0; i < wakeLength; i++) {
			attenuator[i] = new Vec3D(loc.sub(selected[i]));
			attenuator[i].normalizeTo(PULL / (i + 1));
			newLoc = new Vec3D(selected[i].add(attenuator[i]));
			selected[i].set(newLoc);
		}
	}

	void set() {
		// Adjust the extent of the pulling
		for (int i = 0; i < wakeLength; i++) {
			attenuator[i] = new Vec3D(loc.sub(selected[i]));
			newLoc = new Vec3D(selected[i].add(attenuator[i]));
			selected[i].set(newLoc);
		}
	}

	/* Find the closest mesh node and lock the nodes near it */
	int[] findClosest() {
		// PApplet.println("Location of Swimmer: " + loc);
		int[] closestIndex = { 1, 1 };
		float prev_distance = Global.MESH_WIDTH * 1000;
		for (int y = 1; y < Global.ROWS - 2; y++) {
			for (int x = 1; x < Global.COLS - 2; x++) {
				VerletParticle p = (VerletParticle) Global.myMesh[y][x];
				distance = p.distanceTo(loc);
				if (distance < prev_distance) {
					//CHANGE DIRECTION HERE
					//if((Global.goingLeft && loc.x > p.x) || (!Global.goingLeft && loc.x < p.x))
					closestIndex[0] = y;
					closestIndex[1] = x;
					prev_distance = distance;
					closest = distance;
				}
			}
		}
		//PApplet.(closestIndex[0] + "\t" + closestIndex[1]);
		return closestIndex;
	}

}

class PluckWake extends Wake {
	boolean grab = false;
	boolean release = false;

	PluckWake(int wakeLength, PApplet parent) {
		super(wakeLength, parent);
		// PApplet.println("Selected Length: " + selected.length);
	}

	void run(Vec3D loc_) {
		loc = loc_;
		// PApplet.println("Where is mouse?" + loc.x);
		// PApplet.println("Locked? " + locked);

		if (grab) {
			// PApplet.println("Click!");
			select(findClosest());
			lock();
			grab = false;

		} else if (locked) {
			set();
			// PApplet.println("Plucked to: " + selected[0].x);
			if (Global.debug) {
				parent.stroke(120, 100, 100);
				parent.strokeWeight(50);
				parent.point(selected[0].x, selected[0].y, selected[0].z);
			}
		}
		if (locked && release) {
			unlock();
			release = false;
			PApplet.println("Mesh Point Released");
		}
	}

}

class StreamWake extends Wake {
	Vec3D anchor;
	VerletParticle firstNode, nextNode;
	int[] pullee, pulleeHead, firstHead;

	// How far as swimmer gone between mesh points?
	float howFar = 0;
	float howFarPct = 0;

	StreamWake(Vec3D loc_, int wakeLength, PApplet parent) {
		super(wakeLength, parent);
		loc = loc_;
		pullee = findClosest();
		pulleeHead = pullee;
		firstHead = pullee;
	}

	void run(Vec3D loc_, boolean followMouse) {
		loc = loc_;

		if (locked) {
			attenuate();
			if (letGo(followMouse)) {
				unlock();
			}
			if (Global.debug && followMouse) {
				// PApplet.println("Loc: " + loc);
				parent.stroke(240, 100, 100);
				parent.strokeWeight(20);
				parent.point(selected[0].x, selected[0].y, selected[0].z);
			}
		} else if (!locked) {
			moveOn();
			select(pullee);
			anchor();
			lock();
		}
		if (Global.debug)
			displayBuoys();
	}

	void anchor() {
		anchor = new Vec3D(selected[0].x, selected[0].y, selected[0].z);
	}

	Vec3D buoy() {

		// PApplet.println("PulleeHead: " + pulleeHead);

		// Find the node the swimmer is swimming towards
		if (Global.goingLeft)
			nextNode = Global.myMesh[pulleeHead[0]][pulleeHead[1] - 1];
		else
			nextNode = Global.myMesh[pulleeHead[0]][pulleeHead[1] + 1];
		
		// PApplet.println("Next Node: " + nextNode.x);
		howFarPct = howFar / pullTH;

		// Figure out where the swimmer is between 2 mesh points
		float swimmerY = PApplet.lerp(selected[0].y, nextNode.y, howFarPct);
		Vec3D swimmerWhere = new Vec3D(0, swimmerY, 0);

		return swimmerWhere;
	}

	void displayBuoys() {

		// Draw the initial closest point to the swimmer VerletParticle
		firstNode = Global.myMesh[firstHead[0]][firstHead[1]];
		parent.stroke(360, 100, 100);
		parent.strokeWeight(10);
		parent.point(firstNode.x, firstNode.y, firstNode.z);

		// Draw the node the swimmer is pulling on
		parent.stroke(120, 100, 100);
		parent.point(selected[0].x, selected[0].y, selected[0].z);

		// Draw the node the swimmer is swimming towards
		nextNode = Global.myMesh[pulleeHead[0]][pulleeHead[1] - 1];
		parent.stroke(240, 0, 100);
		parent.point(nextNode.x, nextNode.y, nextNode.z);
	}

	boolean letGo(boolean followMouse) {
		boolean release = false;

		// Unlock the nodes if gets far enough away
		howFar = anchor.distanceTo(loc);
		// PApplet.println("How Far? " + howFar);

		if (howFar > pullTH) {
			if (!followMouse) {

				// CHANGE DIRECTION HERE
				if ((Global.goingLeft && loc.x < anchor.x)
						|| (!Global.goingLeft && loc.x > anchor.x)) {
					//PApplet.println("Let Go!");
					release = true;
					anchor = null;
				}
			} else {
				PApplet.println("Let Go!");
				release = true;
				anchor = null;
			}
		}

		// PApplet.println("Release? " + release);
		return release;
	}

	void moveOn() {
		pullee = findClosest();
		select(pullee);
		pulleeHead = pullee;

	}
}
