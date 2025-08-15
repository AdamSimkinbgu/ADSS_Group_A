package DomainLayer.EmployeeSubModule;

public class Branch {
    private long branchId;
    private String branchName;
    private int areaCode;
    private String branchAddress;
    private String ManagerID;

    public Branch(long branchID, String branchName,int areaCode, String branchAddress, String ManagerID) {
        this.branchId = branchID;
        this.branchName = branchName;
        this.areaCode = areaCode;
        this.branchAddress = branchAddress;
        this.ManagerID = ManagerID;
    }

    public long getBranchId() {
        return branchId;
    }

    public int getAreaCode() {
        return areaCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getManagerID() {
        return ManagerID;
    }

    public void setManagerID(String ManagerID) {
        this.ManagerID = ManagerID;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "branchName='" + branchName + '\'' +
                ", branchAddress='" + branchAddress + '\'' +
                ", ManagerID='" + ManagerID + '\'' +
                '}';
    }
}
