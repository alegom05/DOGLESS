-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema northwind
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema northwind
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `northwind` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `northwind` ;

-- -----------------------------------------------------
-- Table `northwind`.`Categories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`Categories` (
  `CategoryID` INT NOT NULL AUTO_INCREMENT,
  `CategoryName` VARCHAR(15) NOT NULL,
  `Description` MEDIUMTEXT NULL DEFAULT NULL,
  `Picture` LONGBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`CategoryID`),
  INDEX `CategoryName` (`CategoryName` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`CustomerCustomerDemo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`CustomerCustomerDemo` (
  `CustomerID` VARCHAR(5) NOT NULL,
  `CustomerTypeID` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`CustomerID`, `CustomerTypeID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`CustomerDemographics`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`CustomerDemographics` (
  `CustomerTypeID` VARCHAR(10) NOT NULL,
  `CustomerDesc` MEDIUMTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`CustomerTypeID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`Customers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`Customers` (
  `CustomerID` VARCHAR(5) NOT NULL,
  `CompanyName` VARCHAR(40) NOT NULL,
  `ContactName` VARCHAR(30) NULL DEFAULT NULL,
  `ContactTitle` VARCHAR(30) NULL DEFAULT NULL,
  `Address` VARCHAR(60) NULL DEFAULT NULL,
  `City` VARCHAR(15) NULL DEFAULT NULL,
  `Region` VARCHAR(15) NULL DEFAULT NULL,
  `PostalCode` VARCHAR(10) NULL DEFAULT NULL,
  `Country` VARCHAR(15) NULL DEFAULT NULL,
  `Phone` VARCHAR(24) NULL DEFAULT NULL,
  `Fax` VARCHAR(24) NULL DEFAULT NULL,
  PRIMARY KEY (`CustomerID`),
  INDEX `City` (`City` ASC) VISIBLE,
  INDEX `CompanyName` (`CompanyName` ASC) VISIBLE,
  INDEX `PostalCode` (`PostalCode` ASC) VISIBLE,
  INDEX `Region` (`Region` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`EmployeeTerritories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`EmployeeTerritories` (
  `EmployeeID` INT NOT NULL,
  `TerritoryID` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`EmployeeID`, `TerritoryID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`Employees`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`Employees` (
  `EmployeeID` INT NOT NULL AUTO_INCREMENT,
  `LastName` VARCHAR(20) NOT NULL,
  `FirstName` VARCHAR(10) NOT NULL,
  `Title` VARCHAR(30) NULL DEFAULT NULL,
  `TitleOfCourtesy` VARCHAR(25) NULL DEFAULT NULL,
  `BirthDate` DATETIME NULL DEFAULT NULL,
  `HireDate` DATETIME NULL DEFAULT NULL,
  `Address` VARCHAR(60) NULL DEFAULT NULL,
  `City` VARCHAR(15) NULL DEFAULT NULL,
  `Region` VARCHAR(15) NULL DEFAULT NULL,
  `PostalCode` VARCHAR(10) NULL DEFAULT NULL,
  `Country` VARCHAR(15) NULL DEFAULT NULL,
  `HomePhone` VARCHAR(24) NULL DEFAULT NULL,
  `Extension` VARCHAR(4) NULL DEFAULT NULL,
  `Photo` LONGBLOB NULL DEFAULT NULL,
  `Notes` MEDIUMTEXT NOT NULL,
  `ReportsTo` INT NULL DEFAULT NULL,
  `PhotoPath` VARCHAR(255) NULL DEFAULT NULL,
  `Salary` FLOAT NULL DEFAULT NULL,
  PRIMARY KEY (`EmployeeID`),
  INDEX `LastName` (`LastName` ASC) VISIBLE,
  INDEX `PostalCode` (`PostalCode` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`Order Details`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`Order Details` (
  `OrderID` INT NOT NULL,
  `ProductID` INT NOT NULL,
  `UnitPrice` DECIMAL(10,4) NOT NULL DEFAULT '0.0000',
  `Quantity` SMALLINT NOT NULL DEFAULT '1',
  `Discount` DOUBLE(8,0) NOT NULL DEFAULT '0',
  PRIMARY KEY (`OrderID`, `ProductID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`Orders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`Orders` (
  `OrderID` INT NOT NULL AUTO_INCREMENT,
  `CustomerID` VARCHAR(5) NULL DEFAULT NULL,
  `EmployeeID` INT NULL DEFAULT NULL,
  `OrderDate` DATETIME NULL DEFAULT NULL,
  `RequiredDate` DATETIME NULL DEFAULT NULL,
  `ShippedDate` DATETIME NULL DEFAULT NULL,
  `ShipVia` INT NULL DEFAULT NULL,
  `Freight` DECIMAL(10,4) NULL DEFAULT '0.0000',
  `ShipName` VARCHAR(40) NULL DEFAULT NULL,
  `ShipAddress` VARCHAR(60) NULL DEFAULT NULL,
  `ShipCity` VARCHAR(15) NULL DEFAULT NULL,
  `ShipRegion` VARCHAR(15) NULL DEFAULT NULL,
  `ShipPostalCode` VARCHAR(10) NULL DEFAULT NULL,
  `ShipCountry` VARCHAR(15) NULL DEFAULT NULL,
  PRIMARY KEY (`OrderID`),
  INDEX `OrderDate` (`OrderDate` ASC) VISIBLE,
  INDEX `ShippedDate` (`ShippedDate` ASC) VISIBLE,
  INDEX `ShipPostalCode` (`ShipPostalCode` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 11078
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`Products`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`Products` (
  `ProductID` INT NOT NULL AUTO_INCREMENT,
  `ProductName` VARCHAR(40) NOT NULL,
  `SupplierID` INT NULL DEFAULT NULL,
  `CategoryID` INT NULL DEFAULT NULL,
  `QuantityPerUnit` VARCHAR(20) NULL DEFAULT NULL,
  `UnitPrice` DECIMAL(10,4) NULL DEFAULT '0.0000',
  `UnitsInStock` SMALLINT NULL DEFAULT '0',
  `UnitsOnOrder` SMALLINT NULL DEFAULT '0',
  `ReorderLevel` SMALLINT NULL DEFAULT '0',
  `Discontinued` BIT(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`ProductID`),
  INDEX `ProductName` (`ProductName` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`Region`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`Region` (
  `RegionID` INT NOT NULL,
  `RegionDescription` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`RegionID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`Shippers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`Shippers` (
  `ShipperID` INT NOT NULL AUTO_INCREMENT,
  `CompanyName` VARCHAR(40) NOT NULL,
  `Phone` VARCHAR(24) NULL DEFAULT NULL,
  PRIMARY KEY (`ShipperID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`Suppliers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`Suppliers` (
  `SupplierID` INT NOT NULL AUTO_INCREMENT,
  `CompanyName` VARCHAR(40) NOT NULL,
  `ContactName` VARCHAR(30) NULL DEFAULT NULL,
  `ContactTitle` VARCHAR(30) NULL DEFAULT NULL,
  `Address` VARCHAR(60) NULL DEFAULT NULL,
  `City` VARCHAR(15) NULL DEFAULT NULL,
  `Region` VARCHAR(15) NULL DEFAULT NULL,
  `PostalCode` VARCHAR(10) NULL DEFAULT NULL,
  `Country` VARCHAR(15) NULL DEFAULT NULL,
  `Phone` VARCHAR(24) NULL DEFAULT NULL,
  `Fax` VARCHAR(24) NULL DEFAULT NULL,
  `HomePage` MEDIUMTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`SupplierID`),
  INDEX `CompanyName` (`CompanyName` ASC) VISIBLE,
  INDEX `PostalCode` (`PostalCode` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `northwind`.`Territories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `northwind`.`Territories` (
  `TerritoryID` VARCHAR(20) NOT NULL,
  `TerritoryDescription` VARCHAR(50) NOT NULL,
  `RegionID` INT NOT NULL,
  PRIMARY KEY (`TerritoryID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
