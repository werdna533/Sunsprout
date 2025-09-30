// Andrew Law
// January 19, 2024
/*
 * Projectile class
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Projectile {

	private int posX; 
    private int posY = 50;
    private double yVel = 5;
    private int size = 50; 
    private Image projectileImage;
    private String identity;
  
    private ArrayList<Cloud> hitClouds = new ArrayList<>();
    
    private final Image sunray = new ImageIcon("Assets//Projectile0.png").getImage();
    private final Image smallDrop = new ImageIcon("Assets//Projectile1.png").getImage();
    private final Image lightning = new ImageIcon("Assets//Projectile2.png").getImage();
    
    // Constructor
 	// Creates a Projectile object and initializes its coordinates, image, and identity
 	// Parameters: x coordinate, y coordinate
 	// Does not return any type
    public Projectile(int posX, int posY) {
    	
    	identity = "sunray";
    	projectileImage = sunray;
    	this.posX = posX;
    	this.posY = posY+25;
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
    
    // Helps to draw the Projectile object without needing as many getters from the Driver
    // Parameters: Graphics
    // Return: None
    public void drawOnSurface(Graphics surface){
        surface.drawImage(projectileImage,posX-size/2, posY, size, size, null);
    }
    
    // Moves projectile downward at a constant speed
    public void fall() {
    	posY += yVel;
    }
    
    // Changes the projectile when it hits a cloud, either a rain cloud or a thundercloud
    // Parameters: the cloud that it hit
    // Returns String indicating the general identity of the projectile
    public String hitCloud(Cloud c) {
    	
    	// Lighning cannot be changed by any cloud
    	if (identity.equals("lightning")) {
    		return "lightning";
    	}
    	
    	// If the projectile hit a thundercloud, then turn the projectile into a lightning bolt
    	if (c.getIdentity().equals("thundercloud")) {
    		identity = "lightning";
    		projectileImage = lightning;
    		return "lightning";
    	}
    	
    	// Keep track of the previous rainclouds it hit, up to 3 rain clouds
    	if (hitClouds.contains(c) == false && hitClouds.size() < 3) {
    		hitClouds.add(c);
    		
    		// Change sunray to raindrop, and grow raindrops, if projectile hit a rain cloud
    		if (identity.equals("sunray")) {
        		identity = "raindrop";
        		projectileImage = smallDrop;
        	} else if (identity.equals("raindrop")) {
        		identity = "mediumDrop";
        		size = 75;
        	} else if (identity.equals("mediumDrop")) {
        		identity = "largeDrop";
        		size = 100;
        	}
    		
    		return "rainDrop";
    	}
    	 return "";
    }
}