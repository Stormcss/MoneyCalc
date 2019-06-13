# MoneyCalc [![Build Status](https://travis-ci.org/Stormcss/MoneyCalc.svg?branch=master)](https://travis-ci.org/Stormcss/MoneyCalc) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/f778035a0b5045988a053ffe346970ab)](https://app.codacy.com/app/Stormcss/MoneyCalc)

Backend service for pocket bookkeeping. 
Provides REST API for storing and accessing spending history, which could be spent for custom spending sections. 
API provides full CRUD set for user data such as personal settings, identifications, spending history, etc.
Statistics calculation for required spending sections and dates range allows to see analyzed spending data.

Following stack is used:
1) PostgreSQL for storing data;
2) Mybatis for accessing the data;
3) Spring MVC for providing REST API;
4) Spring Boot for configuring application;
5) Spring Security for providing secure access to personal data;
6) TestNG for integration and module tests;
7) Gradle for building the project;
8) Travis CI integration for controlling quality.

Project is divided to modules: 
1) MoneyCalcDTO - set of DTOs and Entity objects;
2) MoneyCalcMigrator - service for migrating data from .txt files from previous standalone Android app to current service;
3) MoneyCalcServer - service itself;
