# NineNinetyNine

This projects looks up all [Gamestop 9.99€](https://www.gamestop.de/9.99er) items from the "[Eintauschliste](https://www.gamestop.de/eintauschliste)" and queries each item on "[Ebay Kleinanzeigen](http://www.ebay-kleinanzeigen.de/)".

You are able to specify:
- Postal code
- Radius away from the area
- Your maximum price
- Age of the ebay post in days

## Example
A new game is worth about 60€. So if you can buy two games off ebay for less than this price (+fee) you can reduce the cost of the new game.

| Money  |Description |
| ------------- | ------------- |
| ~60€  | New game at Gamestop  |
| -9.99€  | Gamestop fee  |
| --- | --- |
| -15€  | Game1 on list  |
| -15€  | Game2 on list  |
| --- | --- |
| ~20€  | Savings on a new game |

## Requirements
- [Java 1.8](https://java.com/de/download/)
- [Maven](https://maven.apache.org/download.cgi)

## Build
  - Run "**mvn clean package**"
  
## Setup
1) Edit settings.properties
   ```
   plz=1337
   radius=5
   maxprice=50
   lessthan=10
   #verbose=true
   ```
2) Run
   ```
   java -jar NineNinetyNine.jar
   ```
3) Look up results
   ```
   results_YYYYMMDD_HHMMSS.html
   ```
4) $$$ Profit   
   
