package water;

import java.io.PrintWriter;
import java.util.ArrayList;

import com.sun.tools.internal.ws.wsdl.framework.GloballyKnown;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;

import processing.core.*;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.behaviors.GravityBehavior;

public class Stage {

	PApplet parent;

	// Writing and Reading data
	//PrintWriter Tramps, outputWhales;
	String[] trampLines, whaleLines;
	int trampsStart = 0;
	int whalesStart = 0;
	int frameCount = 0;

	// Audio
	Minim minim;
	AudioPlayer nuit;
	boolean cueMusic = false;

	// Mesh
	Mesh mesh;

	// Swimming objects
	//Swimmer swimmer;
	SpaceshipSwimmer spaceship;
	Mouser mouse;
	VerletParticle tramp1, tramp2, tramp3;

	// Whales
	ArrayList<BabySwimmer> whales = new ArrayList<BabySwimmer>();

	// New Babies
	ArrayList<Integer> births = new ArrayList<Integer>();

	// Starfish
	ArrayList<Starfish> starfish = new ArrayList<Starfish>();

	// Sky
	Moon moon;

	boolean skyOn = true;

	boolean whalesOn = true;
	boolean swimWhales = false;
	int whalesMax = 0;
	int starfishMax = 44;

	int s = 0;
	int birthWhalesTH = 60;
	int swimWhalesTH = 3300;

	// Interactions
	boolean key1Pressed = false;
	boolean key2Pressed = false;
	boolean key3Pressed = false;

	boolean mouseClicked = false;
	boolean mouseReleased = false;

	// Translate
	int centerX = Global.mWidth / 2;
	int centerY = Global.mHeight / 2;
	
	Stage(PApplet p) {
		parent = p;
		parent.noCursor();

		// Writing and Reading trampolines and whales
		//outputTramps = parent.createWriter("trampolines.txt");
		//outputWhales = parent.createWriter("whales.txt");
		trampLines = parent.loadStrings("trampolines.txt");
		whaleLines = parent.loadStrings("whales.txt");

		// Audio stuff
		minim = new Minim(parent);
		nuit = minim.loadFile("nuit.mp3", 2048);

		// Mesh
		mesh = new Mesh(parent);

		// Sky
		moon = new Moon(parent);

		// Swimmer
		//swimmer = new Swimmer(parent);
		spaceship = new SpaceshipSwimmer(parent);
		mouse = new Mouser(parent);

		// Tramps
		initTramps();
		initBirths();

	}

	void run() {

		frameCount++;

		if (Global.auto) {
			// parseTramps();
			parseWhales();
		}

		// Lights
		fade();

		// Sound
		cueMusic();

		// Interaction
//		if (Global.mouseOn)
//			trackMouse();
		cueTramps();

		// Sky
		if (skyOn) {
			moon.run();
			cueStarfish();
		}

		// Water
		// Update simulation
		if (Global.reset) {
			mesh.initPhysics();
			Global.reset = false;
		}

		mesh.updateMesh();

		parent.pushMatrix();
		parent.translate(centerX, centerY, 0);
		parent.scale((float) (0.5));
		mesh.renderMesh();

		// Animals!!!
		spaceship.run();

		if (whalesOn)
			birthWhales();
		
		parent.popMatrix();
	}

	void parseTramps() {
		if (trampsStart < trampLines.length - 1) {
			for (int i = trampsStart; i < trampLines.length; i++) {
				String[] pieces = PApplet.split(trampLines[i], PApplet.TAB);

				if (Integer.parseInt(pieces[0]) == frameCount) {
					int key = Integer.parseInt(pieces[1]);
					// PApplet.println("Frame: " + pieces[0] + "\t" + key);
					if (key == 1)
						key1Pressed = true;
					if (key == 2)
						key2Pressed = true;
					if (key == 3)
						key3Pressed = true;
					if (i < trampLines.length - 1)
						trampsStart++;
				}
			}
		}
	}

	void initBirths() {
		for (int i = 0; i < whaleLines.length; i++) {
			String[] pieces = PApplet.split(whaleLines[i], PApplet.TAB);
			births.add(Integer.parseInt(pieces[0]));
		}
	}

	void parseWhales() {
		if (frameCount > swimWhalesTH)
			swimWhales = true;

		if (whalesStart < births.size()) {
			for (int i = whalesStart; i < births.size(); i++) {
				int thisBirth = births.get(i);
				if (frameCount == thisBirth) {
					whalesMax++;
					whalesStart++;
					break;
				}
			}
		}
	}
	
	void birthWhales() {
		if (whales.size() < whalesMax) {
			whales.add(new BabySwimmer(parent));
			//if (!Global.auto)
				//outputWhales.println(frameCount + "\tNew Whale!");
		}
		for (int i = 0; i < whales.size(); i++) {
			BabySwimmer thisWhale = (BabySwimmer) whales.get(i);
			thisWhale.run(swimWhales);
		}
	}

	void cueStarfish() {
		if (starfish.size() < starfishMax) {
			starfish.add(new Starfish(parent));
		}
		for (int i = 0; i < starfish.size(); i++) {
			Starfish thisStarfish = (Starfish) starfish.get(i);
			thisStarfish.run();
		}
	}

	void fade() {
		// PApplet.println(Global.s);
		if (Global.s > 100)
			Global.fadeIn = true;

		if (Global.die) {
			Global.b -= 0.1f;
			Global.s -= 0.05f;
		} else if (!Global.fadeIn) {
			Global.s += .15f;
			Global.b += .75f;
			Global.b = PApplet.constrain(Global.b, 0, 100);
		} else if (Global.fadeIn) {
			Global.s -= .0075f;
			Global.b -= .015f;
		}
		parent.background(220, Global.s, Global.b);
	}

	void trackMouse() {
		// if (!MPE && local) {
		Global.mouseLocX = (int) PApplet.map(parent.mouseX, 0, parent.width,
				-Global.mWidth, Global.mWidth);
		Global.mouseLocY = (int) PApplet.map(parent.mouseY, 0, parent.height,
				-Global.mHeight, Global.mHeight);
		// }
		Vec2D mouseXY = new Vec2D(Global.mouseLocX, Global.mouseLocY);
		mouse.run(mouseXY);
	}

	void initTramps() {

		tramp1 = Global.myMesh[17][15];
		tramp2 = Global.myMesh[20][45];
		tramp3 = Global.myMesh[17][70];

	}

	void cueTramps() {

		if (key1Pressed) {
			tramp1.addForce(new Vec3D(-Global.REST_LENGTH * 10, 0, 0));
			//if (!Global.auto)
				//outputTramps.println(frameCount + "\t1");
		}
		if (key2Pressed) {
			tramp2.addForce(new Vec3D(Global.REST_LENGTH * 10, 0, 0));
			//if (!Global.auto)
				//outputTramps.println(frameCount + "\t2");
		}
		if (key3Pressed) {
			tramp3.addForce(new Vec3D(Global.REST_LENGTH * 10, 0, 0));
			//if (!Global.auto)
				//outputTramps.println(frameCount + "\t3");
		}

		if (mouseClicked)
			mouse.pluckMouse.grab = true;
		else if (mouseReleased)
			mouse.pluckMouse.release = true;
	}

	void cueMusic() {
		if (frameCount > 67)
			cueMusic = true;

		if (cueMusic) {
			nuit.unmute();
			nuit.play();
		} else {
			nuit.pause();
			nuit.mute();
		}
	}

}
