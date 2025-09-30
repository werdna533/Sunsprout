// Andrew Law
// January 19, 2024
/*
 * Sun class
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Sun implements MouseMotionListener {
	
    private int sunX; // X position of the sun
    private final int sunY = 50; // Y position of the sun
    private final int size = 100; 
    private Image sunImage;

    // Constructor
 	// Creates a Sun object and initializes its image and x position
 	// Parameters: none
 	// Does not return any type
    public Sun() {
    	
    	sunImage = new ImageIcon("Assets//Sun0.png").getImage();
    	
        sunX = 300;
    }
    
    public int getSunX() {
    	return sunX;
    }
    
    public int getSunY() {
    	return sunY;
    }

 // Helps to draw the Sun object without needing as many getters from the Driver
    // Parameters: Graphics
    // Return: None
    public void drawOnSurface(Graphics surface){
        surface.drawImage(sunImage,sunX-size/2, sunY, size, size, null);
    }

    // Match the x-coordinate of the Sun with the cursor's x-coordinate
    @Override
    public void mouseMoved(MouseEvent e) {
        sunX = e.getX();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    	 sunX = e.getX();
    }
}
