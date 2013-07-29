import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * 
 * @author wuwenjie
 * @date 20130722
 */
// 错误: 非法字符: \65279 Unicode 标记BOM
// windows set path=C:\Program Files\Java\jdk1.7.0_17\bin;%path%

public class getStock {

	public static String ShareNum = "sh000001";
	public static final String POST_URL = "http://wap.eastmoney.com/FuturesInfo.aspx";
	public static String song = "";
	public static StringBuffer response_get = new StringBuffer();

	public static void readContentFromGet(Boolean t) throws IOException {

		String StockURL_s = "http://hq.sinajs.cn/list=" + ShareNum;// GBK;
		// URLEncoder.encode("","utf-8");
		URL StockURL = new URL(StockURL_s);

		// 打开连接，URL.openConnection函数会根据URL的类型，
		// 返回不同的URLConnection子类的对象，URL是http,返回HttpURLConnection
		HttpURLConnection connection = (HttpURLConnection) StockURL
				.openConnection();
		// 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到服务器

		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.connect();
		// 取得输入流，并使用Reader读取
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "GBK"));
		// 设置编码,否则中文乱码

		String lines;

		while ((lines = reader.readLine()) != null) {
			// lines = new String(lines.getBytes(), "utf-8");
			// System.out.println(lines);
			response_get.append(lines);
		}
		reader.close();
		// 断开连接
		connection.disconnect();

		// System.out.println(response_get.toString());

		if (t) {
			playSound mp3Sound = new playSound(song);
			mp3Sound.start();
		}

	}

	public static void readContentFromPost() throws IOException {
		// Post请求的url，与get不同的是不需要带参数
		URL postUrl = new URL(POST_URL);
		// 打开连接
		HttpURLConnection connection = (HttpURLConnection) postUrl
				.openConnection();

		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setDoOutput(true);
		connection.setDoInput(true); // Read from the connection. Default is
										// true.

		// httpUrlConnection.setDoOutput(true);以后就可以使用conn.getOutputStream().write()
		// httpUrlConnection.setDoInput(true);以后就可以使用conn.getInputStream().read();
		// post请求（比如：文件上传）需要往服务区传输大量的数据，
		// 这些数据是放在http的body里面的，因此需要在建立连接以后，往服务端写数据。

		// Set the post method. Default is GET
		connection.setRequestMethod("POST");

		// Post cannot use caches
		// Post 请求不能使用缓存
		connection.setUseCaches(false);

		// setFollowRedirects 设置所有的http连接是否自动处理重定向
		// setInstanceFollowRedirects 设置本次连接是否自动处理重定向
		connection.setInstanceFollowRedirects(true);

		// Set the content type to urlencoded,because we will write some
		// URL-encoded content to the
		// connection. Settings above must be set before connect

		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// application/x-www-form-urlencoded（使用HTTP的POST方法提交的表单）
		// 正文是urlencoded编码过的form参数

		connection.connect();
		DataOutputStream out = new DataOutputStream(
				connection.getOutputStream());
		// The URL-encoded contend
		// 正文，正文内容其实跟get的URL中'?'后的参数字符串一致
		String content = "c=CONC";

		// writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
		out.writeBytes(content);
		out.flush();
		out.close(); // flush and close

		int responseCode = connection.getResponseCode();
		System.out.println("Response Code : " + responseCode);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
		String line = "";

		StringBuffer response = new StringBuffer();

		while ((line = reader.readLine()) != null) {
			// line = new String(line.getBytes(), "utf-8");
			// System.out.println(line);
			response.append(line + "\n");
		}
		reader.close();
		connection.disconnect();

		System.out.println(response.toString());

	}

	//分析获得的股票信息
	public static void execGetS(Boolean t, String song) throws IOException {

		getStock.song = song;
		getStock.readContentFromGet(t);
		// gs.readContentFromPost();
		String share_info = getStock.response_get.toString();

		StockDaly share = new StockDaly();

		int CommaIndex = share_info.indexOf(","); // 第一个逗号位置

		if (CommaIndex < 0) {
			System.out.println("\n股票代码有误！！");
			System.exit(1);
		}

		share.StockName = share_info.substring(share_info.indexOf("\"") + 1,
				CommaIndex);// 股票名称
		share.Ticker = share_info.substring(share_info.indexOf("str") + 4,
				share_info.indexOf("="));

		ArrayList<String> shareOut_a = new ArrayList<String>(31);

		for (int i = 0; i < 31; i++) {
			CommaIndex = share_info.indexOf(",", CommaIndex) + 1; // 逗号位置

			shareOut_a.add(share_info.substring(CommaIndex,
					share_info.indexOf(",", CommaIndex))); // 下一个逗号位置
		}

		share.OpeningQuotation = shareOut_a.get(0);// 开盘价
		share.YesterdayClosing = shareOut_a.get(1);// 昨收价
		share.latestPrice = shareOut_a.get(2); // 当前价
		share.CeilingPrice = shareOut_a.get(3); // 最高价
		share.BottomPrice = shareOut_a.get(4); // 最低价
		share.PriceOfbuy1 = shareOut_a.get(5); // 买一价
		share.PriceOfsell1 = shareOut_a.get(6); // 卖一价
		share.Volume = shareOut_a.get(7); // 成交的股票数
		share.Turnover = shareOut_a.get(8);// 成交金额

		// share.setbuyN(shareOut_a.get(9), 0);//买一手
		// share.setbuyN(shareOut_a.get(10), 1);//买一价

		for (int i = 0; i < 10; i++) {
			share.setbuyN(shareOut_a.get(9 + i), i);// 买手价
			share.setsellN(shareOut_a.get(19 + i), i);// 卖手价
		}

		share.date = shareOut_a.get(29);
		share.time = shareOut_a.get(30);

		float volumeOfRiseDrop = Float.parseFloat(share.latestPrice)
				- Float.parseFloat(share.YesterdayClosing); // 涨跌额

		float RateOfChange = volumeOfRiseDrop
				/ Float.parseFloat(share.YesterdayClosing);

		System.out.println("股票名称:" + share.StockName + "\n股票代码:" + share.Ticker
				+ "\n\n当前价:" + share.latestPrice + "\n涨跌额:" + volumeOfRiseDrop
				+ "\n涨跌幅:" + RateOfChange * 100 + "%" + "\n\n开盘价:"
				+ share.OpeningQuotation + "\n昨收价:" + share.YesterdayClosing
				+ "\n最高价:" + share.CeilingPrice + "\n最低价:" + share.BottomPrice
				+ "\n买一价:" + share.PriceOfbuy1 + "\n卖一价:" + share.PriceOfsell1
				+ "\n成交量:" + Integer.parseInt(share.Volume) / 100 + "手"
				+ "\n成交额:" + Long.parseLong(share.Turnover) / 10000 + "万元"
				+ "\n买一手:" + Integer.parseInt(share.getbuyN(0)) / 100 + "手"
				+ "	买一价:" + share.getbuyN(1) + "\n买二手:"
				+ Integer.parseInt(share.getbuyN(2)) / 100 + "手" + "	买二价:"
				+ share.getbuyN(3) + "\n买三手:"
				+ Integer.parseInt(share.getbuyN(4)) / 100 + "手" + "	买三价:"
				+ share.getbuyN(5) + "\n买四手:"
				+ Integer.parseInt(share.getbuyN(6)) / 100 + "手" + "	买四价:"
				+ share.getbuyN(7) + "\n买五手:"
				+ Integer.parseInt(share.getbuyN(8)) / 100 + "手" + "	买五价:"
				+ share.getbuyN(9) + "\n\n卖一手:"
				+ Integer.parseInt(share.getsellN(0)) / 100 + "手" + "	卖一价:"
				+ share.getsellN(1) + "\n卖二手:"
				+ Integer.parseInt(share.getsellN(2)) / 100 + "手" + "	卖二价:"
				+ share.getsellN(3) + "\n卖三手:"
				+ Integer.parseInt(share.getsellN(4)) / 100 + "手" + "	卖三价:"
				+ share.getsellN(5) + "\n卖四手:"
				+ Integer.parseInt(share.getsellN(6)) / 100 + "手" + "	卖四价:"
				+ share.getsellN(7) + "\n卖五手:"
				+ Integer.parseInt(share.getsellN(8)) / 100 + "手" + "	卖五价:"
				+ share.getsellN(9) + "\n\n交易日期:" + share.date + "\n交易时间:"
				+ share.time

		);

	}

	public static void main(String[] args) throws IOException {

		// for (int i = 0; i < args.length; i++) {
		// System.out.println(args.length + " args[" + i + "]="
		// + args[i].toString());
		// }

		Boolean b = false;
		String song = null;

		if (args.length != 0) { // 如果有参数

			for (int i = 0; i < args.length; i++) {

				if (args[i].equals("-Share")) { // 看哪只股票

					if (args.length >= i + 1) {

						getStock.ShareNum = args[i + 1].toString();

					} else {
						System.out.println("-Share ShareNum");
					}

				} else if (args[i].equals("-sound")) { // 提示音

					if (args.length >= i + 1) {

						b = true;
						song = args[i + 1];

					} else {
						System.out.println("-sound song");
					}

				} else if (args[i].equals("-help")) {
					System.out
							.println("\ngetStock v0.0.1 20130722-20130726\n"
									+ "Author Improved wuwenjie\n"
									+ "ThankFor Eclipse;Java;Xubuntu;GUN/Linux\n"
									+ "UseAge:\n"
									+ "-Share sh000001 股票代码\n-sound a.wav 音频文件\n-help 帮助\n");
				}

			}// for

			execGetS(b, song); // 执行

		} else { // 如果没有参数

			execGetS(false, "");
		}

	}// main

}
