package au.id.bennettscash.photogallery;

/**
 * Created by chris on 26/08/15.
 */
public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    private String mOwner;

    public String toString() {
        return mCaption;
    }

    public String getCaption() { return mCaption; }
    public void setCaption(String caption) { this.mCaption = caption; }

    public String getId() { return mId; }
    public void setId(String id) { this.mId = id; }

    public String getUrl() { return mUrl; }
    public void setUrl(String url) { this.mUrl = url; }

    public String getOwner() { return mOwner; }
    public void setOwner(String mOwner) { this.mOwner = mOwner; }

    public GalleryItem(String id, String caption, String url, String owner) {
        this.mId = id;
        this.mCaption = caption;
        this.mUrl = url;
        this.mOwner = owner;
    }

    public String getPhotoPageURL() {
        return "https://www.flickr.com/photos/" + mOwner + "/" + mId;
    }
}
