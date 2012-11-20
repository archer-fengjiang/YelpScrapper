package org.cmu.fastcode.yelpscrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

/**
 * This class is responsible for scraping reviews
 *
 * @author Fengjiang.
 *         Created Nov 18, 2012.
 */
public class ReviewScrapper {
	private String bizLinksFileName;
	private String reviewFileName;
	private BufferedReader fileReader;
	private JsonWriter jsonWriter;
	
	public ReviewScrapper(String bizLinksFileName){
		this.bizLinksFileName = bizLinksFileName;
		this.reviewFileName = bizLinksFileName.split("\\[")[0] + "_review"
				+ Util.getCurrentDateString();
	}
	
	
	/**
	 * This method will read file containing biz-links and return list of URL
	 * @throws IOException when read file IO fails
	 * */
	private List<String> readBizLinksFromFile() throws IOException {
		this.fileReader = new BufferedReader(new FileReader(this.bizLinksFileName));
		System.out.print("read file " + this.bizLinksFileName +" success...");
		List<String> bizLinkList = new ArrayList<String>();
		String line;
		while((line = this.fileReader.readLine()) != null){
			bizLinkList.add(line);
		}
		this.fileReader.close();
		System.out.println("form biz link success, " + bizLinkList.size() + " links acquired...");
		return bizLinkList;
	}
	
	/**
	 * This method will write review-rating pairs to file in json format
	 * @throws IOException when read or write file IO fails
	 * */
	public void writeReviews() throws IOException{
		this.jsonWriter = new JsonWriter(new BufferedWriter(new FileWriter(this.reviewFileName)));
		Gson gson = new Gson();
		List<String> bizLinks;
		try {
			bizLinks = this.readBizLinksFromFile();
		} catch (IOException exception) {
			exception.printStackTrace();
			return;
		}
		
		Document dom;
		List<ReviewRatingPair> reviews;
		String reviewPageURL;
		this.jsonWriter.setIndent("    ");
		this.jsonWriter.beginArray();
		for(String bizURL : bizLinks){
			// Iterate biz
			System.out.println("new biz:" + bizURL);
			reviewPageURL = bizURL;
			
			while(reviewPageURL != null){
				System.out.println("\t...pasing review page:"+reviewPageURL);
				// Iterate review page
				try{
					dom = Util.getDOM(reviewPageURL);
				} catch(IOException e){
					// IOException from fetching URL from yelp 
					// will stop scraping review for this biz
					// and will jump to next biz if possible
					break;
				}
				reviews = Util.reviewPageGetReviewRatingPairs(dom);
				System.out.println("\tdone get reviews, size:" + reviews.size() 
						+ "\n\t...passing reviews to json");
				for(ReviewRatingPair pair : reviews){
					gson.toJson(pair, ReviewRatingPair.class, this.jsonWriter);
				}
				this.jsonWriter.flush();
				System.out.println("\tdone flushing out reviews to json file");
				reviewPageURL = Util.reviewPageGetNextPage(dom);
			}
			System.out.println("done scrapping review for biz:" + bizURL);
		}
		this.jsonWriter.endArray();
		this.jsonWriter.close();
	}
	
	
	public static void main(String[] args){
		ReviewScrapper scrapper = new ReviewScrapper("restaurant_sf[2012-11-19 00:38:04]");
		try {
			scrapper.writeReviews();
		} catch (IOException exception) {
			// TODO Auto-generated catch-block stub.
			exception.printStackTrace();
		}
	}
}
