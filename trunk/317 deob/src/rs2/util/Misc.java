package rs2.util;

import java.util.HashSet;
import java.util.Set;

public class Misc {

	/**
	 * Reverses the order of values in a given int array.
	 * @param data
	 */
	public static int[] reverse(int[] data) {
	    int left = 0;
	    int right = data.length - 1;
	    while(left < right) {
	        int temp = data[left];
	        data[left] = data[right];
	        data[right] = temp;
	        left++;
	        right--;
	    }
	    return data;
	}

	/**
	 * Returns whether or not an array contains duplicates.
	 * @param list
	 * @return
	 */
	public static boolean containsDuplicates(final int[] list) {
		Set<Integer> lump = new HashSet<Integer>();
		for (int i : list) {
			if (lump.contains(i)) {
				return true;
			}
			lump.add(i);
		}
		return false;
	}

}
