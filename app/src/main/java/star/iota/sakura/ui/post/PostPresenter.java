package star.iota.sakura.ui.post;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import star.iota.sakura.Url;
import star.iota.sakura.base.PVContract;
import star.iota.sakura.base.StringPresenter;


public class PostPresenter extends StringPresenter<List<PostBean>> {

    PostPresenter(PVContract.View view) {
        super(view);
    }

    @Override
    protected List<PostBean> dealResponse(String s) {
        List<PostBean> beans = new ArrayList<>();
        Elements elements = Jsoup.parse(s).select("#topic_list > tbody > tr");
        for (Element ele : elements) {
            PostBean bean = new PostBean();
            ele.select("td:nth-child(1) > span").remove();
            bean.setDate(ele.select("td:nth-child(1)").text());
            bean.setCategory(ele.select("td:nth-child(2)").text());
            bean.setMagnet(ele.select("td:nth-child(4) > a").attr("href"));
            bean.setTitle(ele.select("td.title > a").text());
            Element tag = ele.select("td.title > span.tag > a").first();
            if (tag != null) {
                String url = Url.BASE + tag.attr("href");
                String text = tag.text();
                PostBean.SubBean subBean = new PostBean.SubBean();
                subBean.setUrl(url);
                subBean.setName(text);
                bean.setSub(subBean);
            }
            bean.setSize(ele.select("td:nth-child(5)").text());
            bean.setUrl(Url.BASE + ele.select("td.title > a").attr("href"));
            beans.add(bean);
        }
        return beans;
    }
}
