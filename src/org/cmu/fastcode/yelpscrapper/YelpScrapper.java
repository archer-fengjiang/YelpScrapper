package org.cmu.fastcode.yelpscrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;

/**
 * This class is responsible for scraping review-rating pairs from www.yelp.com
 * and restore them to file in json format
 *
 * @author Fengjiang.
 *         Created Nov 15, 2012.
 */
public class YelpScrapper {
	private String entryURL;
	private List<String> bizLinkList;

	public YelpScrapper(String entryURL){
		this.entryURL = entryURL;
	}
	
	private List<String> getBizLinks(){
		List<String> bizLinks = new ArrayList<String>();
		String url = this.entryURL;
		Document dom;
		
		while(url != null){
			try {
				dom = Util.getDOM(url);
			} catch (IOException e) {
				System.out.println(e.getMessage());
				break;
			}
			bizLinks.addAll(BizListGetter.bizPageGetBizLinks(dom));
			url = Util.bizPageGetNextPage(dom);
		}
		return bizLinks;
	}
	
	
	public static void main(String[] args){
		YelpScrapper scrapper = new YelpScrapper("http://www.yelp.com/search?find_desc=restaurants&find_loc=New+York%2C+NY&ns=1");
		List<String> bizLinkList = scrapper.getBizLinks();
		for(String str : bizLinkList){
			System.out.println(str);
		}
		
	}
}