package org.cmu.fastcode.yelpscrapper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * TODO Put here a description of what this class does.
 *
 * @author Fengjiang.
 *         Created Nov 15, 2012.
 */
public class YelpScrapper {
	public static void main(String[] args) throws IOException {
		String url = "http://www.yelp.com/search?find_desc=&find_loc=15134&ns=1#find_loc=new+york&start=280";
		
		// Get DOM from specified URL
		Document doc = Jsoup.connect(url).get();
		String title = doc.title();
		System.out.println(title);
		System.out.println(doc.text());
		
		Element pageElement = doc.getElementById("paginationControls");
		System.out.println(pageElement.text());
	
		System.out.println(pageElement.select("a[href*=/biz/]").first());
		System.out.println(pageElement.select("a").first());
		System.out.println(pageElement.select("a").first().text());

		System.out.println(pageElement.select("span").first());
	}
}
