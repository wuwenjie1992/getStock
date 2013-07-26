import java.util.ArrayList;
/**
 * 
 * @author wuwenjie
 *	@date 20130722
 */
public class StockDaly {

	public String StockName; // 股票名字
	public String Ticker; // 股票代码
	public String OpeningQuotation; // 今日开盘价
	public String YesterdayClosingQuotation; // 昨日收盘价
	public String latestPrice; // 当前价格
	public String CeilingPrice; // 最高价
	public String BottomPrice; // 今日最低价
	public String PriceOfbuy1; // 买一价
	public String PriceOfsell1; // 卖一价
	public String Volume; // 成交的股票数
	public String Turnover; // 成交金额

	public ArrayList<String> buyN_a = new ArrayList<String>(10); // 委买手价
	public ArrayList<String> sellN_a = new ArrayList<String>(10); // 委卖手价

	public String date; // 日期
	public String time; // 时间

	// buyN
	public String getbuyN(int index) {
		return buyN_a.get(index);
	}

	public void setbuyN(String price, int index) {
		this.buyN_a.add(index, price);
	}

	// sellN
	public String getsellN(int index) {
		return sellN_a.get(index);
	}

	public void setsellN(String price, int index) {
		this.sellN_a.add(index, price);
	}

}// StockDaly
