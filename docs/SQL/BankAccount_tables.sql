PRAGMA foreign_keys = ON;

-- Bank Accounts for Employees
CREATE TABLE IF NOT EXISTS BankAccounts (
    employeeId          INTEGER PRIMARY KEY,
    bankNumber          INTEGER NOT NULL,
    bankBranchNumber    INTEGER NOT NULL,
    bankAccountNumber   INTEGER NOT NULL,
    FOREIGN KEY (employeeId) REFERENCES Employees(israeliId)
);