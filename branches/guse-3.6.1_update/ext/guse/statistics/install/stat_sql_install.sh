#!/bin/sh

. ./statistics.properties


#Updating SQL files with actual parameters (DBNAME + DBUSER REQUIRED) and executing them one by one

TEMPLATE_FOLDER=./sql_templates 
ACT_FOLDER=./actual_sqls

if [ -d $ACT_FOLDER ]; then
    rm -r $ACT_FOLDER
fi

mkdir ./actual_sqls

for SQLFILE in `find $TEMPLATE_FOLDER -type f -path '*.sql' | cut -d/ -f 3`
do
echo $SQLFILE
sed "s/<DATABASENAME_MODIFY>/$DBNAME/" $TEMPLATE_FOLDER/$SQLFILE > $ACT_FOLDER/"$SQLFILE"_mod; 
sed "s/<DATABASEUSER_MODIFY>/$DBUSER/" $ACT_FOLDER/"$SQLFILE"_mod > $ACT_FOLDER/"$SQLFILE"_act;
rm $ACT_FOLDER/"$SQLFILE"_mod
mysql -h $DBHOST -u $DBUSER --password=$DBPASS < $ACT_FOLDER/"$SQLFILE"_act
#echo "mysql -u $DBUSER --password=$DBPASS < $SQLFILE_act"
done