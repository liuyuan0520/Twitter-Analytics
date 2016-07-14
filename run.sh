mysql -u root -p123456 p2 < clear_q4.sql
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
mvn compile
mvn exec:java -Dexec.mainClass="Main" -e