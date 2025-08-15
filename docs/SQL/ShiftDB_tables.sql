CREATE TABLE IF NOT EXISTS Shifts (
                                      id INTEGER PRIMARY KEY,
                                      shiftType TEXT NOT NULL,
                                      shiftDate DATE NOT NULL,
                                      isAssignedShiftManager BOOLEAN NOT NULL,
                                      isOpen BOOLEAN NOT NULL,
                                      startHour TIME NOT NULL,
                                      endHour TIME NOT NULL,
                                      creationDate DATE NOT NULL,
                                      updateDate DATE NOT NULL,
                                      branchId INTEGER NOT NULL,
                                      FOREIGN KEY (branchId) REFERENCES Branches(branchId)
);

CREATE TABLE IF NOT EXISTS RoleRequired (
                                            shiftId INTEGER NOT NULL,
                                            roleName TEXT NOT NULL,
                                            requiredCount INTEGER NOT NULL,
                                            PRIMARY KEY (shiftId, roleName),
                                            FOREIGN KEY (shiftId) REFERENCES Shifts(id),
                                            FOREIGN KEY (roleName) REFERENCES Roles(roleName)
);

CREATE TABLE IF NOT EXISTS AssignedEmployees (
                                                 shiftId INTEGER NOT NULL,
                                                 roleName TEXT NOT NULL,
                                                 employeeId INTEGER NOT NULL,
                                                 PRIMARY KEY (shiftId, roleName, employeeId),
                                                 FOREIGN KEY (shiftId) REFERENCES Shifts(id),
                                                 FOREIGN KEY (roleName) REFERENCES Roles(roleName),
                                                 FOREIGN KEY (employeeId) REFERENCES Employees(israeliId)
);

CREATE TABLE IF NOT EXISTS AvailableEmployees (
                                                  shiftId INTEGER NOT NULL,
                                                  employeeId INTEGER NOT NULL,
                                                  PRIMARY KEY (shiftId, employeeId),
                                                  FOREIGN KEY (shiftId) REFERENCES Shifts(id),
                                                  FOREIGN KEY (employeeId) REFERENCES Employees(israeliId)
);
