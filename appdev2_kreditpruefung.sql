-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 16. Jan 2024 um 19:27
-- Server-Version: 10.4.28-MariaDB
-- PHP-Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `appdev2_kreditprüfung`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `credit`
--

CREATE TABLE `credit` (
  `ID` int(11) NOT NULL,
  `CustomerId` int(11) NOT NULL,
  `CreditSum` int(11) NOT NULL,
  `CreditTimeRange` int(11) NOT NULL,
  `PaymentInterval` varchar(50) NOT NULL,
  `CreditName` varchar(50) NOT NULL,
  `InterestRateId` int(11) NOT NULL,
  `Status` varchar(50) NOT NULL,
  `suggestion` tinyint(1) NOT NULL,
  `originId` int(11) DEFAULT NULL,
  `firstSuggestionWorkerID` int(11) DEFAULT NULL,
  `secondSuggestionWorkerID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `credit`
--

INSERT INTO `credit` (`ID`, `CustomerId`, `CreditSum`, `CreditTimeRange`, `PaymentInterval`, `CreditName`, `InterestRateId`, `Status`, `suggestion`, `originId`, `firstSuggestionWorkerID`, `secondSuggestionWorkerID`) VALUES
(38, 9, 30000, 20, 'QUARTERLY', 'Auto', 1, 'GENEHMIGT', 0, NULL, NULL, NULL),
(39, 10, 30000, 12, 'MONTHLY', 'Autp', 1, 'GENEHMIGT', 0, NULL, NULL, NULL),
(40, 13, 550000, 120, 'MONTHLY', 'Haus', 1, 'OFFEN', 0, NULL, 17, NULL),
(41, 13, 550000, 24, 'QUARTERLY', 'Haus', 1, 'BEARBEITUNG', 1, 40, 17, NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `customer`
--

CREATE TABLE `customer` (
  `ID` int(11) NOT NULL,
  `firstname` varchar(50) NOT NULL,
  `lastname` varchar(50) NOT NULL,
  `street` varchar(50) NOT NULL,
  `postalcode` int(5) NOT NULL,
  `city` varchar(50) NOT NULL,
  `bonitaet` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `customer`
--

INSERT INTO `customer` (`ID`, `firstname`, `lastname`, `street`, `postalcode`, `city`, `bonitaet`) VALUES
(9, 'customer', 'customer', 'Schaevenstraße 1B', 50676, 'Köln', 100000),
(10, 'customer', 'customer', 'Vogelsangerstraße', 50676, 'Köln', 200000);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `initialcreditvalues`
--

CREATE TABLE `initialcreditvalues` (
  `ID` int(11) NOT NULL,
  `InterestRate` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `initialcreditvalues`
--

INSERT INTO `initialcreditvalues` (`ID`, `InterestRate`) VALUES
(1, 7.25);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `login`
--

CREATE TABLE `login` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `customer` int(11) DEFAULT NULL,
  `worker` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `login`
--

INSERT INTO `login` (`username`, `password`, `customer`, `worker`) VALUES
('admin', 'admin', NULL, 16),
('customer', 'customer', 9, NULL),
('customer2', 'customer', 10, NULL),
('customer3', 'customer', 13, NULL),
('manager', 'manager', NULL, 20),
('superior', 'superior', NULL, 19),
('worker', 'worker', NULL, 17),
('worker2', 'worker', NULL, 18);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `worker`
--

CREATE TABLE `worker` (
  `ID` int(11) NOT NULL,
  `firstname` varchar(50) NOT NULL,
  `lastname` varchar(50) NOT NULL,
  `permission` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `worker`
--

INSERT INTO `worker` (`ID`, `firstname`, `lastname`, `permission`) VALUES
(16, 'admin', 'admin', 'ADMIN'),
(17, 'worker', 'worker', 'WORKER'),
(18, 'worker2', 'worker', 'WORKER'),
(19, 'superior', 'superior', 'SUPERIOR'),
(20, 'manager', 'manager', 'MANAGER');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `credit`
--
ALTER TABLE `credit`
  ADD PRIMARY KEY (`ID`);

--
-- Indizes für die Tabelle `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`ID`);

--
-- Indizes für die Tabelle `initialcreditvalues`
--
ALTER TABLE `initialcreditvalues`
  ADD PRIMARY KEY (`ID`);

--
-- Indizes für die Tabelle `login`
--
ALTER TABLE `login`
  ADD PRIMARY KEY (`username`);

--
-- Indizes für die Tabelle `worker`
--
ALTER TABLE `worker`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `credit`
--
ALTER TABLE `credit`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;

--
-- AUTO_INCREMENT für Tabelle `customer`
--
ALTER TABLE `customer`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT für Tabelle `initialcreditvalues`
--
ALTER TABLE `initialcreditvalues`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT für Tabelle `worker`
--
ALTER TABLE `worker`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
