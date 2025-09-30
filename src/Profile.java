// Andrew Law
// January 19, 2024
/*
 * Profile class - Stores user data
 */

public class Profile implements Comparable <Profile> {

	private String username;
	private int score;
	private int numFlowers;
	private int numFires;
	private String difficulty;
	
    // Constructor
 	// Creates a Profile object to store user data on a particular game played
 	// Parameters: difficulty of the game, username, score, numFlowers, numFires
 	// Does not return any type
	public Profile(String difficulty, String username, int score, int numFlowers, int numFires) {
		this.difficulty = difficulty;
		this.username = username;
		this.score = score;
		this.numFlowers = numFlowers;
		this.numFires = numFires;
	}
	
	public String getDifficulty(){
		return difficulty;
	}
	
	public String getUsername(){
		return username;
	}
	
	public int getScore(){
		return score;
	}
	
	public int getNumFlowers(){
		return numFlowers;
	}
	
	public int getNumFires(){
		return numFires;
	}
	
	public String toString() {
		return String.format("%8s %25s %8d %6d %6d", difficulty, username, score, numFlowers, numFires);
	}
	
	// Overridden toString is for displaying in the highscore table. Otherwise, it is for updating the textfile
	public String toString(boolean isForHighscore) {
		if (isForHighscore) {
			return String.format("%-15s %-7d %-8d %-6d", username, score, numFlowers, numFires);
		} else {
			return String.format("%8s %25s %8d %6d %6d", difficulty, username, score, numFlowers, numFires);
		}
	}

	// Method compares usernames and difficulty to determine if two profiles are equal
	// Parameters: Object that you're comparing the current profile to
	// Returns: true if both difficulty and username are the same, false if not
	public boolean equals(Object o) {
		Profile p = (Profile)o;
		return this.username.equalsIgnoreCase(p.username) && this.difficulty.equalsIgnoreCase(p.difficulty);
	}
	
	// Method compares usernames to sort usernames alphabetically
	// Parameters: the profile you're comparing the current instance to
	// Returns: positive int if username a goes before username b, negative int if username a goes after username b, 0 if they're the same 
	public int compareTo(Profile p) {
		return this.username.compareToIgnoreCase(p.username);
	}
}
