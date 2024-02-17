package vsas.pojo;

import java.sql.Timestamp;

public class UserDownload implements Pojo {
    private Integer download_id;
    private Integer downloader_id;
    private Integer scroll_id;
    private Timestamp download_time;
    private String scroll_name;
    private String file_name;
    private long file_size;
    private Integer uploader_id;
    private String uploader_name;

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Integer getDownloadId() {
        return download_id;
    }

    public void setDownloadId(Integer download_id) {
        this.download_id = download_id;
    }

    public Integer getDownloaderId() {
        return downloader_id;
    }

    public Integer getScroll_id() {
        return scroll_id;
    }

    public Timestamp getDownload_time() {
        return download_time;
    }

    public void setDownloaderId(Integer downloader_id) {
        this.downloader_id = downloader_id;
    }

    public Integer getScrollId() {
        return this.scroll_id;
    }

    public void setScrollId(Integer scroll_id) {
        this.scroll_id = scroll_id;
    }

    public Timestamp getDownloadTime() {
        return download_time;
    }

    public void setDownloadTime(Timestamp download_time) {
        this.download_time = download_time;
    }

    public String getScroll_name() {
        return scroll_name;
    }

    public void setScroll_name(String scroll_name) {
        this.scroll_name = scroll_name;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public Integer getUploader_id() {
        return uploader_id;
    }

    public void setUploader_id(Integer uploader_id) {
        this.uploader_id = uploader_id;
    }

    public String getUploader_name() {
        return uploader_name;
    }

    public void setUploader_name(String uploader_name) {
        this.uploader_name = uploader_name;
    }

    @Override
    public String toString() {
        return "UserDownload{" +
                "download_id=" + download_id +
                ", downloader_id=" + downloader_id +
                ", scroll_id=" + scroll_id +
                ", download_time=" + download_time +
                ", scroll_name='" + scroll_name + '\'' +
                ", file_size=" + file_size +
                ", uploader_id=" + uploader_id +
                ", uploader_name='" + uploader_name + '\'' +
                '}';
    }
}
