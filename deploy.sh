program="connect4"
cd /Users/mschijf/PriveSources/$program
mvn clean package 
ssh pi rm $program/$program*.jar
sftp pi <<< $'mput target/'$program'*.jar '$program'/'
ssh pi ./$program/start.sh

