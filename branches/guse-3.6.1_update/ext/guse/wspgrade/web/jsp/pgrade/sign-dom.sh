
#dir=`pwd`
#dir=`/home/user/lpds`
#echo $dir

echo "****************************************************************************************"
echo " Signing dom.jar..."
echo "****************************************************************************************"

#$JAVA_HOME/bin/jarsigner -keystore ./tomcat.key ./dom.jar tomcat-server
jarsigner -keystore ./tomcat.key ./dom.jar tomcat-server

echo "****************************************************************************************"
echo " Succesfull...."
echo "****************************************************************************************"