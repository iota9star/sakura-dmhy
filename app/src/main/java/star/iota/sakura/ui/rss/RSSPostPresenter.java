package star.iota.sakura.ui.rss;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import star.iota.sakura.base.PVContract;
import star.iota.sakura.base.StringPresenter;


class RSSPostPresenter extends StringPresenter<List<RSSPostBean>> {

    RSSPostPresenter(PVContract.View view) {
        super(view);
    }

    @Override
    protected List<RSSPostBean> dealResponse(String s) {
        List<RSSPostBean> beans = new ArrayList<>();
        Elements elements = Jsoup.parseBodyFragment(s).select("item");
        for (Element ele : elements) {
            RSSPostBean bean = new RSSPostBean();
            String title = ele.select("title").text().replace("<![CDATA[", "").replace("]]>", "");
            bean.setTitle(title);
            String link = ele.select("guid").text();
            bean.setLink(link);
            String description = ele.select("description").text().replace("<![CDATA[", "").replace("]]>", "");
            bean.setDescription(description);
            Element img = Jsoup.parseBodyFragment(description).select("img").first();
            if (img != null) {
                String cover = img.attr("src");
                bean.setCover(cover);
            }
            String url = ele.select("enclosure").attr("url");
            bean.setUrl(url);
            String category = ele.select("category").text().replace("<![CDATA[", "").replace("]]>", "");
            bean.setCategory(category);
            String categoryUrl = ele.select("category").attr("domain");
            bean.setCategoryUrl(categoryUrl);
            ele.select("description").remove();
            ele.select("author").remove();
            ele.select("enclosure").remove();
            ele.select("guid").remove();
            ele.select("category").remove();
            String pubDate = ele.select("pubDate").text();
            bean.setPubDate(pubDate);
            beans.add(bean);
        }
        return beans;
    }
}
