package org.cmu.fastcode.yelpscrapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;

public class BizLinkScrapper {
	private String entryURL;
	private String query;
	private BufferedWriter fileWriter;
	private List<String> bizLinkList;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public BizLinkScrapper(String entryURL, String query){
		this.entryURL = entryURL;
		this.query = query;
	}
	
	private List<String> getBizLinks(){
		try {
			this.fileWriter = new BufferedWriter(new FileWriter(this.query + "[" + dateFormat.format(new Date()) + "]"));
		} catch (IOException e1) {
			
		}
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
		BizLinkScrapper scrapper = new BizLinkScrapper("http://www.yelp.com/search?find_desc=restaurants&find_loc=New+York%2C+NY&ns=1"
				, "restaurant_ny");
		List<String> bizLinkList = scrapper.getBizLinks();
		for(String str : bizLinkList){
			System.out.println(str);
		}
		
	}
}
