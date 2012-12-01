
#dir=`pwd`
#dir=`/home/user/lpds`
#echo $dir

echo "****************************************************************************************"
echo " Signing xmlrpc.jar..."
echo "****************************************************************************************"

#$JAVA_HOME/bin/jarsigner -keystore ./tomcat.key ./xmlrpc.jar tomcat-server
jarsigner -keystore ./tomcat.key ./xmlrpc.jar tomcat-server

echo "****************************************************************************************"
echo " Succesfull...."
echo "****************************************************************************************"
