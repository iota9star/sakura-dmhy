package star.iota.sakura.ui.rss;


class RSSPostBean {
    private int id;
    private String title;
    private String link;
    private String pubDate;
    private String cover;
    private String description;
    private String url;
    private String category;
    private String categoryUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    String getPubDate() {
        return pubDate;
    }

    void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    String getCover() {
        return cover;
    }

    void setCover(String cover) {
        this.cover = cover;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }
}
