Instructions:
Use command line argument to pass in location of XML file
Change the USER, PASS, and DB static vars in DatabaseManager.java on lines 14,15,16 to the one for your system.

Compile with the following command:
javac -cp ../lib/mysql-connector-java-5.0.8-bin.jar:. Main.java DatabaseManager.java Works.java XMLParser.java

Before optimizations: 173619 ms
After enabling batch inserts of 1000 records and autocommit: 22411 ms
Difference: 151208 ms