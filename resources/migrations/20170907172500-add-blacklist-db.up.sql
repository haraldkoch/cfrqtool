-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 07, 2017 at 05:29 PM
-- Server version: 10.2.8-MariaDB-10.2.8+maria~trusty
-- PHP Version: 5.5.9-1ubuntu4.22

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `blacklist`
--
CREATE DATABASE IF NOT EXISTS `blacklist` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `blacklist`;

-- --------------------------------------------------------

--
-- Table structure for table `blacklist`
--

CREATE TABLE IF NOT EXISTS `blacklist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varbinary(16) NOT NULL COMMENT 'IP address or prefix',
  `prefix` tinyint(3) unsigned NOT NULL COMMENT 'prefix length',
  `type` enum('http','smtp','all') CHARACTER SET ascii NOT NULL DEFAULT 'http',
  `date` datetime NOT NULL DEFAULT current_timestamp(),
  `description` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ip` (`ip`,`prefix`),
  KEY `type` (`type`,`date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `whitelist`
--

CREATE TABLE IF NOT EXISTS `whitelist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varbinary(16) NOT NULL,
  `prefix` tinyint(3) unsigned NOT NULL,
  `type` enum('http','smtp','all') CHARACTER SET latin1 NOT NULL DEFAULT 'http',
  `date` datetime NOT NULL DEFAULT current_timestamp(),
  `description` text CHARACTER SET latin1 DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ip` (`ip`,`prefix`),
  KEY `date` (`date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
