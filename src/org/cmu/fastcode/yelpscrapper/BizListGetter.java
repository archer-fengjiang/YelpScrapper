package org.cmu.fastcode.yelpscrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class is responsible for getting list of all business links
 * on business page
 *
 * @author Fengjiang.
 *         Created Nov 15, 2012.
 */
public class BizListGetter {
	
	/**
	 * This method is responsible for getting all biz lists for one
	 * biz-list page
	 * 
	 * input: URL of biz-list page
	 * return: List of URL of each biz link
	 * @throws IOException 
	 * */
	static List<String> getBizLinkLists(String bizPageUrl) throws IOException{
		List<String> bizLinkLists = new ArrayList<String>();
		Document doc = Jsoup.connect(bizPageUrl).get();
		Elements bizListElements = doc.getElementsByClass("itemheading");
		for(Element e : bizListElements){
			bizLinkLists.add(e.select("a[href*=/biz]").first().attr("abs:href"));
		}
		return bizLinkLists;
	}
	
	static String getNextPageBizList(String bizPageUrl) throws IOException{
		Document doc = Jsoup.connect(bizPageUrl)
				.header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
				.timeout(0)
				.get();
		return doc.getElementById("pager_page_next").attr("abs:href");
	}
	
	
	
	/**
	 * This method will take URL of page displaying reviews for one biz
	 * and return one list containing review-rating pairs 
	 * 
	 * input:  review page URL
	 * return: ArrayList of ReviewRatingPair
	 * 
	 * Note the DOM structure of review page 
	 * (ie http://www.yelp.com/biz/central-park-new-york) is:
	 * 
	 * -id:reviews-other
	 *     -class:review clearfix  externalReview
	 *         -class:review_comment ieSucks -> review text
	 *         -<meta itemprop="ratingValue" content="5.0">
	 * */
	static List<ReviewRatingPair> getReviewRatingPairs(String url) throws IOException{
		List<ReviewRatingPair> list = new ArrayList<ReviewRatingPair>();
		Document doc = Jsoup.connect(url)
				.header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
				.timeout(0)
				.get();
		//Element reviewContainer = doc.getElementById("reviews-other");
		Elements reviews = doc.select("li[class=review clearfix  externalReview]");
		for(Element review : reviews){
			 String text = review.select("p[class=review_comment ieSucks]").first().text();
			 String ratingStr = review.select("meta[itemprop=ratingValue]").first().attr("content");
			 float rating = Float.parseFloat(ratingStr);
			 list.add(new ReviewRatingPair(text, rating));
		}
		return list;
	}
	
	public static void main(String[] args) throws IOException{
		String url = "http://www.yelp.com/biz/central-park-new-york";
		List<ReviewRatingPair> list = getReviewRatingPairs(url);
		for(ReviewRatingPair p : list){
			System.out.println(p.rating + " " + p.review);
		}
	}
}

