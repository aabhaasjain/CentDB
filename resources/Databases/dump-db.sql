-- Host: localhost    Database: db
-----------------------------------------------------------
-- Table structure for table person
--
DROP TABLE IF EXISTS person;
CREATE TABLE person (
PersonID int primary key,
LastName text,
FirstName text,
Age int);
--
-- Dumping data for table person
--
LOCK TABLES person WRITE;
INSERT INTO person VALUES (PersonID,LastName,FirstName)
VALUES (1,Hansen,Ola,30),(2,Svendson,Tove,23),(3,Pettersen,Kari,20);
UNLOCK TABLES;--
-- Table structure for table orders
--
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
OrderID int primary key,
OrderNumber int,
PersonID int foreign key);
--
-- Dumping data for table orders
--
LOCK TABLES orders WRITE;
INSERT INTO orders VALUES (OrderID,OrderNumber,PersonID)
VALUES (1,77895,3),(2,44678,3),(3,22456,2),(4,24562,1);
UNLOCK TABLES;