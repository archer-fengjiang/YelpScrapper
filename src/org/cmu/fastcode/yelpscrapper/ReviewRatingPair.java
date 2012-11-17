package org.cmu.fastcode.yelpscrapper;

/**
 * This class stores a review and rating pair
 *
 * @author Fengjiang.
 *         Created Nov 17, 2012.
 */
public class ReviewRatingPair{
	public String review;
	public float rating;

	public ReviewRatingPair(String str, float f){
		this.review = str;
		this.rating = f;
	}
}
