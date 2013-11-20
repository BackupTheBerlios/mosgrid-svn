#!/usr/bin/perl
use strict;
use warnings;

########### Please adjust the variables to your settings ##########################

# Path of the Tomcat root directory:
my $TOMCAT_ROOT="";

# Location of tomcat server e.g.:localhost
my $HOSTNAME="localhost";
# Port of the tomcat server e.g.:8080
my $DEFAULT_PORT="8080";
      
# MYSQL server e.g.:"localhost"
my $GUSE_DB_URL="";
# Database
my $GUSE_DB_NAME="";
# Username of MYSQL database 
my $GUSE_DB_USER="";
# Password of MYSQL database
my $GUSE_DB_PASS="";

###################### Do not modify below ########################################

my @extra_libs = ('cog-jglobus.jar','commons-logging-1.1.1.jar','cryptix32.jar','cryptix-asn1.jar','dsn.jar','imap.jar','javaee-api-5.0-1.jar','jce-jdk13-131.jar','mailapi.jar','mail.jar','mysql-connector-java-5.1.6-bin.jar','pop3.jar','puretls.jar','smtp.jar');
my @GUSE_packages = ('mysql', 'jdk', 'wget'); # wget is just needed for this check script!
my $TEMP_DIR="/tmp/guse_precheck";
my @envVars = ('JAVA_OPTS','X509_USER_PROXY','CATALINA_OPTS');
my $MYSQL_INSTALLED=1;
my $fatal = 0;

&check_env;
&check_java_version;
&check_java_type;
&check_packages;
&check_extralibs;
&check_portal_properties;
&check_tomcat;
&check_liferay;
&check_mysql;
&end_msg;

sub check_env
{
        print"\n********************************************************\n";
        print"Check environment variables\n";         
        print"********************************************************\n\n";
        my $fail=0;
        foreach (@envVars)
	{
	              if (defined($ENV{$_})&&($_ ne "")){
		      	print" $_.... (OK)\n";
		         }
	    		else{
			$fail=1;
		        print "\n!!!No $_ variable defined!!!\n ";
			 }
        }
    	if ($fail) {
	    &error_and_quit("Please configure the missing variable(s)");
	    }
}


sub check_packages
{
	my $failed = 0;
	print"\n********************************************************\n";
	print"Check the required additional packages\n";
	print"********************************************************\n\n";
	foreach (@GUSE_packages) {
		print $_;
		system "rpm --quiet -q $_";
		if ( $? ) {
			$failed = 1;
			print "(MISSING)\n \n";
		        if ($_ eq "mysql") {
			  $MYSQL_INSTALLED=0;  
			  }
		  } else {
		  print "..........(OK)\n";
		}
	 }
        if( $failed && $MYSQL_INSTALLED ) {
	        &error_and_quit("Required package is missing from the installation \nPlease install it first!");
         }
}


sub check_extralibs
{
	      my $failed = 0;
	      print"\n********************************************************\n";
	      print"Check the required extra libraries\n";
	      print"********************************************************\n\n";
	
	      foreach (@extra_libs) {
		  print"$_";
		  if (-e "$TOMCAT_ROOT/lib/$_" ) {
		      print "..........(OK)\n"
		   } else {
		      $failed = 1;
	              print "(MISSING)\n \n";				      
		      }
	          }
	      if( $failed ) {
		  &error_and_quit("!!!Required extra library is missing from the installation!!! \nPlease install it first!");
	       }
}

sub check_portal_properties
{
   	print"\n********************************************************\n";
        print"Checking portal-ext.properties\n";
        print"********************************************************\n\n";
        
        if (-e "$TOMCAT_ROOT/../portal-ext.properties" ) {
	    print "..........(OK)\n"
	    } else {
		&error_and_quit("The portal-ext.properties file does not exist!");
		}
}


sub check_java_version
{
    print"\n*******************************\n";
    print"** Checking java version ... **\n";
    print"*******************************\n\n";

    open (java_version, "java -version 2>&1|fgrep java|cut -c 15-17|");
    my $JAVA_VERSION=<java_version>;
    close(java_version);
    $JAVA_VERSION *= 10;
    my $REQUIRED_JAVA_VERSION=1.6;
    my $REQUIRED_JAVA_VERSION_corrected = $REQUIRED_JAVA_VERSION * 10;

    if ( $JAVA_VERSION >= $REQUIRED_JAVA_VERSION_corrected)
    {
     	print "JAVA Version OK!\n";
    }else
    {
     	&error_and_quit("Java version sould be at least \"$REQUIRED_JAVA_VERSION\"!\n\n");
    }
}

sub check_java_type
{
                print"\n*******************************\n";
                print"** Checking java type ...    **\n";
                print"*******************************\n\n";
      
      		open (java_type, "java -version 2>&1|fgrep Free|");
      		my $JAVA_TYPE=<java_type>;
		close(java_type);

      		if ( $JAVA_TYPE )
	          {
		    &error_and_quit("You might have openjdk installed, but the sun/oracle java is needed!\n\n");
		                }
                else {
		    print "JAVA type OK!\n";
		    }
}

sub rm_tmpdir {
    system"rm -rf $TEMP_DIR;"
    }

sub check_tomcat
  {
          print"\n********************************************************\n";
          print "Check available tomcat \n";
          print"********************************************************\n\n";
      
          print"Checking TOMCAT is running on $HOSTNAME:$DEFAULT_PORT? \n";
          system"mkdir $TEMP_DIR;cd $TEMP_DIR;wget -q http://$HOSTNAME:$DEFAULT_PORT >/dev/null";
          if ( $? )
	    {
		&rm_tmpdir;
		print "(WRONG SETUP)\n";
		&error_and_quit("TOMCAT setup is invalid.\nPlease fix it before installing gUSE!\n");
		} else
	    {
	        &rm_tmpdir;
	        print"***************************\n";
	        print"** Tomcat is running ... **\n";
	        print"***************************\n\n";
	           }
      }

sub check_liferay
  {
          print"\n********************************************************\n";
          print "Check available Liferay \n";
          print"********************************************************\n";
            
          print"Checking Liferay is running\n";
      	  open (FILE, "<", "$TOMCAT_ROOT/../portal-ext.properties") or die "Unable to open portal-ext.properties file";
          my $portalurl;
      	  while (<FILE>) {
	  	if ($_ =~ /ctx/) {
			$portalurl = substr("$_",12);
			}
		}
          close FILE;
      
          system"mkdir $TEMP_DIR;cd $TEMP_DIR;wget -q http://$HOSTNAME:$DEFAULT_PORT/$portalurl>/dev/null";
          system"fgrep liferay-menu $TEMP_DIR/*>/dev/null";
          if ( $? )
	    {
		      &rm_tmpdir;
		      print "(WRONG SETUP)\n\n";
		      &error_and_quit("Liferay setup may be invalid!\nIt should be checked manually before gUSE installation!\n");
		    } else
	    {
		      &rm_tmpdir;
		      print"****************************\n";
		      print"** Liferay is running ... **\n";
		      print"****************************\n\n";
		    }
}      

sub check_mysql
{
	    
    print"\n***********************************\n";
    print"** Checking mysql connection ... **\n";
    print"***********************************\n\n";
	    
    if (!$MYSQL_INSTALLED) {
	print"WARNING!! As mysql package is not installed, we can not check the database connection!\n\n";
	}
    else {
    system"mysql --host=$GUSE_DB_URL --user=$GUSE_DB_USER --password=$GUSE_DB_PASS $GUSE_DB_NAME -e \"\"";
    if ( $? )
     {
      print "(WRONG SETUP)\n";
      &error_and_quit("Could not connect to the database!\nPlease fix it before installing gUSE!\n");
     } else
     {
      print"*******************************\n";
      print"** Database is available ... **\n";
      print"*******************************\n\n";
     }
    }
	          
}

sub end_msg
{
    if ($fatal == 0) {
    		print "*************************************\n";
   		print "******** Congratulations ************\n";
    		print "** Prerequisites are set properly  **\n";
    		print"*************************************\n\n";
    }
}

sub error_and_quit()
{
              my $error_text=shift;
		   $fatal = 1;
              print "Fatal error! \n";
              print "$error_text \n";
              print "Please correct the error! \n";
}
