// Andrew Law
// January 19, 2024
/*
 * Flower class
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Flower {
	
	private Image[] commonSprites = {	new ImageIcon("Assets//commonFlower0.png").getImage(), 
										new ImageIcon("Assets//commonFlower1.png").getImage(), 
										new ImageIcon("Assets//commonFlower2.png").getImage(),
										new ImageIcon("Assets//commonFlower3.png").getImage(),
										new ImageIcon("Assets//commonFlower4.png").getImage()};
	
	private Image[] rareSprites = {	new ImageIcon("Assets//rareFlower0.png").getImage(), 
									new ImageIcon("Assets//rareFlower1.png").getImage(), 
									new ImageIcon("Assets//rareFlower2.png").getImage(),
									new ImageIcon("Assets//rareFlower3.png").getImage(),
									new ImageIcon("Assets//rareFlower4.png").getImage()};
	
	private Image[] epicSprites = {	new ImageIcon("Assets//epicFlower0.png").getImage(), 
									new ImageIcon("Assets//epicFlower1.png").getImage(), 
									new ImageIcon("Assets//epicFlower2.png").getImage(),
									new ImageIcon("Assets//epicFlower3.png").getImage(),
									new ImageIcon("Assets//epicFlower4.png").getImage()};
	
	private final Map <String, Image[]> rarityMap = new HashMap<>() {{put("Common", commonSprites); put("Rare", rareSprites); put("Epic", epicSprites);}};
	
	private Image currentImg;
	private static int size;
	
	private int status = 0;
	
	private String rarity;
	private int score;
	
	private int growthPhase = 0;
	private final int maxGrowth = 5;
	
	private int countdown = 0;

    // Constructor
 	// Creates a Flower object and initializes its rarity, score, and image
 	// Parameters: none, the rarity is randomly generated
 	// Does not return any type
	public Flower() {
		
		double random = Math.random();
		if (random <= 0.5) {
			rarity = "Common";
			score = 100;
		} else if (random > 0.5 && random <= 0.85) {
			rarity = "Rare";
			score = 250;
		} else {
			rarity = "Epic";
			score = 500;
		}
		
		currentImg = rarityMap.get(rarity)[0];
	}
	
	public Image getImage() {
		return currentImg;
	}
	
	public static void setSize(int i) {
		Flower.size = i;
	}
	
	public static int getSize() {
		return size;
	}
	
	public int getStatus() {
		return status;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getRarity() {
		return rarity;
	}
	
	public int getCountdown() {
		return countdown/60;
	}
	
	// Helps to draw the Flower object without needing as many getters from the Driver
    // Parameters: Graphics
    // Return: None
	public void drawOnSurface(Graphics surface, int xValue, int yValue){
        surface.drawImage(currentImg, xValue, yValue, size, size, null);
    }
	
	// Grows the flower
    // Parameters: number of growths, depending on the size of the drop that hit the flower
    // Returns the current growthPhase (from 0 to 4) of the flower
	public int grow(int numGrowths) {
		// If the flower was on fire, extinguish the fire
		if (status == 1) {
			status = 0;
			return growthPhase;
		}
		// If the flower reaches max growth, return -1 to signal to Driver that the flower is ready to propagate
		if (growthPhase + numGrowths >= maxGrowth) {
			return -1;
		} else {
			growthPhase += numGrowths;
		currentImg = rarityMap.get(rarity)[growthPhase];
		}
		
		return growthPhase;
	}
	
	// Sets flower on fire
    // Parameters: none
    // Returns void
	public void setOnFire() {
		status = 1;
		countdown = 600;
	}
	
	// Updates countdown for the flower. If the countdown reaches 0, then the flower dies
    // Parameters: none
    // Returns the countdown number (from 10 to 1 seconds)
	public int updateCountdown() {
		if (Driver.isGamePaused() || Driver.isGameOver()) {
			return countdown/60;
		}
		
		if (countdown > 0 ) {
			countdown--;
			return countdown/60;
		} else {
			// Countdown reached 0, let Driver know that the flower died
			return countdown-1;
		}
	}
}