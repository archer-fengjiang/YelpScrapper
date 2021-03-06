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
	
	
	/**
	 * This method will write all biz links associate with one query
	 * @throws IOException if file IO has problem
	 * */
	private void writeBizLinks() throws IOException{
		this.fileWriter = new BufferedWriter(new FileWriter(this.query + Util.getCurrentDateString()));
		String url = this.entryURL;
		Document dom;
		while(url != null){
			// get DOM of next page
			try {
				dom = Util.getDOM(url);
			} catch (IOException e) {
				// IOException from Fetching URL will stop the process
				e.printStackTrace();
				this.fileWriter.write("error");
				this.fileWriter.close();
				break;
			}
			// parse and write all biz links on this page
			List<String> bizLinkList = Util.bizPageGetBizLinks(dom);
			for(String bizLink : bizLinkList){
				this.fileWriter.write(bizLink + "\n");
			}
			// parse URL of next page from DOM, if get null while loop stops
			url = Util.bizPageGetNextPage(dom);
		}
		// complete fetching, flush writer and close if
		this.fileWriter.close();
	}
	
	
	public static void main(String[] args){
		BizLinkScrapper scrapper = new BizLinkScrapper("http://www.yelp.com/search?find_desc=restaurants&find_loc=San+Francisco%2C+CA&ns=1"
				, "restaurant_sf");
		try {
			scrapper.writeBizLinks();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
