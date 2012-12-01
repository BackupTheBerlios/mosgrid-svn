
#dir=`pwd`
#dir=`/home/user/lpds`
#echo $dir

echo "****************************************************************************************"
echo " Signing visualizer.jar..."
echo "****************************************************************************************"

#$JAVA_HOME/bin/jarsigner -keystore ./tomcat.key ./visualizer.jar tomcat-server
jarsigner -keystore ./tomcat.key ./visualizer.jar tomcat-server

echo "****************************************************************************************"
echo " Succesfull...."
echo "****************************************************************************************"
