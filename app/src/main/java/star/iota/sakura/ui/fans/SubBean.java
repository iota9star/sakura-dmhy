package star.iota.sakura.ui.fans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class SubBean implements Parcelable {
    public static final Creator<SubBean> CREATOR = new Creator<SubBean>() {
        @Override
        public SubBean createFromParcel(Parcel in) {
            return new SubBean(in);
        }

        @Override
        public SubBean[] newArray(int size) {
            return new SubBean[size];
        }
    };
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String url;

    public SubBean() {
    }

    private SubBean(Parcel in) {
        name = in.readString();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
