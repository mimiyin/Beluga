package water;

import mpe.client.*;
import water.IDGetter;

import processing.core.*;

import codeanticode.glgraphics.*;

public class Water extends PApplet {
	// Set it to 1 for actual size, 0.5 for half size, etc.
	// This is useful for testing MPE locally and scaling it down to fit to your
	// screen
	public static float scale = .15f;

	// if this is true, it will use the MPE library, otherwise just run
	// stand-alone
	public static boolean MPE = false;
	public static boolean local = true;

	// Client ID
	// Should be adjusted only for "local" testing
	// --------------------------------------
	TCPClient client;
	int ID = 0;

	// Stage
	Stage stage;

	// --------------------------------------
	static public void main(String args[]) {
		// Windowed
		if (local) {
			PApplet.main(new String[] { "water.Water" });
			// FullScreen Exclusive Mode
		} else {
			PApplet.main(new String[] { "--present", "--exclusive",
					"water.Water" });
		}
	}

	// --------------------------------------

	public void setup() {

		// If we are using the library set everything up
		if (MPE) {
			// make a new Client using an INI file
			// sketchPath() is used so that the INI file is local to the sketch
			String path = "mpefiles/";
			if (local) {
				path += "local/mpe" + ID + ".ini";
			} else {
				ID = IDGetter.getID();
				path += "6screens/mpe" + ID + ".ini";
			}
			client = new TCPClient(path, this);
			if (local) {
				size((int) (client.getLWidth() * scale),
						(int) (client.getLHeight() * scale),
						GLConstants.GLGRAPHICS);
			} else {
				size(client.getLWidth(), client.getLHeight(),
						GLConstants.GLGRAPHICS);
			}
			// the size is determined by the client's local width and height
			Global.mWidth = client.getMWidth();
			Global.mHeight = client.getMHeight();
			Global.local = false;

		} else {
			// Otherwise with no library, force size
			size(parseInt(11520 * scale), parseInt(1080 * scale),
					GLConstants.GLGRAPHICS);
			Global.local = true;
			Global.mWidth = 11520;
			Global.mHeight = 1080;
		}

		if (MPE) {
			// IMPORTANT, YOU MUST START THE CLIENT!
			client.start();
			client.enable3D(true);
		}

		// /////////////////////////////////////////////////////////

		// mesh = new Mesh(this);
		stage = new Stage(this);

		// App Settings
		noiseSeed(1);
		randomSeed(1);
		colorMode(HSB, 360, 100, 100, 100);
		frameRate(30);

	}

	// --------------------------------------
	// Keep the motor running... draw() needs to be added in auto mode, even if
	// it is empty to keep things rolling.
	public void draw() {
		noCursor();
		// If we are on the 6 screens we want to preset the frame's location
		// if (MPE && local) {
		// frame.setLocation(ID * width, 0);
		// }

		// If we're not using the library frameEvent() will not be called
		// automatically
		if (!MPE) {
			frameEvent(null);
		}
	}

	String test = "WAITING";

	public void frameEvent(TCPClient c) {

		if (MPE && c.messageAvailable()) {

			String[] msg = c.getDataMessage();
			String[] mssgs = msg[0].split(",");
			int i = 0;
			Global.reset = Boolean.parseBoolean(mssgs[i]);
			i++;
			Global.debug = Boolean.parseBoolean(mssgs[i]);
			i++;
//			Global.auto = Boolean.parseBoolean(mssgs[i]);
//			i++;
//			Global.mouseOn = Boolean.parseBoolean(mssgs[i]);
//			i++;
//			Global.mouseLocX = Integer.parseInt(mssgs[i]);
//			i++;
//			Global.mouseLocY = Integer.parseInt(mssgs[i]);
//			i++;
//			stage.mouseClicked = Boolean.parseBoolean(mssgs[i]);
//			i++;
//			stage.mouseReleased = Boolean.parseBoolean(mssgs[i]);
//			i++;
			stage.key1Pressed = Boolean.parseBoolean(mssgs[i]);
			i++;
			stage.key2Pressed = Boolean.parseBoolean(mssgs[i]);
			i++;
			stage.key3Pressed = Boolean.parseBoolean(mssgs[i]);
			i++;
//			Global.bdelta = Float.parseFloat(mssgs[i]);
//			i++;
//			stage.whalesOn = Boolean.parseBoolean(mssgs[i]);
//			i++;
//			stage.swimWhales = Boolean.parseBoolean(mssgs[i]);
//			i++;
//			stage.whalesMax = Integer.parseInt(mssgs[i]);
//			i++;
			stage.spaceship.swimmerDir = Float.parseFloat(mssgs[i]);
			i++;
//			stage.cueMusic = Boolean.parseBoolean(mssgs[i]);
//			i++;
//			stage.skyOn = Boolean.parseBoolean(mssgs[i]);
//			i++;
			Global.die = Boolean.parseBoolean(mssgs[i]);

		}

		// clear the screen
		if (!MPE || local) {
			scale(scale);
		}

		stage.run();

		if (Global.debug) {
			if (MPE) {
				fill(0, 0, 100);
				textSize(32);
				text(test, 20 + ID * 3840, 200);
				text("" + client.getFPS(), 20 + ID * 3840, 250);
				text("" + stage.frameCount, 20 + ID * 3840, 300);
			} else {
				fill(0, 0, 100);
				textSize(128);
				text("Framerate: " + frameRate, 50, 200);
			}

		}
	}

	public void mousePressed() {
		stage.mouseClicked = true;
		stage.mouseReleased = false;
	}

	public void mouseReleased() {
		stage.mouseReleased = true;
		stage.mouseClicked = false;
	}

	public void keyReleased() {
		if (key == '1') {
			stage.key1Pressed = false;
		}
		if (key == '2') {
			stage.key2Pressed = false;
		}
		if (key == '3') {
			stage.key3Pressed = false;
		}
	}

	public void keyPressed() {
		if (key == '1')
			stage.key1Pressed = true;
		if (key == '2')
			stage.key2Pressed = true;
		if (key == '3')
			stage.key3Pressed = true;
		if (key == 'p') {
			//stage.outputTramps.flush(); // Write the remaining data
			//stage.outputTramps.close(); // Finish the file
			//stage.outputWhales.flush(); // Write the remaining data
			//stage.outputWhales.close(); // Finish the file
			exit(); // Stop the program
		} else if (key == 'r')
			Global.reset = true;
		else if (key == 'a')
			Global.auto = !Global.auto;
		else if (key == 'd')
			Global.debug = !Global.debug;
		else if (key == 32)
			stage.cueMusic = !stage.cueMusic;
		else if (key == ENTER)
			Global.die = !Global.die;
		else if (key == 'm') {
			Global.mouseOn = !Global.mouseOn;
			if (!Global.mouseOn)
				stage.mouse.pluckMouse.release = true;
		} else if (key == 's')
			stage.skyOn = !stage.skyOn;
		else if (key == 'w')
			stage.whalesOn = !stage.whalesOn;
		else if (key == CODED && keyCode == UP) {
			stage.whalesMax++;
			println("whalesMax: " + stage.whalesMax);
		} else if (key == CODED && keyCode == DOWN) {
			stage.whalesMax--;
			println("whalesMax: " + stage.whalesMax);
		} else if (key == CODED && keyCode == RIGHT) {
			stage.spaceship.swimmerDir += 1;
			println("swimmerDir: " + stage.spaceship.swimmerDir);
		} else if (key == CODED && keyCode == LEFT) {
			stage.spaceship.swimmerDir -= 1;
			println("swimmerDir: " + stage.spaceship.swimmerDir);
		} else if (key == '=') {
			 Global.bdelta += 0.01f;
			 println("bdelta: " + Global.bdelta);
			 stage.moon.blu += 0.1f;
			 println("blu: " + stage.moon.blu);
			 for (int i = 0; i < stage.starfishMax; i++) {
				Starfish thisStarfish = (Starfish) stage.starfish.get(i);
				thisStarfish.max -= 0.1f;
				if (i == stage.starfishMax - 1)
					println("max: " + thisStarfish.max);
			}
		} else if (key == '-') {
			 Global.bdelta -= 0.01f;
			 println("bdelta: " + Global.bdelta);
			stage.moon.blu -= 0.1f;
			println("blu: " + stage.moon.blu);
			for (int i = 0; i < stage.starfishMax; i++) {
				Starfish thisStarfish = (Starfish) stage.starfish.get(i);
				thisStarfish.max -= 0.1f;
				if (i == stage.starfishMax - 1)
					println("max: " + thisStarfish.max);
			}
		}
	}
}
