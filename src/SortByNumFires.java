// Andrew Law
// Jan 19, 2024
// Sort by numFires comparator

import java.util.*;

public class SortByNumFires implements Comparator <Profile> {

	// Method compares numFires of two profiles to sort in descending order
	// Parameters: the two profiles that you are comparing 
	// Returns: negative int if a>b, positive int if a<b, 0 if they're equal 
	public int compare(Profile a, Profile b) {
		return -(a.getNumFires() - b.getNumFires());
	}
	
}
