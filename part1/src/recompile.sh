/usr/local/mysql/bin/mysql -u root -p -D dblp < droptable.sql
/usr/local/mysql/bin/mysql -u root -p -D dblp < ../createtable.sql
javac -cp ../lib/mysql-connector-java-5.0.8-bin.jar:. Main.java DatabaseManager.java Works.java XMLParser.java
java -cp ../lib/mysql-connector-java-5.0.8-bin.jar:. Main ../dblp-data-big/dblp-data.xml