package org.cmu.fastcode.yelpscrapper;

import java.io.IOException;

/**
 * TODO Put here a description of what this class does.
 *
 * @author Fengjiang.
 *         Created Nov 16, 2012.
 */
public class StressTest extends Thread{
	public static String url;
	
	public static int threadsNum;
	
	public static int totalHttpRequestNum = 0;
	
	public static int objectRequestNum;
	
	private int threadID;
	
	public StressTest(int id){
		this.threadID = id;
	}
	
	public static synchronized int incrementReuqestNum(){
		return ++StressTest.totalHttpRequestNum;
	}
	
	
	public void run(){
		while(StressTest.totalHttpRequestNum < StressTest.objectRequestNum){
			String str;
			try {
				str = BizListGetter.getNextPageBizList(StressTest.url);
			} catch (IOException e) {
				System.out.println(e.getMessage());
				continue;
			}
			int num = StressTest.incrementReuqestNum();
			System.out.println(num + " " + this.threadID + " " + str);
		}
	}
	
	public static void main(String[] args){
		StressTest.url = "http://www.yelp.com/nyc";
		StressTest.threadsNum = 1;
		StressTest.objectRequestNum = 200;
		for(int i = 0; i < StressTest.threadsNum; i++){
			StressTest test = new StressTest(i);
			test.start();
		}
	}
}
