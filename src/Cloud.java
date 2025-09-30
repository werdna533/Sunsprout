// Andrew Law
// January 19, 2024
/*
 * Cloud class
 */

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

import java.util.*;

public class Cloud {

	private final int size = 100;
	private int posX; 
    private int posY;
    private double xVel = 0; 
    private String direction;
    
    private static double riskOfThunder;
    
    private Image cloudImage;
    private final Image raincloud = new ImageIcon("Assets//Cloud0.png").getImage();
    private final Image thundercloud = new ImageIcon("Assets//Cloud1.png").getImage(); 
    private String identity;
    
    private Map <String, Integer> rarityMap = new HashMap<>();
    
    // Constructor
 	// Creates a Cloud object and initializes its image, identity, y-position, speed, and whether it comes in from left or right
 	// Parameters: screen height, screen width, minimum speed, maximum speed, probability of thunder
 	// Does not return any type
    public Cloud (int screenHeight, int screenWidth, double minSpeed, double maxSpeed, double thunderProbability) {
    	riskOfThunder = thunderProbability;
    	if (Math.random() >= riskOfThunder) {
    		cloudImage = raincloud;
    		identity = "raincloud";
    	} else {
    		cloudImage = thundercloud;
    		identity = "thundercloud";
    	}
    	
    	posY = (int)(Math.random()*(screenHeight-350)+150);
    	xVel = (double)(Math.random()*(maxSpeed-minSpeed+1) + minSpeed);
    	
    	if (Math.random() <= 0.5) {
    		direction = "right";
    		posX = -50;
    	} else {
    		direction = "left";
    		posX = screenWidth + 50;
    	}
    }
    
    public int getPosX() {
    	return posX;
    }
    
    public int getPosY() {
    	return posY;
    }
    
    public int getSize() {
    	return size;
    }
    
    public String getIdentity() {
    	return identity;
    }
    
    // Helps to draw the Cloud object without needing as many getters from the Driver
    // Parameters: Graphics
    // Returns void
    public void drawOnSurface(Graphics surface){
        surface.drawImage(cloudImage,posX, posY, size, size, null);
    }
    
    // Moves the cloud either forwards or backwards across the screen
    // Parameters: none
    // Returns void
    public void move() {
    	if (direction.equals("right")) {
    		posX += xVel;
    	} else if (direction.equals("left")){
    		posX -= xVel;
    	}
    	
    }
}
