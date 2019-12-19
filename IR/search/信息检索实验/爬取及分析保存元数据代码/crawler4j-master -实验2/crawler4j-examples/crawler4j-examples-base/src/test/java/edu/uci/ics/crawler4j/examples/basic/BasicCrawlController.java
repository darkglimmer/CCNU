package edu.uci.ics.crawler4j.examples.basic;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.examples.imagecrawler.ImageCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

//在URL中提取元数据并存入文件
class JsoupPrintMetadata {
    void print(String url, String count) throws IOException{
        //String url = page.getWebURL().getURL();
        Document doc = Jsoup.connect(url).get();
        String title = doc.select("title").text(); //标题
        String text = doc.select(".news_txt").text();//正文
        String time_source = doc.select(".news_about").text();//时间、来源
        //System.out.println("Title : " + title);
        //为网页新建txt文档
        String path = "D:\\tmp\\crawler4j\\fencitest\\news";
        path = path.concat(count); //count：以网页顺序编号命名
        path = path.concat(".txt");
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        //保存获取的内容
        Writer out =new FileWriter(file,true);
        out.write(title); //标题
        out.write("\r\n");
        out.write(time_source); //时间、来源
        out.write("\r\n");
        out.write(text); //正文
        out.close();
    }
}
public class BasicCrawlController {

    public static long startTime=System.currentTimeMillis();
    public static void main(String[] args) throws Exception {
        CrawlConfig config = new CrawlConfig();

        /*File file = new File("D:\\tmp\\crawler4j1.txt");
        if (!file.exists()) {
            file.mkdirs();
        }*/

        // Set the folder where intermediate crawl data is stored (e.g. list of urls that are extracted from previously
        // fetched pages and need to be crawled later).
        config.setCrawlStorageFolder("/tmp/crawler4j/");

        //确保我们每秒发送的请求不超过1个（请求之间的间隔为1000毫秒）
        //否则可能会导致目标服务器过载
        config.setPolitenessDelay(1000);

        //最大的爬行深度。对于无限深度，默认值为-1
        config.setMaxDepthOfCrawling(-1);

        //要爬行的最大页数。对于不受限制的页数，默认值为-1
        config.setMaxPagesToFetch(100000);

        //二进制数据是否爬取 如：pdf的内容，图像的元数据等
        config.setIncludeBinaryContentInCrawling(false);

        // Do you need to set a proxy? If so, you can use:
        // config.setProxyHost("proxyserver.example.com");
        // config.setProxyPort(8080);

        // If your proxy also needs authentication:
        // config.setProxyUsername(username); config.getProxyPassword(password);

        // This config parameter can be used to set your crawl to be resumable
        // (meaning that you can resume the crawl from a previously
        // interrupted/crashed crawl). Note: if you enable resuming feature and
        // want to start a fresh crawl, you need to delete the contents of
        // rootFolder manually.
        //此配置参数可用于将爬网设置为可恢复，这意味着会从以前的中断/崩溃位置开始爬网
        //注意：如果启用恢复功能并要开始新的爬网，需要手动删除根文件夹的内容。
        config.setResumableCrawling(false);

        // Set this to true if you want crawling to stop whenever an unexpected error
        // occurs. You'll probably want this set to true when you first start testing
        // your crawler, and then set to false once you're ready to let the crawler run
        // for a long time.
        config.setHaltOnError(true);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        controller.addSeed("https://www.thepaper.cn/");
        controller.addSeed("https://www.thepaper.cn/channel_25950");
        controller.addSeed("https://www.thepaper.cn/channel_25951");
/*      controller.addSeed("https://news.163.com/");
        controller.addSeed("https://news.163.com/~lopes/");
        controller.addSeed("https://news.163.com/~welling/");

        controller.addSeed("https://news.163.com/domestic/");
        controller.addSeed("https://news.163.com/domestic/~lopes/");
        controller.addSeed("https://news.163.com/domestic/~welling/");

        controller.addSeed("https://news.163.com/world/");
        controller.addSeed("https://news.163.com/world/~lopes/");
        controller.addSeed("https://news.163.com/world/~welling/");

        controller.addSeed("https://war.163.com/");
        controller.addSeed("https://war.163.com/~lopes/");
        controller.addSeed("https://war.163.com/~welling/");

        controller.addSeed("https://news.163.com/air/");
        controller.addSeed("https://news.163.com/air/~lopes/");
        controller.addSeed("https://news.163.com/air/~welling/");
*/

        /*
        JsoupPrintMetadata jpm = new JsoupPrintMetadata();
        jpm.print();*/
        // Number of threads to use during crawling. Increasing this typically makes crawling faster. But crawling
        // speed depends on many other factors as well. You can experiment with this to figure out what number of
        // threads works best for you.
        int numberOfCrawlers = 8;

        // To demonstrate an example of how you can pass objects to crawlers, we use an AtomicInteger that crawlers
        // increment whenever they see a url which points to an image.
        AtomicInteger numSeenImages = new AtomicInteger();

        // The factory which creates instances of crawlers.
        CrawlController.WebCrawlerFactory<BasicCrawler> factory = () -> new BasicCrawler(numSeenImages);

        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.

        controller.start(factory, numberOfCrawlers);
    }

}