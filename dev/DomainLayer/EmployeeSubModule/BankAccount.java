package DomainLayer.EmployeeSubModule;

public class BankAccount {
    private long employeeId;
    private long bankNumber;
    private long bankBranchNumber;
    private long bankAccountNumber;


    public BankAccount(long employeeId, long bankNumber, long bankBranchNumber, long bankAccountNumber) {
        this.employeeId = employeeId;
        this.bankNumber = bankNumber;
        this.bankBranchNumber = bankBranchNumber;
        this.bankAccountNumber = bankAccountNumber;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public long getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(long bankNumber) {
        this.bankNumber = bankNumber;
    }

    public long getBankBranchNumber() {
        return bankBranchNumber;
    }

    public void setBankBranchNumber(long bankBranchNumber) {
        this.bankBranchNumber = bankBranchNumber;
    }

    public long getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(long bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "employeeId=" + employeeId +
                ", bankNumber=" + bankNumber +
                ", bankBranchNumber=" + bankBranchNumber +
                ", bankAccountNumber=" + bankAccountNumber +
                '}';
    }
}
