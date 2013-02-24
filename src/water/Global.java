package water;

import toxi.physics.VerletPhysics;
import toxi.physics.*;

public class Global {
	public static boolean local = true;
			
	public static boolean debug = false;
	public static boolean auto = true;
	public static boolean reset = false;
	
	// These we'll use for master width and height instead of Processing's built-in variables
	public static int mWidth = 11520;
	public static int mHeight = 1080;

	public static VerletPhysics physics;

	public static int ROWS = 30;
	public static int COLS = 100;
	public static VerletParticle[][] myMesh = new VerletParticle[ROWS][COLS];	

	public static int REST_LENGTH=330;  //Rest length of springs between nodes
	public static int MESH_WIDTH = COLS*REST_LENGTH;
	public static int MESH_DEPTH = ROWS*REST_LENGTH;
	
	public static float STRENGTH=0.001f;  //Stiffness of springs

	public static int horizon = (int)(mHeight);      //Height of water

	public static float h = 0;
	public static float s = 10;
	public static float b = 0;
	public static float hsbSpeed = .015f;
	public static float bdelta = 1.5f;

	public static boolean die = false;
	public static boolean fadeIn = false;

	//Gravity effects
	public static float xgrav = 0.1f;
	public static float ygrav = 0.1f;
	public static float zgrav = 0.01f;

	//Noise index
	public static int t = 0;

	//Follow mouse?
	public static boolean mouseOn = false; 
	
	//Mouse Position
	public static int mouseLocX = 0;
	public static int mouseLocY = 0;
		
	//Direction of Swimming
	public static boolean goingLeft = true;
}
