package easy.life.sharuash;


public class DataClass {

    private String dataTitle;
    private String dataDesc;
    private String dataLang;
    private String dataImage;
    private String key;
    private Boolean collar;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataLang() {
        return dataLang;
    }

    public String getDataImage() {
        return dataImage;
    }
    public Boolean getCollar() {return collar;}

    public DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage, Boolean collar) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
        this.collar = collar;
    }
    public DataClass(){

    }
}
