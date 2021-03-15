# Offshore transfer prices
Script for calculating order of production steps in capital group offshore companies to maximize net profit.

## Constraints
* In each country only 1 production step can be performed
* Product is sold in country where last production step takes place
* For each transaction between companies margin must be at least 10%

## How to use
* To adjust values (prices, taxes, etc), edit constants in [main.kt](src/main/kotlin/main.kt)
* To run execute `gradlew start`

## Requirements
* JDK 14