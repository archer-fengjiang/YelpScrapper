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
	 * This method is responsible for parsing HTML into DOM
	 * So DOM could be reused
	 * 
	 * input:  URL of any web page
	 * return: Document representing the DOM of this page
	 * */
	static Document getDOM(String url) throws IOException{
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
	static List<String> bizPageGetBizLinks(Document doc) throws IOException{
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
			return e.attr("abs:href");
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
	 * This method will get List of pages after this review page
	 * the order of URL inside the list will be same as displayed on yelp
	 * return empty list if next page doesn't exist
	 * 
	 * input:  url of current review page
	 * return: ArrayLists of url of pages afte current pages
	 * <div id="paginationControls">
	 *     <span class="highlight2"> current page </span>
	 *     <a href="biz/..."> ...
	 * */
	static List<String >reviewPageGetFollowingPages(Document doc){
		List<String> list = new ArrayList<String>();
		Element pagingControl = doc.getElementById("paginationControls");
		if(pagingControl == null){
			return list;
		}
		Element currentPage = pagingControl.select("span[class=highlight2]").first();
		Element nextPage = currentPage.nextElementSibling();
		while(nextPage != null){
			list.add(nextPage.attr("abs:href"));
			nextPage = nextPage.nextElementSibling();
		}
		return list;
	}
	
	public static void main(String[] args) throws IOException{
		//testZeroReviewOnReviewPage();
		//System.out.println();
		//testMultiReviewsOnReviewPage();
		testReviewPageWithoutNextPage();
		
		testReviewPageWithNextPage();
	}
	
	private static void testZeroReviewOnReviewPage(){
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
	
	private static void testMultiReviewsOnReviewPage(){
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
	
	private static void testNextPageWithoutNextPage(){
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
	
	private static void testReviewPageWithoutNextPage() throws IOException{
		String url = "http://www.yelp.com/biz/gramercy-tavern-new-york?start=880";
		Document dom = getDOM(url);
		List<String> nextPageURLList = reviewPageGetFollowingPages(dom);
		System.out.println("for url:" + url + "\nnext page is:");
		for(String str : nextPageURLList){
			System.out.println("\t" + str);
		}
	}
	
	private static void testReviewPageWithNextPage() throws IOException{
		String url = "http://www.yelp.com/biz/gramercy-tavern-new-york?start=400";
		Document dom = getDOM(url);
		List<String> nextPageURLList = reviewPageGetFollowingPages(dom);
		System.out.println("for url:" + url + "\nnext page is:");
		for(String str : nextPageURLList){
			System.out.println("\t" + str);
		}
	}
}

