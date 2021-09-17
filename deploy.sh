cd /Users/mschijf/PriveSources/connect4
mvn clean package 
ssh pi rm connect4/connect4*.jar
sftp pi <<< $'mput target/connect4*.jar connect4/'
ssh pi ./connect4/start.sh

