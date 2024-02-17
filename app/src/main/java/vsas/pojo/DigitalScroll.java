package vsas.pojo;

import java.sql.Timestamp;

public class DigitalScroll implements Pojo{
    private Integer scroll_id;
    private String scroll_name;
    private String scroll_description;
    private Integer uploader_id;
    private String uploader_name;
    private String file_name;
    private Timestamp create_time;
    private Timestamp update_time;
    private Integer upload_times;
    private Integer download_times;
    private long file_size;
    private byte[] binary_file;

    public byte[] getBinary_file() {
        return binary_file;
    }

    public void setBinary_file(byte[] binary_file) {
        this.binary_file = binary_file;
    }

    public Integer getScroll_id() {
        return scroll_id;
    }

    public void setScroll_id(Integer scroll_id) {
        this.scroll_id = scroll_id;
    }

    public String getUploader_name() {
        return uploader_name;
    }

    public void setUploader_name(String uploader_name) {
        this.uploader_name = uploader_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getScroll_name() {
        return scroll_name;
    }

    public void setScroll_name(String scroll_name) {
        this.scroll_name = scroll_name;
    }

    public String getScroll_description() {
        return scroll_description;
    }

    public void setScroll_description(String scroll_description) {
        this.scroll_description = scroll_description;
    }

    public int getUploader_id() {
        return uploader_id;
    }

    public void setUploader_id(int uploader_id) {
        this.uploader_id = uploader_id;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }
    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public int getUpload_times() {
        return upload_times;
    }

    public void setUpload_times(int upload_times) {
        this.upload_times = upload_times;
    }

    public int getDownload_times() {
        return download_times;
    }

    public void setDownload_times(int download_times) {
        this.download_times = download_times;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(int file_size) {
        this.file_size = file_size;
    }

    @Override
    public String toString() {
        return "DigitalScroll{" +
                "scroll_id=" + scroll_id +
                ", \nscroll_name='" + scroll_name + '\'' +
                ", \nscroll_description='" + scroll_description + '\'' +
                ", \nuploader_id=" + uploader_id +
                ", \ncreate_time=" + create_time +
                ", \nupdate_time=" + update_time +
                ", \nupload_times=" + upload_times +
                ", \ndownload_times=" + download_times +
                ", \nfile_size=" + file_size +
                '}';
    }
}
