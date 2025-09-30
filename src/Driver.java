// Andrew Law
// January 19, 2024
/*
 * Sunsprout - In the game, you shoot sun rays at clouds to turn them into rain drops. Water flowers to help them grow!
 *
 */

import java.io.*;
import java.util.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


@SuppressWarnings("serial")

public class Driver extends JPanel implements Runnable, MouseListener, MouseMotionListener, KeyListener {

	// Non-game variables
	private final int FPS = 60;
	private Font gameFont;
	private Font highscoresFont;
	private final int screenWidth = 540;
	private final int screenHeight = 800;
	
	Thread thread;
	
	// Audio
	private Clip burn, extinguish, gameBackground, gameOverSound, grow, hoverSound, lightning, menuBackground, propagate, selectSound, sunRay, waterDrop;
	
	// Screens and buttons
	private final Image mainMenuImage = new ImageIcon("Screens//mainMenu.png").getImage();
	private final Rectangle nullArea = new Rectangle();
	private final Rectangle playButtonArea = new Rectangle(158, 109, 225, 37);
	private final Rectangle highscoresButtonArea = new Rectangle(158, 163, 225, 37);
	private final Rectangle instructionsButtonArea = new Rectangle(158, 218, 225, 37);
	private final Rectangle aboutButtonArea = new Rectangle(158, 272, 225, 37);
	private final Rectangle exitButtonArea = new Rectangle(158, 326, 225, 37);
	private Rectangle hoverButton = nullArea;
	private Rectangle lastHoverButton = nullArea;
	
	private final Image difficultySelectImage = new ImageIcon("Screens//difficultySelect.png").getImage();
	private final Rectangle easyButtonArea = new Rectangle(150, 146, 241, 100);
	private final Rectangle mediumButtonArea = new Rectangle(150, 267, 241, 100);
	private final Rectangle hardButtonArea = new Rectangle(150, 388, 241, 100);
	private final Rectangle backButtonArea = new Rectangle(202, 743, 137, 37);
	
	private final Image pauseButton = new ImageIcon("Assets//pause0.png").getImage();
	private final Image pauseButtonHover = new ImageIcon("Assets//pause1.png").getImage();
	private final Rectangle pauseButtonArea = new Rectangle(screenWidth-50, 10, 40, 40);
	
	private final Image pauseImage = new ImageIcon("Screens//pause.png").getImage();
	private final Rectangle endGameButtonArea = new Rectangle(202, 377, 137, 37);
	private final Rectangle resumeButtonArea = new Rectangle(202, 442, 137, 37);
	
	private final Image gameOverImage = new ImageIcon("Screens//gameOver.png").getImage();
	private final Rectangle backToMenuButtonArea = new Rectangle(158, 530, 225, 37);
	private final Image commonFlowerImage = new ImageIcon("Assets//commonFlower4.png").getImage();
	private final Image rareFlowerImage = new ImageIcon("Assets//rareFlower4.png").getImage();
	private final Image epicFlowerImage = new ImageIcon("Assets//epicFlower4.png").getImage();
	
	private final Image instructionsImage = new ImageIcon("Screens//instructions.png").getImage();
	
	private final Image aboutImage = new ImageIcon("Screens//about.png").getImage();
	
	private final Image usernameImage = new ImageIcon("Screens//username.png").getImage();
	private final Rectangle textArea = new Rectangle(137, 325, 266, 37);
	private final Rectangle enterButtonArea = new Rectangle(158, 392, 225, 37);
	
	private final Image highscoresImage = new ImageIcon("Screens//highscores.png").getImage();
	private final Rectangle hsEasyButtonArea = new Rectangle(48, 108, 137, 37);
	private final Rectangle hsMediumButtonArea = new Rectangle(202, 108, 137, 37);
	private final Rectangle hsHardButtonArea = new Rectangle(357, 108, 137, 37);
	private final Rectangle hsScoreButtonArea = new Rectangle(48, 556, 137, 37);
	private final Rectangle hsFlowersButtonArea = new Rectangle(202, 556, 137, 37);
	private final Rectangle hsFiresButtonArea = new Rectangle(357, 556, 137, 37);
	private String hsDifficulty = "easy";
	private String hsSort = "score";
	
	// Sets/Lists to store and sort user data for the highscores
	private Set <Profile> easyProfileSet = new TreeSet<>();
	private ArrayList <Profile> easyProfileSetScore = new ArrayList<>();
	private ArrayList <Profile> easyProfileSetFlowers = new ArrayList<>();
	private ArrayList <Profile> easyProfileSetFires = new ArrayList<>();
	
	private Set <Profile> mediumProfileSet = new TreeSet<>();
	private ArrayList <Profile> mediumProfileSetScore = new ArrayList<>();
	private ArrayList <Profile> mediumProfileSetFlowers = new ArrayList<>();
	private ArrayList <Profile> mediumProfileSetFires = new ArrayList<>();
	
	private Set <Profile> hardProfileSet = new TreeSet<>();
	private ArrayList <Profile> hardProfileSetScore = new ArrayList<>();
	private ArrayList <Profile> hardProfileSetFlowers = new ArrayList<>();
	private ArrayList <Profile> hardProfileSetFires = new ArrayList<>();
	
	private Set <Profile> currentSet;		
	
	// Game objects and variables
	private String gameScreen = "mainMenu";
	private String previousGameScreen = "mainMenu";
	private String difficulty = "";
	
	private static boolean gamePaused = false;
	private boolean isHoveringOnPause = false;
	private boolean isTyping = false;
	
	private static boolean gameOver = false;
	
	private Sun gameSun;

	private ArrayList<Plot> gamePlot = new ArrayList<>();
	private int numPlots;
	private int plotSize;
	
	private ArrayList <Projectile> projectiles = new ArrayList<>();
	private final Map <String, Integer> growthMap = Map.of("raindrop", 1, "mediumDrop", 2, "largeDrop", 3);
    
	private long previousClickTime = 0;
	
	private ArrayList<Cloud> gameClouds = new ArrayList<>();
	private int maxClouds;
	private double maxCloudSpeed;
	private double minCloudSpeed;
	private double riskOfThunder;
	
	private final Image fireImage = new ImageIcon("Assets//fire.png").getImage();
	private final Image cloudImage = new ImageIcon("Assets//cloud0.png").getImage();
	private final Image cloudImage2 = new ImageIcon("Assets//cloud0.png").getImage();
	private final Image cloudImage3 = new ImageIcon("Assets//cloud0.png").getImage();
	
	private String username;
	private boolean hasUsername = false;
	private int score = 0;
	private int numFiresSet = 0;
	private int numFlowersPropagated = 0;
	private Map <String, Integer> rarityTracker = new HashMap<>();
	private int hearts = 0;
	private static Image heartImage = new ImageIcon("Assets//Life.png").getImage();
	private ArrayList<Image> heartList = new ArrayList <>();
	
	// Constructor - initializes certain game variables
	// No parameters
	// No return type
	public Driver() {
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setVisible(true); 
		setFocusable(true);
		requestFocusInWindow();
		 
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		
		// Import and initialize a custom font
		try {
			gameFont = Font.createFont(Font.TRUETYPE_FONT, new File("Assets//PressStart2P.ttf"));
			gameFont = gameFont.deriveFont(Font.PLAIN, 12);
			highscoresFont = gameFont.deriveFont(Font.PLAIN, 10);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(gameFont);
		} catch (FontFormatException e) {
			System.out.println("Font file not found");
		} catch (IOException e) {
			System.out.println("Something is deeply wrong with the font file");
		}
		
		// Import and initialize the music and sound effects
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File ("Sounds//burn.wav"));
			burn = AudioSystem.getClip();
			burn.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("Sounds//extinguish.wav"));
			extinguish = AudioSystem.getClip();
			extinguish.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("Sounds//gameBackground.wav"));
			gameBackground = AudioSystem.getClip();
			gameBackground.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("Sounds//gameOver.wav"));
			gameOverSound = AudioSystem.getClip();
			gameOverSound.open(sound);	
			sound = AudioSystem.getAudioInputStream(new File ("Sounds//grow.wav"));
			grow = AudioSystem.getClip();
			grow.open(sound);	
			sound = AudioSystem.getAudioInputStream(new File ("Sounds//hoverSound.wav"));
			hoverSound = AudioSystem.getClip();
			hoverSound.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("Sounds//lightning.wav"));
			lightning = AudioSystem.getClip();
			lightning.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("Sounds//menuBackground.wav"));
			menuBackground = AudioSystem.getClip();
			menuBackground.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("Sounds//propagate.wav"));
			propagate = AudioSystem.getClip();
			propagate.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("Sounds//selectSound.wav"));
			selectSound = AudioSystem.getClip();
			selectSound.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("Sounds//sunRay.wav"));
			sunRay = AudioSystem.getClip();
			sunRay.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("Sounds//waterDrop.wav"));
			waterDrop = AudioSystem.getClip();
			waterDrop.open(sound); 
		} 
		catch (Exception e) {
		}
		
		// Initialize the rest of the game
		gameSun = new Sun();

		addMouseMotionListener(gameSun);
        
        thread = new Thread(this);
		thread.start();
		
		loadProfiles();
	}
	
	// Getters
	public static boolean isGamePaused() {
		return gamePaused;
	}
	
	public static boolean isGameOver() {
		return gameOver;
	}
	
	// Sets the difficulty of the game, adjusting game variables accordingly (number of plots, cloud speed, probability of thunderclouds)
	// Parameters: the new difficulty chosen by the player
	// Returns void
	public void setDifficulty(String newDifficulty) {
		
		// Reset all the game statistics at the start of each game
		score = 0;
		numFiresSet = 0;
		numFlowersPropagated = 0;
		rarityTracker.put("Common", 0);
		rarityTracker.put("Rare", 0);
		rarityTracker.put("Epic", 0);
		
		hearts = 3;
		heartList.clear();
		for (int i = 0; i < hearts; i++) {
			heartList.add(heartImage);
		}
		
		gameClouds.clear();
		projectiles.clear();
		
		if (difficulty.equals(newDifficulty)) {
			// If the difficulty did not change, then don't update the settings
		} else if (newDifficulty.equals("easy")) {
			difficulty = "easy";
			currentSet = easyProfileSet;
			numPlots = 6;
			maxClouds = 5;
			maxCloudSpeed = 1.5;
			minCloudSpeed = 1;
			riskOfThunder = 0.05;
		} else if (newDifficulty.equals("medium")) {
			difficulty = "medium";
			currentSet = mediumProfileSet;
			numPlots = 9;
			maxClouds = 4;
			maxCloudSpeed = 2.25;
			minCloudSpeed = 1.25;
			riskOfThunder = 0.1;
		} else if (newDifficulty.equals("hard")) {
			difficulty = "hard";
			currentSet = hardProfileSet;
			numPlots = 12;
			maxClouds = 4;
			maxCloudSpeed = 2.5;
			minCloudSpeed = 1.5;
			riskOfThunder = 0.15;
		}
		plotSize = screenWidth/numPlots;
		Flower.setSize(plotSize); // Match the flower size to the plot size
		// Update the plots to match the new setting
		gamePlot.clear();
		for (int i = 0; i < numPlots; i++) {
			gamePlot.add(new Plot(i*plotSize, screenHeight-plotSize));
		}
		gamePlot.get(numPlots/2).addFlower();
	}
	
	// Main game loop is in the run() method
    // Parameters: none
    // Returns void
	@Override
	public void run() {
		System.out.println("Thread: Starting thread");
		while(true) {
			if (gameOver) {
				gameBackground.stop();
				gameBackground.setFramePosition(0);
	        	gameOverSound.start();
			}
			if (!gamePaused && !gameOver) {
				gameOverSound.stop();
				gameOverSound.setFramePosition(0);
				
				menuBackground.loop(Clip.LOOP_CONTINUOUSLY);
				
				if (gameScreen.equals("mainGame")) {
					// Update game objects
					moveClouds();
					moveProjectiles();
					checkProjectileHit();
					menuBackground.stop();
					menuBackground.setFramePosition(0);
					gameBackground.loop(Clip.LOOP_CONTINUOUSLY);
				}
			} 	
			this.repaint();
			try {
				Thread.sleep(1000/FPS);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Controls the game's graphics
    // Parameters: Graphics
    // Returns void
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(158, 231, 255));
		g2.fillRect(0, 0, screenWidth, screenHeight);
		
		// Each game screen is controlled by a separate method
		if (gameScreen.equals("mainMenu")) {
			paintMainMenu(g);
		} else if (gameScreen.equals("difficultySelect")) {
			paintDifficultySelect(g);
		} else if (gameScreen.equals("mainGame")) {
			paintGame(g);
		} else if (gameScreen.equals("inputUsername")) {
			paintInputUsername(g);
		} else if (gameScreen.equals("highscores")) {
			paintHighscores(g);
		}else if (gameScreen.equals("instructions")) {
			paintInstructions(g);
		} else if (gameScreen.equals("about")) {
			paintAbout(g);
		}
	}
	
	public void paintMainMenu(Graphics g) {
		g.drawImage(mainMenuImage, 0, 0, screenWidth, screenHeight, null);
		
		if (hoverButton.equals(playButtonArea)) {
			g.drawImage(cloudImage, playButtonArea.x - 65, playButtonArea.y + 2, 50, 50, null);
		} else if (hoverButton.equals(highscoresButtonArea)){
			g.drawImage(cloudImage, highscoresButtonArea.x - 65, highscoresButtonArea.y + 2, 50, 50, null);
		} else if (hoverButton.equals(instructionsButtonArea)) {
			g.drawImage(cloudImage, instructionsButtonArea.x - 65, instructionsButtonArea.y + 2, 50, 50, null);
		} else if (hoverButton.equals(aboutButtonArea)) {
			g.drawImage(cloudImage, aboutButtonArea.x - 65, aboutButtonArea.y + 2, 50, 50, null);
		} else if (hoverButton.equals(exitButtonArea)) {
			g.drawImage(cloudImage, exitButtonArea.x - 65, exitButtonArea.y + 2, 50, 50, null);
		}
	}
	
	// Paints the difficulty select screen
	public void paintDifficultySelect(Graphics g) {
		g.drawImage(difficultySelectImage, 0, 0, screenWidth, screenHeight, null);
		
		if (hoverButton.equals(easyButtonArea)) {
			g.drawImage(cloudImage, easyButtonArea.x - 65, easyButtonArea.y + 30, 50, 50, null);
		} else if (hoverButton.equals(mediumButtonArea)){
			g.drawImage(cloudImage, mediumButtonArea.x - 65, mediumButtonArea.y + 30, 50, 50, null);
		} else if (hoverButton.equals(hardButtonArea)) {
			g.drawImage(cloudImage, hardButtonArea.x - 65, hardButtonArea.y + 30, 50, 50, null);
		} else if (hoverButton.equals(backButtonArea)) {
			g.drawImage(cloudImage, backButtonArea.x - 65, backButtonArea.y + 2, 50, 50, null);
		} 
	}
	
	// Paints the input username screen
	public void paintInputUsername(Graphics g) {
		g.setFont(gameFont);
		g.drawImage(usernameImage, 0, 0, screenWidth, screenHeight, null);
		if (hoverButton.equals(backButtonArea)) {
			g.drawImage(cloudImage, backButtonArea.x - 65, backButtonArea.y + 2, 50, 50, null);
		} else if (hoverButton.equals(enterButtonArea)) {
			g.drawImage(cloudImage, enterButtonArea.x - 25, enterButtonArea.y + 2, 50, 50, null);
		} 
		// Change the background colour of the typing area if the user is able to type
		if (isTyping) {
			g.setColor(new Color(255, 229, 153));
			g.fillRect(textArea.x, textArea.y, textArea.width, textArea.height);
		}
		
		if (username != null) {
			g.setColor(Color.white);
			g.drawString(username, textArea.x+5, textArea.y+24);
		}
	}
	
	// Paint the highscores screen
	public void paintHighscores(Graphics g) {
		g.drawImage(highscoresImage, 0, 0, screenWidth, screenHeight, null);
		if (hoverButton.equals(backButtonArea)) {
			g.drawImage(cloudImage, backButtonArea.x - 65, backButtonArea.y + 2, 50, 50, null);
		} else if (hoverButton.equals(hsEasyButtonArea)) {
			g.drawImage(cloudImage, hsEasyButtonArea.x+42, hsEasyButtonArea.y-15, 50, 50, null);
		} else if (hoverButton.equals(hsMediumButtonArea)) {
			g.drawImage(cloudImage, hsMediumButtonArea.x+42, hsMediumButtonArea.y-15, 50, 50, null);
		} else if (hoverButton.equals(hsHardButtonArea)) {
			g.drawImage(cloudImage, hsHardButtonArea.x+42, hsHardButtonArea.y-15, 50, 50, null);
		} else if (hoverButton.equals(hsScoreButtonArea)) {
			g.drawImage(cloudImage, hsScoreButtonArea.x+42, hsScoreButtonArea.y-15, 50, 50, null);
		} else if (hoverButton.equals(hsFlowersButtonArea)) {
			g.drawImage(cloudImage, hsFlowersButtonArea.x+42, hsFlowersButtonArea.y-15, 50, 50, null);
		} else if (hoverButton.equals(hsFiresButtonArea)) {
			g.drawImage(cloudImage, hsFiresButtonArea.x+42, hsFiresButtonArea.y-15, 50, 50, null);
		}
		
		if (hsDifficulty.equals("easy")) {
			g.drawImage(cloudImage2, hsEasyButtonArea.x+42, hsEasyButtonArea.y-15, 50, 50, null);
		} else if (hsDifficulty.equals("medium")) {
			g.drawImage(cloudImage2, hsMediumButtonArea.x+42, hsMediumButtonArea.y-15, 50, 50, null);
		} else {
			g.drawImage(cloudImage2, hsHardButtonArea.x+42, hsHardButtonArea.y-15, 50, 50, null);
		}
		
		g.setColor(Color.white);
		g.setFont(highscoresFont);
		
		// Display the top profiles for each category
		if (hsSort.equals("score")) {
			g.drawImage(cloudImage3, hsScoreButtonArea.x+42, hsScoreButtonArea.y-15, 50, 50, null);
			
			if (hsDifficulty.equals("easy")) {
				displayProfiles(easyProfileSetScore, g);
			} else if (hsDifficulty.equals("medium")) {
				displayProfiles(mediumProfileSetScore, g);
			} else {
				displayProfiles(hardProfileSetScore, g);
			}
			
		} else if (hsSort.equals("flowers")) {
			g.drawImage(cloudImage3, hsFlowersButtonArea.x+42, hsFlowersButtonArea.y-15, 50, 50, null);
			
			if (hsDifficulty.equals("easy")) {
				displayProfiles(easyProfileSetFlowers, g);
			} else if (hsDifficulty.equals("medium")) {
				displayProfiles(mediumProfileSetFlowers, g);
			} else {
				displayProfiles(hardProfileSetFlowers, g);
			}
			
		} else if (hsSort.equals("fires")) {
			g.drawImage(cloudImage3, hsFiresButtonArea.x+42, hsFiresButtonArea.y-15, 50, 50, null);
			
			if (hsDifficulty.equals("easy")) {
				displayProfiles(easyProfileSetFires, g);
			} else if (hsDifficulty.equals("medium")) {
				displayProfiles(mediumProfileSetFires, g);
			} else {
				displayProfiles(hardProfileSetFires, g);
			}
		}
	}
	
	// Paints the instructions screen
	public void paintInstructions(Graphics g) {
		g.drawImage(instructionsImage, 0, 0, screenWidth, screenHeight, null);
		if (hoverButton.equals(backButtonArea)) {
			g.drawImage(cloudImage, backButtonArea.x - 65, backButtonArea.y + 2, 50, 50, null);
		} 
	}
	
	// Paints the about screen
	public void paintAbout(Graphics g) {
		g.drawImage(aboutImage, 0, 0, screenWidth, screenHeight, null);
		if (hoverButton.equals(backButtonArea)) {
			g.drawImage(cloudImage, backButtonArea.x - 65, backButtonArea.y + 2, 50, 50, null);
		} 
	}
	
	// Paints the main game 
	public void paintGame(Graphics g) {
		
		// Change the pause icon when it is hovered over
		if (isHoveringOnPause) {
			g.drawImage(pauseButtonHover, screenWidth-50, 10, 40, 40, null);
		} else {
			g.drawImage(pauseButton, screenWidth-50, 10, 40, 40, null);
		}
		
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile currentProj = projectiles.get(i);
			currentProj.drawOnSurface(g);
		}
		
		// Sun
		gameSun.drawOnSurface(g);
		g.setColor(Color.red);
		g.setFont(gameFont);
		
		// Flowers and Plots
		int currentNumFlowers = 0;
		for (int i = 0; i < gamePlot.size(); i++) {
			Plot currentPlot = gamePlot.get(i);
			g.drawImage(currentPlot.getPlotImage(), currentPlot.getPosX(), currentPlot.getPosY(), plotSize, plotSize, null);
			if (currentPlot.getFlower() != null) {
				currentNumFlowers++;
				Flower currentFlower = currentPlot.getFlower();
				if (currentFlower.getStatus() == 1) {
					g.drawImage(fireImage, currentPlot.getPosX(), currentPlot.getPosY()-(Flower.getSize()-10), Flower.getSize(), Flower.getSize(), null);
					
					int countdown = currentFlower.updateCountdown()+1;
					g.drawString(countdown+"", currentPlot.getPosX() + (Flower.getSize()/2)-7, currentPlot.getPosY() - (Flower.getSize()-5));
					
					
					if (countdown <= 0) {
						heartList.remove(heartList.size()-1);
						hearts--;
						currentPlot.removeFlower();
					}
				}
				g.drawImage(currentFlower.getImage(), currentPlot.getPosX(), currentPlot.getPosY()-(Flower.getSize()-10), Flower.getSize(), Flower.getSize(), null);
			}
		}
		
		// Check if the game is over
		if (currentNumFlowers == 0 || hearts == 0) {
			gameOver = true;
		}
		
		// Clouds
		for (Cloud c :gameClouds) {
			c.drawOnSurface(g);
		}
		
		// Top left corner score and heart count
		g.setColor(new Color(247, 240, 188));
		g.drawString("Score: "+score, 10, 20);
		
		for (int i = 0; i < heartList.size(); i++) {
			Image life = heartList.get(i);
			if (life!= null) {
				g.drawImage(life, 10 + i*25, 25, 20, 20, null);
			}
		}
		
		// Paint the game paused screen
		if (gamePaused) {
			g.drawImage(pauseImage, 0, 0, screenWidth, screenHeight, null);
			if (hoverButton.equals(endGameButtonArea)) {
				g.drawImage(cloudImage, endGameButtonArea.x - 60, endGameButtonArea.y + 2, 50, 50, null);
			} else if (hoverButton.equals(resumeButtonArea)){
				g.drawImage(cloudImage, resumeButtonArea.x - 60, resumeButtonArea.y + 2, 50, 50, null);
			}
		}
		
		// Paint the game over screen
		if (gameOver) {
			
			g.drawImage(gameOverImage, 0, 0, screenWidth, screenHeight, null);
			if (hoverButton.equals(backToMenuButtonArea)) {
				g.drawImage(cloudImage, backToMenuButtonArea.x - 60, backToMenuButtonArea.y + 2, 50, 50, null);
			} 
			
			// Display game summary statistics on the game over page
			g.drawString("Score:", 158, 338);
			g.drawString(score+"", 290, 338);
			g.drawString("Flowers Bloomed:", 158, 368);
			g.drawString(numFlowersPropagated+"", 360, 368);
			g.drawImage(commonFlowerImage, 158, 378, 28, 28, null);
			g.drawString("Common:", 198, 398);
			g.drawString(rarityTracker.get("Common")+"", 290, 398);
			g.drawImage(rareFlowerImage, 158, 408, 28, 28, null);
			g.drawString("Rare:", 198, 428);
			g.drawString(rarityTracker.get("Rare")+"", 290, 428);
			g.drawImage(epicFlowerImage, 158, 438, 28, 28, null);
			g.drawString("Epic:", 198, 458);
			g.drawString(rarityTracker.get("Epic")+"", 290, 458);
			g.drawString("Fires Set:", 158, 488);
			g.drawString(numFiresSet+"", 290, 488);
		}
	}
	
	// Controls the game's clouds
    // Parameters: none
    // Returns void
	public void moveClouds() {
		// Always have the max number of clouds on screen
		while (gameClouds.size() < maxClouds) { 
			gameClouds.add(new Cloud(screenHeight, screenWidth, maxCloudSpeed, minCloudSpeed, riskOfThunder));
		}
		
		// If a cloud moves off screen, remove it from the gameClouds
		ArrayList<Cloud> cloudsToRemove = new ArrayList<>();
		for (int i = 0; i < gameClouds.size(); i++) {
			Cloud c = gameClouds.get(i);
			c.move();
			if (c.getPosX() < -100 || c.getPosX() > screenWidth+100) {
				cloudsToRemove.add(c);
			}
		}
		gameClouds.removeAll(cloudsToRemove);
		for (int i = 0; i < cloudsToRemove.size(); i++) {
			
		}
		cloudsToRemove.clear();
	}
	
	// Controls the game's projectiles' movement
    // Parameters: none
    // Returns void
	public void moveProjectiles() {
		for (int i = 0; i < projectiles.size(); i++) {
			projectiles.get(i).fall();
		}
	}
	
	// Controls the game's projectiles' collisions
    // Parameters: none
    // Returns void
	public void checkProjectileHit() {
		
		ArrayList<Projectile> projectilesToRemove = new ArrayList<>();
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile currentProj = projectiles.get(i);
			
			// Check for collsions with clouds
			for (int k = 0; k < gameClouds.size(); k++) {
				int projX = currentProj.getPosX() + currentProj.getSize()/2;
				int projY = currentProj.getPosY() + currentProj.getSize();
				
				Cloud cloud = gameClouds.get(k);
				int top = cloud.getPosY()+10;
				int bottom = top + cloud.getSize()/2;
				double left = cloud.getPosX();
				double right = left + cloud.getSize();				
				
				if (projY > top && projY < bottom) {
					if (projX > left && projX < right) {
						String s = currentProj.hitCloud(cloud);
						if (s.equals("lightning")) {
							if (lightning.isRunning()) {
								lightning.stop();
							}
							lightning.setFramePosition(0);
							lightning.start();
						} else {
							if (waterDrop.isRunning()) {
								waterDrop.stop();
							}
							waterDrop.setFramePosition(5000);
							waterDrop.start();
						}
					}
				}
			}
			
			// Check if it collided with the plots
			if (currentProj.getPosY() > 750) {
				projectilesToRemove.add(currentProj);
				
				int posX = currentProj.getPosX();
				int plotIndex = -1;
				for (int j = 0; j < gamePlot.size(); j++) {
					if (posX - gamePlot.get(j).getPosX() > 0) {
						plotIndex++;
					} else {
						break;
					}
				}
				Plot hitPlot = gamePlot.get(plotIndex);
				// Check if projectile collided with flower
				if (hitPlot.getFlower() != null) {
					Flower hitFlower = hitPlot.getFlower();
					// If a sunray or a lightning bolt collided with a flower, set the flower on fire
					if (currentProj.getIdentity().equals("sunray") || currentProj.getIdentity().equals("lightning")) {
						if (hitFlower.getStatus() == 0) {
							hitFlower.setOnFire();
							numFiresSet++;
							burn.setFramePosition(5000);
							burn.start();
						} 
						
					} else {
						// If a water drop collided with a flower, either extinguish a fire or grow the flower
						if (hitFlower.getStatus() == 1) {
							extinguish.setFramePosition(38000);
							extinguish.start();
						} else {
							grow.setFramePosition(10000);
							grow.start();
						}
						int growthPhase = hitFlower.grow(growthMap.get(currentProj.getIdentity()));
						// Every flower growth gains 10 points
						score += growthMap.get(currentProj.getIdentity())*10;
						if (growthPhase == -1) {
							propagate.setFramePosition(0);
							propagate.start();
							// Add points when flowers finish growing
							score += hitFlower.getScore();
							// Update the total number of flowers propagated
							numFlowersPropagated++;
							// Update the rarity tracker
							rarityTracker.put(hitFlower.getRarity(), rarityTracker.get(hitFlower.getRarity())+1);

							// Propagate the flower by removing the flower from the current plot and spreading its seeds to adjacent plots
							hitPlot.removeFlower();
							
							if (plotIndex == 0) {
								Plot rightPlot = gamePlot.get(plotIndex+1);
								if (!rightPlot.isOccupied()) {
									rightPlot.addFlower();
								}
							} else if (plotIndex == numPlots-1) {
								Plot leftPlot = gamePlot.get(plotIndex-1);
								if (!leftPlot.isOccupied()) {
									leftPlot.addFlower();
								}
							} else {
								Plot rightPlot = gamePlot.get(plotIndex+1);
								Plot leftPlot = gamePlot.get(plotIndex-1);
								if (!rightPlot.isOccupied()) {
									rightPlot.addFlower();
								}
								if (!leftPlot.isOccupied()) {
									leftPlot.addFlower();
								}
							}
						}
					}
				}	
				currentProj = null;
			}
		}
		projectiles.removeAll(projectilesToRemove);
		projectilesToRemove.clear();
	}
	
	// Creates new profiles based on data given on a single line
    // Parameters: A string containing all the data of a profile
    // Returns a profile
	public Profile readProfileLine(String line) {
		 StringTokenizer tokenizer = new StringTokenizer(line);
         String difficulty = tokenizer.nextToken();   
         String username = tokenizer.nextToken();  
         int score = Integer.parseInt(tokenizer.nextToken());   
         int numFlowers = Integer.parseInt(tokenizer.nextToken()); 
         int numFires = Integer.parseInt(tokenizer.nextToken());  

         return new Profile(difficulty, username, score, numFlowers, numFires);
	}
	
	// Displays profiles (top 12) using graphics
    // Parameters: A list of profiles, the game's graphics
    // Returns void
	public void displayProfiles(ArrayList <Profile> list, Graphics g) {
		int i = 1;
		Iterator <Profile> iter = list.iterator();
		while (iter.hasNext() && i <= 9) {
			g.drawString(i+". "+iter.next().toString(true), 55, 180+(25*i));
			i++;
		}
		// Remove a space to keep columns aligned when displayed
		while (iter.hasNext() && i <= 12) {
			g.drawString(i+"."+iter.next().toString(true), 55, 180+(25*i));
			i++;
		}
	}
	
	// Loads in the profiles from the textfiles into sets/lists
    // Parameters: none
    // Returns void
	public void loadProfiles() {
		// Load in the highscores from the highscores files and store them in sets
				try {
					BufferedReader reader = new BufferedReader(new FileReader("Data//easyHighscores.txt"));
			        String line;
			        while ((line = reader.readLine()) != null) {
			        	Profile p = readProfileLine(line);
			        	easyProfileSet.add(p);
			        }
			        reader.close();
			        
			        BufferedReader reader2 = new BufferedReader(new FileReader("Data//mediumHighscores.txt"));
			        while ((line = reader2.readLine()) != null) {
			        	Profile p = readProfileLine(line);
			        	mediumProfileSet.add(p);
			        }
			        reader2.close();
			        
			        BufferedReader reader3 = new BufferedReader(new FileReader("Data//hardHighscores.txt"));
			        while ((line = reader3.readLine()) != null) {
			        	Profile p = readProfileLine(line);
			        	hardProfileSet.add(p);
			        }
			        reader3.close();
			        // Update the lists with data from the profile sets
			        easyProfileSetScore.addAll(easyProfileSet);
			        easyProfileSetFlowers.addAll(easyProfileSet);
			        easyProfileSetFires.addAll(easyProfileSet);
			        
			        mediumProfileSetScore.addAll(mediumProfileSet);
			        mediumProfileSetFlowers.addAll(mediumProfileSet);
			        mediumProfileSetFires.addAll(mediumProfileSet);
			        
			        hardProfileSetScore.addAll(hardProfileSet);
			        hardProfileSetFlowers.addAll(hardProfileSet);
			        hardProfileSetFires.addAll(hardProfileSet);
			        
			        // Sort all of the list for the first time
					Collections.sort(easyProfileSetScore, new SortByScore());
					Collections.sort(easyProfileSetFlowers, new SortByNumFlowers());
					Collections.sort(easyProfileSetFires, new SortByNumFires());
					
					Collections.sort(mediumProfileSetScore, new SortByScore());
					Collections.sort(mediumProfileSetFlowers, new SortByNumFlowers());
					Collections.sort(mediumProfileSetFires, new SortByNumFires());
					
					Collections.sort(hardProfileSetScore, new SortByScore());
					Collections.sort(hardProfileSetFlowers, new SortByNumFlowers());
					Collections.sort(hardProfileSetFires, new SortByNumFires());
					
			    } catch (FileNotFoundException e) {
			        System.out.println("Can't find the highscores file!");
			    } catch (IOException e) {
			        System.out.println("Something is wrong with the highscores file!");
			    }
	}
	
	// Insert a profile into an already sorted list
    // Parameters: the list to insert the profile into, the profile, and the comparator by which the list was sorted
    // Returns void
	public void insertProfile(ArrayList<Profile> list, Profile p, Comparator<Profile> c) {
		int i = Collections.binarySearch(list, p, c);
		// If it is not in the list, get the insertion position according to the negative value that binarySearch returns
		if (i < 0) {
			i = -(i+1);
		}
        list.add(i, p);
	}
	
	// Update all the profile sets and lists, keeping them sorted
    // Parameters: nothing
    // Returns void
	public void updateProfiles() {
		Profile p = new Profile(difficulty, username, score, numFlowersPropagated, numFiresSet);
		if (currentSet != null) {
		
			// Lists have already been sorted, so insert into the appropriate location using binarySearch
			if (currentSet.contains(p)) {
				ArrayList <Profile> profileList = new ArrayList<>(currentSet);
				int index = Collections.binarySearch(profileList, new Profile (difficulty, username, 0, 0 ,0));
				if (p.getScore() > profileList.get(index).getScore()) {
					// If the current score is a new highscore for a particular username, then replace the profile with the new highscore
					// If not, keep the current score, which is higher
					Profile oldProfile = profileList.get(index);
					if (difficulty.equals("easy")) {
						easyProfileSet.remove(oldProfile);
						easyProfileSet.add(p);
						easyProfileSetScore.remove(oldProfile);
						easyProfileSetFlowers.remove(oldProfile);
						easyProfileSetFires.remove(oldProfile);
						
						insertProfile(easyProfileSetScore, p, new SortByScore());
						insertProfile(easyProfileSetFlowers, p, new SortByNumFlowers());
						insertProfile(easyProfileSetFires, p, new SortByNumFires());
						
					} else if (difficulty.equals("medium")) {
						mediumProfileSet.remove(profileList.get(index));
						mediumProfileSet.add(p);
						mediumProfileSetScore.remove(oldProfile);
						mediumProfileSetFlowers.remove(oldProfile);
						mediumProfileSetFires.remove(oldProfile);
						
						insertProfile(mediumProfileSetScore, p, new SortByScore());
						insertProfile(mediumProfileSetFlowers, p, new SortByNumFlowers());
						insertProfile(mediumProfileSetFires, p, new SortByNumFires());
						
					} else {
						hardProfileSet.remove(profileList.get(index));
						hardProfileSet.add(p);
						hardProfileSetScore.remove(oldProfile);
						hardProfileSetFlowers.remove(oldProfile);
						hardProfileSetFires.remove(oldProfile);
						
						insertProfile(hardProfileSetScore, p, new SortByScore());
						insertProfile(hardProfileSetFlowers, p, new SortByNumFlowers());
						insertProfile(hardProfileSetFires, p, new SortByNumFires());
						
					}
				}
			} else {
				// If the username is new, then insert the profile into the appropriate lists/sets
				if (difficulty.equals("easy")) {
					easyProfileSet.add(p);
					insertProfile(easyProfileSetScore, p, new SortByScore());
					insertProfile(easyProfileSetFlowers, p, new SortByNumFlowers());
					insertProfile(easyProfileSetFires, p, new SortByNumFires());
				} else if (difficulty.equals("medium")) {
					mediumProfileSet.add(p);
					insertProfile(mediumProfileSetScore, p, new SortByScore());
					insertProfile(mediumProfileSetFlowers, p, new SortByNumFlowers());
					insertProfile(mediumProfileSetFires, p, new SortByNumFires());
				} else {
					hardProfileSet.add(p);
					insertProfile(hardProfileSetScore, p, new SortByScore());
					insertProfile(hardProfileSetFlowers, p, new SortByNumFlowers());
					insertProfile(hardProfileSetFires, p, new SortByNumFires());
				}
			}
		}	
	}
	
	// Write the profile sets into the corresponding textfiles
    // Parameters: none
    // Returns void
	public void saveProfiles() {
		try {
	        BufferedWriter writer = new BufferedWriter(new FileWriter("Data//easyHighscores.txt"));
	        Iterator<Profile> iter = easyProfileSet.iterator();
	        while (iter.hasNext()) {
	            Profile profile = iter.next();
	            writer.write(profile.toString()+"\n");
	        }
	        writer.close();
	        
	        BufferedWriter writer2 = new BufferedWriter(new FileWriter("Data//mediumHighscores.txt"));
	        Iterator<Profile> iter2 = mediumProfileSet.iterator();
	        while (iter2.hasNext()) {
	            Profile profile = iter2.next();
	            writer2.write(profile.toString() +"\n");
	        }
	        writer2.close();
	        
	        BufferedWriter writer3 = new BufferedWriter(new FileWriter("Data//hardHighscores.txt"));
	        Iterator<Profile> iter3 = hardProfileSet.iterator();
	        while (iter3.hasNext()) {
	            Profile profile = iter3.next();
	            writer3.write(profile.toString() +"\n");
	        }
	        writer3.close();
	        
	    } catch (FileNotFoundException f) {
	    	System.out.println("Highscores file not found!");
	    } catch (IOException f) {
	        System.out.println("Something is deeply wrong with highscores file!");
	    } 
	}
	
	// Mainly checks for button clicks by comparing the clicked point to the rectangle areas that represent buttons
    // Parameters: MouseEvent
    // Returns void
	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		if (gameScreen.equals("mainMenu")) {
			if (playButtonArea.contains(p)) {
				if (!hasUsername) {
					gameScreen = "inputUsername";
				} else {
					gameScreen = "difficultySelect";
				}
			} else if (highscoresButtonArea.contains(p)){
				updateProfiles();
        		saveProfiles();
				gameScreen = "highscores";
			} else if (instructionsButtonArea.contains(p)){
				gameScreen = "instructions";
			} else if (aboutButtonArea.contains(p)){
				gameScreen = "about";
			} else if (exitButtonArea.contains(p)){
				System.exit(0);
			} 
			
		} else if (gameScreen.equals("difficultySelect")) {
			if (easyButtonArea.contains(p)) {
				gameScreen = "mainGame";
				setDifficulty("easy");
			} else if (mediumButtonArea.contains(p)){
				gameScreen = "mainGame";
				setDifficulty("medium");
			} else if (hardButtonArea.contains(p)){
				gameScreen = "mainGame";
				setDifficulty("hard");
			} else if (backButtonArea.contains(p)){
				gameScreen = "mainMenu";
			} 
			previousClickTime = System.currentTimeMillis()+200;
			
		} else if (gameScreen.equals("mainGame")) {
	        if (gamePaused) {
	        	if (endGameButtonArea.contains(p)) {
	        		gamePaused = false;
		        	gameOver = true;
		        } else if (resumeButtonArea.contains(p)) {
		        	gamePaused = false;
		        	previousClickTime = System.currentTimeMillis()+200;
		        }
	        } else if (gameOver) {
	        	if (backToMenuButtonArea.contains(p)) {
	        		updateProfiles();
	        		saveProfiles();
	        		gameScreen = "mainMenu";
		        	gameOver = false;
		        } 
	        } else {
	        	
	        	if (!isHoveringOnPause) {
	        		// Every click creates a sun ray that shoots down from the sun
	        		long currentTime = System.currentTimeMillis();
					
			        if (currentTime - previousClickTime >= 200) {
			        	if (sunRay.isRunning()) {
			        		sunRay.stop();
			        	}
			            previousClickTime = currentTime;
			            projectiles.add(new Projectile(gameSun.getSunX(), gameSun.getSunY()));
			            sunRay.setFramePosition(1000);
			            sunRay.start();
			        }
	        	}
	        	if (pauseButtonArea.contains(p)) {
		        	gamePaused = true;
		        }
	        } 
		} else if (gameScreen.equals("inputUsername")) {
			// Allow the user to type if the typing area has been clicked, and stop typing privileges when typing area is clicked off of
			if (textArea.contains(p)) {
				isTyping = true;
			} else {
				isTyping = false;
			}
			
			if (enterButtonArea.contains(p)) {
				// Allow the user to enter the username only if it isn't empty
				if (username != null && !username.isEmpty()) {
					hasUsername = true;
					gameScreen = "difficultySelect";
				}
			}
			
			if (backButtonArea.contains(p)){
				gameScreen = "mainMenu";
			} 
		} else if (gameScreen.equals("highscores")) {
			if (backButtonArea.contains(p)){
				gameScreen = "mainMenu";
			} else if (hsEasyButtonArea.contains(p)) {
				hsDifficulty = "easy";
			} else if (hsMediumButtonArea.contains(p)) {
				hsDifficulty = "medium";
			} else if (hsHardButtonArea.contains(p)) {
				hsDifficulty = "hard";
			} else if (hsScoreButtonArea.contains(p)) {
				hsSort = "score";
			} else if (hsFlowersButtonArea.contains(p)) {
				hsSort = "flowers";
			} else if (hsFiresButtonArea.contains(p)) {
				hsSort = "fires";
			}
		}
		
		else if (gameScreen.equals("instructions") || gameScreen.equals("about")) {
			if (backButtonArea.contains(p)){
				gameScreen = "mainMenu";
			} 
		} 
		
		// If the screen changes, play a screen change sound effect
		if (!gameScreen.equals(previousGameScreen)) {
			      if (selectSound.isRunning()) {
			    	  selectSound.stop();
			     	}
			      selectSound.setFramePosition(0);
			      selectSound.start();
			   
			  previousGameScreen = gameScreen; 
			  }
		}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	// Mainly checks for button hovers by comparing the mouse point to the rectangle areas that represent buttons
    // Parameters: MouseEvent
    // Returns void
	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = e.getPoint();
		
		if (gameScreen.equals("mainMenu")) {
			if (playButtonArea.contains(p)) {
				hoverButton = playButtonArea;
			} else if (highscoresButtonArea.contains(p)){
				hoverButton = highscoresButtonArea;
			} else if (instructionsButtonArea.contains(p)){
				hoverButton = instructionsButtonArea;
			} else if (aboutButtonArea.contains(p)){
				hoverButton = aboutButtonArea;
			} else if (exitButtonArea.contains(p)){
				hoverButton = exitButtonArea;
			} else {
				hoverButton = nullArea;
			}
		} else if (gameScreen.equals("difficultySelect")) {
			if (easyButtonArea.contains(p)) {
				hoverButton = easyButtonArea;
			} else if (mediumButtonArea.contains(p)){
				hoverButton = mediumButtonArea;
			} else if (hardButtonArea.contains(p)){
				hoverButton = hardButtonArea;
			} else if (backButtonArea.contains(p)){
				hoverButton = backButtonArea;
			} else {
				hoverButton = nullArea;
			}
		} else if (gameScreen.equals("mainGame")) {
			if (pauseButtonArea.contains(p)) {
				isHoveringOnPause = true;
			} else {
				isHoveringOnPause = false;
			}
			
			if (gamePaused) {
				if (endGameButtonArea.contains(p)) {
					hoverButton = endGameButtonArea;
				} else if (resumeButtonArea.contains(p)){
					hoverButton = resumeButtonArea;
				} else {
					hoverButton = nullArea;
				}
			} else if (gameOver) {
				if (backToMenuButtonArea.contains(p)) {
					hoverButton = backToMenuButtonArea;
				} else {
					hoverButton = nullArea;
				}
			}
		} else if (gameScreen.equals("inputUsername")) {
			if (enterButtonArea.contains(p)) {
				hoverButton = enterButtonArea;
			} else if (backButtonArea.contains(p)){
				hoverButton = backButtonArea;
			} else {
				hoverButton = nullArea;
			}
		} else if (gameScreen.equals("highscores")) {
			if (hsEasyButtonArea.contains(p)) {
				hoverButton = hsEasyButtonArea;
			} else if (hsMediumButtonArea.contains(p)){
				hoverButton = hsMediumButtonArea;
			} else if (hsHardButtonArea.contains(p)){
				hoverButton = hsHardButtonArea;
			} else if (hsScoreButtonArea.contains(p)){
				hoverButton = hsScoreButtonArea;
			} else if (hsFlowersButtonArea.contains(p)){
				hoverButton = hsFlowersButtonArea;
			} else if (hsFiresButtonArea.contains(p)){
				hoverButton = hsFiresButtonArea;
			} else if (backButtonArea.contains(p)){
				hoverButton = backButtonArea;
			} else {
				hoverButton = nullArea;
			}
		} else if (gameScreen.equals("instructions") || gameScreen.equals("about")) {
			if (backButtonArea.contains(p)){
				hoverButton = backButtonArea;
			} else {
				hoverButton = nullArea;
			}
		}
		
		// If whatever button cursor is hovering on changes, play the hover sound effect
		if (hoverButton != lastHoverButton) {
	        if (hoverButton != nullArea) { 
	        	if (hoverSound.isRunning()) {
	        		hoverSound.stop();
	        	}
	        	hoverSound.setFramePosition(0);
	            hoverSound.start();
	        }
	        lastHoverButton = hoverButton; 
	    }
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}
	
	// If the typing area is activated, the the method registers the keys typed by the user (restricted to letters and digits)
    // Parameters: KeyEvent
    // Returns void
	@Override
	public void keyTyped(KeyEvent e) {
		if (isTyping) {
			Character typedChar = e.getKeyChar();
			
			if (username == null) {
				username = "";
			}
			// Updates the username
			if (username.length() <= 15 && Character.isLetterOrDigit(typedChar)) {
				username =  username + typedChar;
			}
		}
	}

	// If the typing area is activated, allows the user to backspace
    // Parameters: KeyEvent
    // Returns void
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == 8 && username.length() > 0) {
			username = username.substring(0, username.length() - 1);
		} 
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame("Sunsprout");
		Driver driver = new Driver();
		frame.add(driver);
		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}