package star.iota.sakura.ui.index;


import com.google.gson.annotations.SerializedName;

import java.util.List;

class IndexBean {
    @SerializedName("years")
    private List<YearBean> years;

    List<YearBean> getYears() {
        return years;
    }

    static class YearBean {
        @SerializedName("n")
        private int year;
        @SerializedName("seasons")
        private List<SeasonBean> seasons;

        int getYear() {
            return year;
        }

        List<SeasonBean> getSeasons() {
            return seasons;
        }

        static class SeasonBean {
            @SerializedName("text")
            private String text;
            @SerializedName("index")
            private String index;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getIndex() {
                return index;
            }

            public void setIndex(String index) {
                this.index = index;
            }
        }
    }
}
