-- phpMyAdmin SQL Dump
-- version 5.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 02, 2020 at 10:32 AM
-- Server version: 10.4.11-MariaDB
-- PHP Version: 7.2.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `onestop`
--

-- --------------------------------------------------------

--
-- Table structure for table `productmaster`
--

CREATE TABLE `productmaster` (
  `ProductId` int(11) NOT NULL,
  `ProductName` varchar(100) NOT NULL,
  `Color` varchar(50) DEFAULT NULL,
  `ProductImage1` text NOT NULL,
  `ProductImage2` text NOT NULL,
  `ProductImage3` text NOT NULL,
  `Status` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `productmaster`
--

INSERT INTO `productmaster` (`ProductId`, `ProductName`, `Color`, `ProductImage1`, `ProductImage2`, `ProductImage3`, `Status`) VALUES
(73, 'purell', 'blue', '1606280941', '1606280941', '1606280941', 1),
(74, 'Purrell', '14 oz Hand sanitizer', '1606369962', '1606369962', '1606369962', 1),
(76, 'Softsoap', '28 oz', '1606370030', '1606370030', '1606370030', 1),
(79, 'Kleenex', 'Face cleaner', '1606813135', '1606813135', '1606813135', 1),
(81, 'Green Apple', 'Organic', '1606813262', '1606813262', '1606813262', 1),
(82, 'Bounty', 'Hand tissue paper', '1606814460', '1606814460', '1606814460', 1);

-- --------------------------------------------------------

--
-- Table structure for table `productstock`
--

CREATE TABLE `productstock` (
  `StockId` int(11) NOT NULL,
  `ProductId` int(11) NOT NULL,
  `StoreId` int(11) NOT NULL,
  `ManufactureDate` varchar(50) DEFAULT NULL,
  `ExpiryDate` varchar(50) DEFAULT NULL,
  `StockInQuantity` decimal(10,2) NOT NULL,
  `StockOutQuantity` decimal(10,2) NOT NULL,
  `SalesRate` decimal(10,2) NOT NULL,
  `StockBalanceQuantity` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `productstock`
--

INSERT INTO `productstock` (`StockId`, `ProductId`, `StoreId`, `ManufactureDate`, `ExpiryDate`, `StockInQuantity`, `StockOutQuantity`, `SalesRate`, `StockBalanceQuantity`) VALUES
(26, 73, 16, '0000-00-00', '0000-00-00', '5.00', '1.00', '100.00', '4.00'),
(27, 74, 17, '0000-00-00', '0000-00-00', '1.00', '1.00', '12.00', '0.00'),
(29, 76, 17, '0000-00-00', '0000-00-00', '1.00', '1.00', '30.00', '0.00'),
(32, 79, 19, '', '11/10/2020', '1.00', '1.00', '12.00', '0.00'),
(35, 81, 19, '', 'N/A', '1.00', '1.00', '0.39', '0.00'),
(36, 82, 19, '', 'N/A', '8.00', '0.00', '20.00', '0.00');

-- --------------------------------------------------------

--
-- Table structure for table `salesorder`
--

CREATE TABLE `salesorder` (
  `OrderId` int(11) NOT NULL,
  `ProductId` int(11) NOT NULL,
  `StoreId` int(11) NOT NULL,
  `UserId` int(11) NOT NULL,
  `StoreName` varchar(100) NOT NULL,
  `ProductName` varchar(100) NOT NULL,
  `Quantity` decimal(10,2) NOT NULL,
  `Rate` decimal(10,2) NOT NULL,
  `Amount` decimal(10,2) NOT NULL,
  `OrderDate` varchar(50) NOT NULL,
  `Latitude` varchar(50) NOT NULL,
  `Longitude` varchar(50) NOT NULL,
  `IsOrderCancel` tinyint(1) NOT NULL,
  `IsOrderDelivery` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `salesorder`
--

INSERT INTO `salesorder` (`OrderId`, `ProductId`, `StoreId`, `UserId`, `StoreName`, `ProductName`, `Quantity`, `Rate`, `Amount`, `OrderDate`, `Latitude`, `Longitude`, `IsOrderCancel`, `IsOrderDelivery`) VALUES
(78, 79, 19, 41, 'Dent Grocery Store', 'Kleenex', '1.00', '12.00', '12.00', '2020-01-12 03:25:34', '37.421998333333335', '-122.08400000000002', 1, 1),
(79, 81, 19, 41, 'Dent Grocery Store', 'Green Apple', '1.00', '0.39', '0.39', '2020-01-12 03:25:47', '37.421998333333335', '-122.08400000000002', 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `storeregister`
--

CREATE TABLE `storeregister` (
  `StoreId` int(11) NOT NULL,
  `StoreName` varchar(100) NOT NULL,
  `Address` varchar(150) NOT NULL,
  `PhoneNo` varchar(20) NOT NULL,
  `MobileNo` varchar(20) DEFAULT NULL,
  `EmailId` varchar(50) DEFAULT NULL,
  `OpeningHour` time NOT NULL,
  `CloseHour` time NOT NULL,
  `StoreImage` text NOT NULL,
  `Latitude` varchar(50) NOT NULL,
  `Longitude` varchar(50) NOT NULL,
  `Status` tinyint(1) NOT NULL,
  `UserName` varchar(10) NOT NULL,
  `Password` varchar(200) NOT NULL,
  `UserType` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `storeregister`
--

INSERT INTO `storeregister` (`StoreId`, `StoreName`, `Address`, `PhoneNo`, `MobileNo`, `EmailId`, `OpeningHour`, `CloseHour`, `StoreImage`, `Latitude`, `Longitude`, `Status`, `UserName`, `Password`, `UserType`) VALUES
(16, 'Nancy Store', '5232 N ester rd', '4697892587', '4968752359', 'nure@', '10:32:00', '05:32:00', '1606369232', '37.421998333333335', '-122.08400000000002', 1, 'nure100', '12345', 'store'),
(17, 'Rakesh Grocery', '1111 e McKinney st.', '9852365415', '5123654125', '', '07:30:00', '12:00:00', '1606369638', '37.421998333333335', '-122.08400000000002', 1, 'rakesh12', 'Janak', 'store'),
(18, 'Danny Food Store', '2085 strathmore dr, highland village, tx, 75077', '4697893251', '4699523547', 'denn@gmail.com', '07:00:00', '12:00:00', '1606799420', '33.23329', '-97.16851', 1, 'denyy12', 'Jaiho@123', 'store'),
(19, 'Dent Grocery Store', '4008 Grimes rd, Irving, TX, 76201', '9403265841', '9401236548', 'Dent12@gmail.com', '07:30:00', '12:00:00', '1606812865', '37.421998333333335', '-122.08400000000002', 1, 'dent1', 'Denton@123', 'store');

-- --------------------------------------------------------

--
-- Table structure for table `userregister`
--

CREATE TABLE `userregister` (
  `UserId` int(11) NOT NULL,
  `FullName` varchar(50) NOT NULL,
  `UserName` varchar(10) NOT NULL,
  `PhoneNo` varchar(20) DEFAULT NULL,
  `MobileNo` varchar(20) NOT NULL,
  `Email` varchar(50) DEFAULT NULL,
  `Address` varchar(100) NOT NULL,
  `State` varchar(50) NOT NULL,
  `City` varchar(50) NOT NULL,
  `ZipCode` varchar(10) NOT NULL,
  `Password` varchar(200) NOT NULL,
  `Latitude` varchar(50) NOT NULL,
  `Longitude` varchar(50) NOT NULL,
  `Status` tinyint(1) NOT NULL,
  `UserType` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `userregister`
--

INSERT INTO `userregister` (`UserId`, `FullName`, `UserName`, `PhoneNo`, `MobileNo`, `Email`, `Address`, `State`, `City`, `ZipCode`, `Password`, `Latitude`, `Longitude`, `Status`, `UserType`) VALUES
(39, 'nurenabi', 'nabi100', '123424', '23423', 'nabi@', 'siraha', '2', 'siraha', '2334', '12345', '37.421998333333335', '-122.08400000000002', 1, 'user'),
(40, 'Rakesh Yadav', 'rakesh11', '98456879521', '9523548795', 'way2rakesh16@gmail.com', '982 w sycamore st.', 'TX', 'Denton', '76201', 'Janak', '37.421998333333335', '-122.08400000000002', 1, 'user'),
(41, 'Rakesh Yadav', 'rakesh123', '594464646', '111155141', 'rak@gmail.com', '923 w sycamore st', 'tx', 'denton', '76215', 'Rakesh@123', '37.421998333333335', '-122.08400000000002', 1, 'user');

-- --------------------------------------------------------

--
-- Table structure for table `usertype`
--

CREATE TABLE `usertype` (
  `UserTypeId` int(11) NOT NULL,
  `UserName` varchar(10) NOT NULL,
  `UserType` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `usertype`
--

INSERT INTO `usertype` (`UserTypeId`, `UserName`, `UserType`) VALUES
(14, 'nure100', 'store'),
(15, 'nabi100', 'user'),
(16, 'rakesh12', 'store'),
(17, 'rakesh11', 'user'),
(18, 'rakesh123', 'user'),
(19, 'denyy12', 'store'),
(20, 'dent1', 'store');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `productmaster`
--
ALTER TABLE `productmaster`
  ADD PRIMARY KEY (`ProductId`);

--
-- Indexes for table `productstock`
--
ALTER TABLE `productstock`
  ADD PRIMARY KEY (`StockId`),
  ADD KEY `ProductId` (`ProductId`),
  ADD KEY `StoreId` (`StoreId`);

--
-- Indexes for table `salesorder`
--
ALTER TABLE `salesorder`
  ADD PRIMARY KEY (`OrderId`),
  ADD KEY `ProductId` (`ProductId`),
  ADD KEY `StoreId` (`StoreId`),
  ADD KEY `UserId` (`UserId`);

--
-- Indexes for table `storeregister`
--
ALTER TABLE `storeregister`
  ADD PRIMARY KEY (`StoreId`);

--
-- Indexes for table `userregister`
--
ALTER TABLE `userregister`
  ADD PRIMARY KEY (`UserId`);

--
-- Indexes for table `usertype`
--
ALTER TABLE `usertype`
  ADD PRIMARY KEY (`UserTypeId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `productmaster`
--
ALTER TABLE `productmaster`
  MODIFY `ProductId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=83;

--
-- AUTO_INCREMENT for table `productstock`
--
ALTER TABLE `productstock`
  MODIFY `StockId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT for table `salesorder`
--
ALTER TABLE `salesorder`
  MODIFY `OrderId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=80;

--
-- AUTO_INCREMENT for table `storeregister`
--
ALTER TABLE `storeregister`
  MODIFY `StoreId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `userregister`
--
ALTER TABLE `userregister`
  MODIFY `UserId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;

--
-- AUTO_INCREMENT for table `usertype`
--
ALTER TABLE `usertype`
  MODIFY `UserTypeId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `productstock`
--
ALTER TABLE `productstock`
  ADD CONSTRAINT `ProductStock_ibfk_1` FOREIGN KEY (`ProductId`) REFERENCES `productmaster` (`ProductId`),
  ADD CONSTRAINT `ProductStock_ibfk_2` FOREIGN KEY (`StoreId`) REFERENCES `storeregister` (`StoreId`);

--
-- Constraints for table `salesorder`
--
ALTER TABLE `salesorder`
  ADD CONSTRAINT `SalesOrder_ibfk_1` FOREIGN KEY (`ProductId`) REFERENCES `productmaster` (`ProductId`),
  ADD CONSTRAINT `SalesOrder_ibfk_2` FOREIGN KEY (`StoreId`) REFERENCES `storeregister` (`StoreId`),
  ADD CONSTRAINT `SalesOrder_ibfk_3` FOREIGN KEY (`UserId`) REFERENCES `userregister` (`UserId`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
