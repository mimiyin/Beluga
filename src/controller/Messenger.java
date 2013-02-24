/**
 * Simple Template for the Big Screens Class, Fall 2011
 * <https://github.com/shiffman/Most-Pixels-Ever>
 * 
 * This shows how to use the ASyncClient to broadcast messages into the MPE Server
 */

package controller;

import mpe.client.*;
import processing.core.*;
import water.Global;

public class Messenger extends PApplet {

	// --------------------------------------
	AsyncClient client;
	PFont font;

	boolean message = false;
	boolean reset = false;
	boolean debug = false;
	boolean auto = true;
	boolean mouseOn = false;

	boolean mouseClicked = false;
	boolean mouseReleased = false;

	boolean key1Pressed = false;
	boolean key2Pressed = false;
	boolean key3Pressed = false;

	boolean cueMusic = false;
	boolean whalesOn = false;
	boolean swimWhales = false;
	boolean skyOn = true;
	boolean die = false;

	int birthWhalesTH = 60;
	int swimWhalesTH = 150;

	int s; // Take a snapshot of what time it is.

	int whalesMax = 0;
	float bdelta = 1.5f;

	float swimmerDir = -10;

	// --------------------------------------
	public void setup() {
		// set up the client
		// For testing locally
		// client = new AsyncClient("localhost",9003);

		// At NYU
		//client = new AsyncClient("128.122.151.64", 9003);

		// At IAC
		client = new AsyncClient("192.168.130.241", 9003);

		size(1280, 128);

		smooth();
		frameRate(30);
		font = createFont("Arial", 12);
	}

	// --------------------------------------
	public void draw() {
		s = (int) (millis() / 1000);

		if (s > swimWhalesTH)
			swimWhales = true;
//		else if (s > birthWhalesTH)
//			whalesOn = true;

		background(255);

		// int x = (int) map(mouseX, 0, width, -11520, 11520);
		// int y = (int) map(mouseY, 0, height, -1080, 1080);
//		String msg = reset + "," + debug + "," + auto + "," + mouseOn + ","
//				+ mouseX + "," + mouseY + "," + mouseClicked + ","
//				+ mouseReleased + "," + key1Pressed  + ","
//				+ key2Pressed + "," + key3Pressed + "," + bdelta + "," + whalesOn + ","
//				+ swimWhales + "," + whalesMax + "," + swimmerDir + ","
//				+ cueMusic + "," + skyOn + "," + die;
		String msg = reset + "," + debug + "," + key1Pressed  + ","
				+ key2Pressed + "," + key3Pressed + "," + swimmerDir + "," + die;
		client.broadcast(msg);

		noStroke();
		fill(255, 0, 0);
		ellipseMode(CENTER);
		ellipse(width / 5, height / 2, 50, 50);
		ellipse(width / 2, height / 2, 50, 50);
		ellipse(4 * (width / 5), height / 2, 50, 50);

		fill(255, 155);
		rectMode(CORNER);
		rect(0, 0, width, height);

		textFont(font);
		fill(0);
		// text("Broadcasting: " + msg,25,height/2);
		text("r reset: " + auto, 20, 20);
		text("d debug: " + debug, 150, 20);
		text("a auto: " + auto, 300, 20);
		text("m mouseOn? " + mouseOn, 20, 40);
		text("mouseX: " + mouseX, 150, 40);
		text("mouseY: " + mouseY, 300, 40);
		text("mouseClicked: " + mouseClicked, 20, 60);
		text("mouseReleased: " + mouseReleased, 150, 60);
		text("bdelta: " + bdelta, 300, 60);
		text("w whalesOn? " + whalesOn, 20, 80);
		text("swimWhales: " + swimWhales, 150, 80);
		text("UP whalesMax: " + whalesMax, 300, 80);
		text("swimmerDir: " + swimmerDir, 450, 80);
		text("cue music? " + cueMusic, 20, 100);
		text("s skyOn?" + skyOn, 150, 100);
		text("die? " + die, 300, 100);
		reset = false;
	}

	public void mousePressed() {
		mousePressed = true;
		mouseReleased = false;
	}

	public void mouseReleased() {
		mouseReleased = true;
		mousePressed = false;
	}

	public void keyPressed() {
		if (key == '1')
			key1Pressed = true;
		if (key == '2')
			key2Pressed = true;
		if (key == '3')
			key3Pressed = true;

		if (key == 'r')
			reset = true;
		else if (key == 'd')
			debug = false;
		else if (key == 'a')
			auto = !auto;
		else if (key == 32) {
			//swimWhales = !swimWhales;
			cueMusic = !cueMusic;
		} else if (key == 'm')
			mouseOn = !mouseOn;
		else if (key == ENTER)
			die = !die;
		else if (key == 's')
			skyOn = !skyOn;
		else if (key == 'w')
			whalesOn = !whalesOn;
		else if (key == CODED && keyCode == UP) {
			whalesMax++;
			println("whalesMax: " + whalesMax);
		} else if (key == CODED && keyCode == DOWN) {
			whalesMax--;
			println("whalesMax: " + whalesMax);
		} else if (key == CODED && keyCode == RIGHT) {
			swimmerDir += 1;
			println("swimmerDir: " + swimmerDir);
		} else if (key == CODED && keyCode == LEFT) {
			swimmerDir -= 1;
			println("swimmerDir: " + swimmerDir);
		} else if (key == '=') {
			bdelta += 0.01f;
			println("bdelta: " + bdelta);
		} else if (key == '-') {
			bdelta -= 0.01f;
			println("bdelta: " + bdelta);
		}
	}

	// --------------------------------------
	static public void main(String args[]) {
		PApplet.main(new String[] { "controller.Messenger" });
	}
}
