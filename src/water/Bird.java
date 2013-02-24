package water;

import processing.core.*;

import java.util.ArrayList;

public class Bird {

	PApplet parent;
	boolean flying;

	PVector loc;
	PVector vel;
	PVector acc;
	float r;
	float maxforce;
	float maxspeed;
	float scale;

	Bird(PVector l, float ms, float mf, boolean flying_, PApplet p) {
		parent = p;
		flying = flying_;

		acc = new PVector(0, 0);
		vel = new PVector(parent.random(-.9f, 1), parent.random(-0.0001f, .00009f));
		loc = l.get();
		r = 20.0f;
		maxspeed = ms;
		maxforce = mf;
		scale = 10;
	}

	void run(ArrayList<Bird> birds) {
		if (flying) {
			flock(birds);
			update();
			borders();
		}
	}

	void applyForce(PVector force) {
		// We could add mass here if we want A = F / M
		acc.add(force);
	}

	// We accumulate a new acceleration each time based on three rules
	void flock(ArrayList<Bird> birds) {
		PVector sep = separate(birds); // Separation
		PVector ali = align(birds); // Alignment
		PVector coh = cohesion(birds); // Cohesion
		// Arbitrarily weight these forces
		sep.mult(1f);
		ali.mult(.7f);
		coh.mult(1f);
		// Add the force vectors to acceleration
		applyForce(sep);
		applyForce(ali);
		applyForce(coh);
	}

	// Method to update location
	void update() {
		// Update velocity
		vel.add(acc);
		// Limit speed
		vel.limit(maxspeed);
		loc.add(vel);
		// Reset accelertion to 0 each cycle
		acc.mult(0);
	}

	// A method that calculates and applies a steering force towards a target
	// STEER = DESIRED MINUS VELOCITY
	PVector seek(PVector target) {
		PVector desired = PVector.sub(target, loc); // A vector pointing from
													// the location to the
													// target

		// If the magnitude of desired equals 0, skip out of here
		// (We could optimize this to check if x and y are 0 to avoid mag()
		// square root
		if (desired.mag() == 0)
			return new PVector(0, 0);

		// Normalize desired and scale to maximum speed
		desired.normalize();
		desired.mult(maxspeed);
		// Steering = Desired minus Velocity
		PVector steer = PVector.sub(desired, vel);
		steer.limit(maxforce); // Limit to maximum steering force

		return steer;
	}

	// Wraparound
	void borders() {
		if (loc.x < -r - Global.mWidth)
			loc.x = Global.mWidth + r;
		if (loc.y < -r - Global.mHeight)
			loc.y = Global.mHeight + r;
		if (loc.x > Global.mWidth + r)
			loc.x = -r - Global.mWidth;
		if (loc.y > Global.mHeight + r)
			loc.y = -r - Global.mHeight;
	}

	// Separation
	// Method checks for nearby birds and steers away
	PVector separate(ArrayList<Bird> birds) {
		float desiredseparation = 100.0f;
		PVector steer = new PVector(0, 0);
		int count = 0;
		// For every Bird in the system, check if it's too close
		for (Bird other : birds) {
			// float d = PVector.dist(loc,other.loc);
			float d = PApplet.abs(loc.y - other.loc.y);
			// If the distance is greater than 0 and less than an arbitrary
			// amount (0 when you are yourself)
			if ((d > 0) && (d < desiredseparation)) {
				// Calculate vector pointing away from neighbor
				PVector diff = PVector.sub(loc, other.loc);
				diff.normalize();
				diff.div(d); // Weight by distance
				steer.add(diff);
				count++; // Keep track of how many
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			steer.div((float) count);
		}

		// As long as the vector is greater than 0
		if (steer.mag() > 0) {
			// Implement Reynolds: Steering = Desired - Velocity
			steer.normalize();
			steer.mult(maxspeed);
			steer.sub(vel);
			steer.limit(maxforce);
		}
		return steer;
	}

	// Alignment
	// For every nearby Bird in the system, calculate the average velocity
	PVector align(ArrayList<Bird> birds) {
		float neighbordist = 50.0f * scale;
		PVector steer = new PVector(0, 0);
		int count = 0;
		for (Bird other : birds) {
			float d = PVector.dist(loc, other.loc);
			if ((d > 0) && (d < neighbordist)) {
				steer.add(other.vel);
				count++;
			}
		}
		if (count > 0) {
			steer.div((float) count);
		}

		// As long as the vector is greater than 0
		if (steer.mag() > 0) {
			// Implement Reynolds: Steering = Desired - Velocity
			steer.normalize();
			steer.mult(maxspeed);
			steer.sub(vel);
			steer.limit(maxforce);
		}
		return steer;
	}

	// Cohesion
	// For the average location (i.e. center) of all nearby birds, calculate
	// steering vector towards that location
	PVector cohesion(ArrayList<Bird> birds) {
		float neighbordist = 50.0f * scale;
		PVector sum = new PVector(0, 0); // Start with empty vector to
											// accumulate all locations
		int count = 0;
		for (Bird other : birds) {
			float d = PVector.dist(loc, other.loc);
			if ((d > 0) && (d < neighbordist)) {
				sum.add(other.loc); // Add location
				count++;
			}
		}
		if (count > 0) {
			sum.div((float) count);
			return seek(sum); // Steer towards the location
		}
		return sum;
	}
}
