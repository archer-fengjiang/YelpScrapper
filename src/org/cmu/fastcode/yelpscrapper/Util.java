package org.cmu.fastcode.yelpscrapper;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

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
public class Util {
	
	/**
	 * This method is responsible for parsing HTML into DOM
	 * So DOM could be reused
	 * 
	 * input:  URL of any web page
	 * return: Document representing the DOM of this page
	 * */
	static Document getDOM(String url) throws IOException{
		SecureRandom random = new SecureRandom();
		
		return Jsoup
				.connect(url)
				.header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
				.timeout(0)
				.get();
	}
	
	/**
	 * This method is responsible for getting all biz lists for one
	 * biz-list page
	 * 
	 * input: DOM of biz-list page
	 * return: ArrayList of URL of each biz link
	 * @throws IOException 
	 * */
	static List<String> bizPageGetBizLinks(Document doc){
		List<String> bizLinkLists = new ArrayList<String>();
		Elements bizListElements = doc.getElementsByClass("itemheading");
		for(Element e : bizListElements){
			bizLinkLists.add(e.select("a[href*=/biz]").first().attr("abs:href"));
		}
		return bizLinkLists;
	}
	
	/**
	 * This method is responsible for getting next page displaying biz list
	 * In case no next page is found, return null
	 * 
	 * input: Document of biz_list page
	 * return: URL of next biz_list page
	 * */
	static String bizPageGetNextPage(Document doc){
		Element e = doc.getElementById("pager_page_next");
		if(e != null){
			return e.attr("abs:href").replaceAll("attrs=&cflt=&","");
		} else {
			return null;
		}
	}
	
	/**
	 * This method will take DOM of page displaying reviews for one biz
	 * and return one list containing review-rating pairs
	 * 
	 * If no reviews in this page, return empty list
	 * 
	 * input:  review page DOM
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
	static List<ReviewRatingPair> reviewPageGetReviewRatingPairs(Document doc) {
		List<ReviewRatingPair> list = new ArrayList<ReviewRatingPair>();
		if(doc.getElementById("reviews-other") == null){
			return list;
		}
		Elements reviews = doc.select("li[class=review clearfix  externalReview]");
		for(Element review : reviews){
			 String text = review.select("p[class=review_comment ieSucks]").first().text();
			 String ratingStr = review.select("meta[itemprop=ratingValue]").first().attr("content");
			 float rating = Float.parseFloat(ratingStr);
			 list.add(new ReviewRatingPair(text, rating));
		}
		return list;
	}
	
	/**
	 * 
	 * return null if next page doesn't exist
	 * 
	 * <div id="paginationControls">
	 *     <span class="highlight2"> current page </span>
	 *     <a href="biz/..."> ...
	 * */
	static String reviewPageGetNextPage(Document doc){
		Element pagingControl = doc.getElementById("paginationControls");
		if(pagingControl == null){
			return null;
		}
		Element currentPage = pagingControl.select("span[class=highlight2]").first();
		Element nextPage = currentPage.nextElementSibling();
		if(nextPage == null){
			return null;
		}
		return nextPage.attr("abs:href");
	}
	
	public static void main(String[] args) throws IOException{
		test8();
	}
	
	
	
	
	
	
	
	
	
	
	
	/********************Unit Tests****************************/
	
	/**
	 * Display review-rating pairs for page without such info
	 * */
	private static void test1(){
		String url = "http://www.yelp.com/biz/poona-badminton-club-nyc-queens-2";
		try {
			Document doc = getDOM(url);
			List<ReviewRatingPair> list = reviewPageGetReviewRatingPairs(doc);
			System.out.println("for url:" +url +"\nsize of review rating pairs is :" + list.size());
			for(ReviewRatingPair p : list){
				System.out.println(p.rating + " " + p.review);
			}
		} catch (IOException exception) {
		}
	}
	
	/**
	 * Display review-rating pairs for page with reviews and next pages
	 * */
	private static void test2(){
		String url = "http://www.yelp.com/biz/usa-badminton-sports-inc-college-point";
		try {
			Document doc = getDOM(url);
			List<ReviewRatingPair> list = reviewPageGetReviewRatingPairs(doc);
			System.out.println("for url:" +url +"\nsize of review rating pairs is :" + list.size());
			for(ReviewRatingPair p : list){
				System.out.println(p.rating + " " + p.review);
			}
		} catch (IOException exception) {
		}
	}
	
	/**
	 * Display Review-RatingPairs On Page Without Next Page
	 * */
	private static void test3(){
		String url = "http://www.yelp.com/biz/usa-badminton-sports-inc-college-point";
		try {
			Document doc = getDOM(url);
			List<ReviewRatingPair> list = reviewPageGetReviewRatingPairs(doc);
			System.out.println("for url:" +url +"\nsize of review rating pairs is :" + list.size());
			for(ReviewRatingPair p : list){
				System.out.println(p.rating + " " + p.review);
			}
		} catch (IOException exception) {
		}
	}
	
	/**
	 * Display next page of review on page without next page, should show null
	 * */
	private static void test4() throws IOException{
		String url = "http://www.yelp.com/biz/gramercy-tavern-new-york?start=880";
		Document dom = getDOM(url);
		String nextPageURL = reviewPageGetNextPage(dom);
		System.out.println("for url:" + url + "\n next page is:" + nextPageURL);
	}
	
	/**
	 * Display all review pages of one biz
	 * */
	private static void test5() throws IOException{
		String url = "http://www.yelp.com/biz/ai-fiori-new-york";
		System.out.println("for reivew page:" + url);
		System.out.println("all reivew pages are:");
		Document dom;
		while(url != null){
			dom = getDOM(url);
			url = reviewPageGetNextPage(dom);
			System.out.println(url);
		}
	}
	
	/**
	 * Display all biz-list page links of one search
	 * */
	private static void test6() throws IOException{
		String url = "http://www.yelp.com/search?find_desc=restaurants&find_loc=New+York%2C+NY&ns=1#start=870";
		System.out.println("for biz list page:" + url);
		System.out.println("all biz list pages are:");
		Document dom;
		while(url != null){
			dom = getDOM(url);
			url = bizPageGetNextPage(dom);
			System.out.println(url);
		}
	}
	
	/**
	 * Display all biz-links on one biz search result page
	 * @throws IOException 
	 * */
	private static void test7() throws IOException {
		String url = "http://www.yelp.com/search?find_desc=restaurants&find_loc=New+York%2C+NY&ns=1#start=870";
		System.out.println("for url:" + url);
		System.out.println("All biz links are:");
		
		Document dom = getDOM(url);
		List<String> list = BizListGetter.bizPageGetBizLinks(dom);
		
		for(String str : list){
			System.out.println(str);
		}
	}
	
	/**
	 * Display all review pages of one biz
	 * @throws IOException 
	 * */
	private static void test8() throws IOException{
		String url = "http://www.yelp.com/biz/gramercy-tavern-new-york";
		System.out.println("for review page:" + url);
		System.out.println("all review pages are:");
		Document dom;
		while(url != null){
			dom = getDOM(url);
			url = reviewPageGetNextPage(dom);
			System.out.println(url);
		}
	}
}

