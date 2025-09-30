// Andrew Law
// January 19, 2024
/*
 * Plot class
 */

import javax.swing.*;
import java.awt.*;

public class Plot {
	
	private Image plotImage = new ImageIcon("Assets//plot.png").getImage();
	private int posX;
	private int posY;

	private Flower currentFlower;
   
	// Constructor
 	// Creates a Plot object and initializes its x and y coordinates
 	// Parameters: x coordinate, y coordinate
 	// Does not return any type	
	public Plot(int x, int y) {
		posX = x;
		posY = y;
	}
	
	public Image getPlotImage() {
		return plotImage;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public Flower getFlower() {
		return currentFlower;
	}
	
	// Determines whether the plot has a flower or not
    // Parameters: none
    // Returns: true if there is a flower, false if there isn't a flower
	public boolean isOccupied() {
		if (currentFlower == null) {
			return false;
		} else {
			return true;
		}
	}
	
	// Helps to draw the Plot object without needing as many getters from the Driver
    // Parameters: Graphics
    // Return: None
	public void drawOnSurface(Graphics surface, int numPlots, int plotSize, int screenHeight) {
		for (int i = 0; i < numPlots; i++) {
			surface.drawImage(plotImage, posX, posY, plotSize, plotSize, null);
		}
	}
	
	// Adds flower to current plot
    // Parameters: none
    // Returns void
	public void addFlower() {
		currentFlower = new Flower();
	}
	
	// Removes flower from current plot
    // Parameters: none
    // Returns void
	public void removeFlower() {
		currentFlower = null;
	}
	
}
