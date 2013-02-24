package water;

import processing.core.*;
import codeanticode.glgraphics.*;

public class Sky {
	PApplet parent;

	int NUM_LINES = 1000;
	int NUM_PTS_PER_LINE = 2;
	int NUM_POINTS = NUM_LINES * NUM_PTS_PER_LINE;

	GLModel lines;
	
	float miss;
	float thetasky;
	float j,jj;
	float polo;
	
	float patha;
	float max= 13;
	float maxspeed = 1;
	float min = 0.1f;
	float thetaspeed = 0.025f;

	Sky(PApplet p) {
		parent = p;
		lines = new GLModel(parent, NUM_POINTS, GLModel.LINES, GLModel.DYNAMIC);
		lines.initColors();
		 		 
	}

	void run() {

		
		PApplet.println("Max: " + max);

		
		 lines.beginUpdateColors();
		    for (int i = 0; i < NUM_POINTS; i++) {
		    	float blu=PApplet.map(PApplet.cos(thetasky),-1,1,min,max);
		    	lines.updateColor(i,0,0,100,10);
		    }
		  lines.endUpdateColors();
	
		
		  lines.beginUpdateVertices();
		  
		  int index = 0;
		  
		  
		  thetasky+= 0.01;            
		  //1/(PApplet.TWO_PI*30); 
		 // if(thetasky>=PApplet.TWO_PI*3){thetasky=PApplet.radians(0);}
          

	      
		  for (int i = 0 ; i < NUM_LINES ; i++) {    
		   
			miss=miss+(PApplet.TWO_PI/NUM_LINES);

			
		     float jj=PApplet.map(PApplet.cos((thetasky+miss)*2),-1,1,1, Global.mWidth*10);               //290);
		     float j=PApplet.map(PApplet.sin((thetasky+miss)*9),-1,1,1,Global.mHeight*10);              //40);
		     
		     
		      
		      float x=jj*PApplet.sin((thetasky/3))+3000;               //Global.mWidth*.5f;
		      float y=j*PApplet.sin((thetasky/3))+3000;                //Global.mHeight;
		      
		      float x2=(jj*PApplet.sin((thetasky)))*PApplet.sin(thetasky/3);
		      float y2=(j*PApplet.sin((thetasky)))*PApplet.cos(thetasky/3);
		      
		      float skyx=x*PApplet.cos(miss+thetasky*2)+Global.mWidth/2;          //patha;
		      float skyy=y*PApplet.sin(miss+thetasky*2)+Global.mHeight/2;            //pathb;
		      

		      
		      
		    lines.updateVertex(index,skyx,skyy,-Global.MESH_DEPTH*.8f);
		    
		    index++;

		    
		    float x3=x2*PApplet.sin((miss+thetasky)*2)+Global.mWidth/2;             //patha;
		    float y3=y2*PApplet.sin((miss+thetasky)*2+Global.mHeight/2);               //pathb;
		    
		    lines.updateVertex(index,x3,y3,-Global.MESH_DEPTH*.8f);
		    index++;
		  }
		  lines.endUpdateVertices();
  

		  GLGraphics renderer = (GLGraphics)parent.g;
		  renderer.beginGL();
		  renderer.model(lines);
		  renderer.endGL();	}
}

