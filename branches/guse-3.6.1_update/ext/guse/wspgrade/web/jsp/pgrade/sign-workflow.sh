
#dir=`pwd`
#dir=`/home/user/lpds`
#echo $dir

echo "****************************************************************************************"
echo " Signing workflow.jar..."
echo "****************************************************************************************"

#$JAVA_HOME/bin/jarsigner -keystore ./tomcat.key ./workflow.jar tomcat-server
jarsigner -keystore ./tomcat.key ./workflow.jar tomcat-server

echo "****************************************************************************************"
echo " Succesfull...."
echo "****************************************************************************************"
