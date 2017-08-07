package star.iota.sakura.ui.fans.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FanBean implements Parcelable {
    public static final Creator<FanBean> CREATOR = new Creator<FanBean>() {
        @Override
        public FanBean createFromParcel(Parcel in) {
            return new FanBean(in);
        }

        @Override
        public FanBean[] newArray(int size) {
            return new FanBean[size];
        }
    };
    private int id;
    @SerializedName("img")
    private String cover;
    @SerializedName("name")
    private String name;
    @SerializedName("kw")
    private String keyword;
    @SerializedName("hp")
    private String official;
    @SerializedName("sub")
    private List<SubBean> subs;

    public FanBean() {
    }

    protected FanBean(Parcel in) {
        id = in.readInt();
        cover = in.readString();
        name = in.readString();
        keyword = in.readString();
        official = in.readString();
        subs = in.createTypedArrayList(SubBean.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(cover);
        dest.writeString(name);
        dest.writeString(keyword);
        dest.writeString(official);
        dest.writeTypedList(subs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getOfficial() {
        return official;
    }

    public void setOfficial(String official) {
        this.official = official;
    }

    public List<SubBean> getSubs() {
        return subs;
    }

    public void setSubs(List<SubBean> subs) {
        this.subs = subs;
    }
}
