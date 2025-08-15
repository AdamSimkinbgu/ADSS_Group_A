-- Insert data into the ShippingAreas table
INSERT INTO ShippingAreas (areaNumber, areaName) VALUES
                        (0, 'Central District'),
                        (1, 'South District'),
                        (2, 'East District');

-- Insert data into the Sites table
INSERT INTO Sites (areaNum, addressStr, contName, contNumber) VALUES
                        (0, 'Tel Aviv', 'Yossi Oren', 0542315421),
                        (1, 'Ben Gurion Uni', 'Meni Adler', 0526451234),
                        (2, 'Afula', 'Dani Hendler', 0535471594);

-- Insert data into the Trucks table
INSERT INTO Trucks (truckNum, model, netWeight, maxCarryWeight, validLicense, inTransportID) VALUES
                        (401, 'Truck Model A', 1000, 20, 'A', -1),
                        (402, 'Truck Model B', 15000, 120, 'E', -1),
                        (403, 'Truck Model C', 12000, 100, 'C', -1);

-- Insert data into the Counters table
INSERT INTO Counters (CounterName, CounterValue) VALUES
                        (  'transportIDCounter', 0);


