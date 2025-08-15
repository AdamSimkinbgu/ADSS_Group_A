package DTOs.TransportModuleDTOs;

public class SiteDTO {
    private int siteAreaNum;
    private String siteAddressString;
    private String cName;
    private long cNumber;

    public SiteDTO() {}
    public SiteDTO(int siteAreaNum, String addressString) {
        this.siteAreaNum = siteAreaNum;
        this.siteAddressString = addressString;
        this.cName = "";   //  if this is created for a transport probably, because a transport will only care about site area number and site address string.
        this.cNumber = 0;   //  if this is created for a transport probably, because a transport will only care about site area number and site address string.
    }

    public SiteDTO(int siteAreaNum, String addressString, String cName, long cNumber) {
        this.siteAreaNum = siteAreaNum;
        this.siteAddressString = addressString;
        this.cName = cName;
        this.cNumber = cNumber;
    }

    public int getSiteAreaNum() {return siteAreaNum;}
    public void setSiteAreaNum(int siteAreaNum) {this.siteAreaNum = siteAreaNum;}
    public String getSiteAddressString() {return siteAddressString;}
    public void setSiteAddressString(String siteAddressString) {this.siteAddressString = siteAddressString;}
    public String getcName() {return cName;}
    public void setcName(String cName) {this.cName = cName;}
    public long getcNumber() {return cNumber;}
    public void setcNumber(long cNumber) {this.cNumber = cNumber;}

    @Override
    public String toString() {
        String res = "Site Area: " + this.siteAreaNum + ", Site String Address: " + this.siteAddressString;
        return res;
    }

}
