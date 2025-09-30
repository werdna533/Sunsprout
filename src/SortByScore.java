// Andrew Law
// Jan 19, 2024
// Sort by score comparator

import java.util.*;

public class SortByScore implements Comparator <Profile> {

	// Method compares scores of two profiles to sort in descending order
	// Parameters: the two profiles that you are comparing 
	// Returns: negative int if a>b, positive int if a<b, 0 if they're equal 
	public int compare(Profile a, Profile b) {
		return -(a.getScore() - b.getScore());
	}
	
}
