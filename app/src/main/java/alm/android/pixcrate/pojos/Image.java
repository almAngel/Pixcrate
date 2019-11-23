package alm.android.pixcrate.pojos;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public class Image implements Serializable {

    @SerializedName("_id")
    private String id;
    @SerializedName("url")
    private String url;
    @SerializedName("description")
    private String description;
    @SerializedName("visibility")
    private String visibility;

    public Image(@Nullable String id, @Nullable String url, String description, String visibility) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.visibility = visibility;
    }

    public Image(String description, String visibility) {
        this.description = description;
        this.visibility = visibility;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", visibility='" + visibility + '\'' +
                '}';
    }
}
