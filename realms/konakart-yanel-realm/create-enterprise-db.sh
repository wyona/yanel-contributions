#!/bin/sh

# Also see http://www.konakart.com/docs/manualInstallation_EE.html

# IMPORTANT: Re-configure INSTALL_DIR according to your setup
INSTALL_DIR=/Users/michaelwechner/src/konakart/konakart-enterprise-5.1.0.0.5688


WEBAPPS_DIR=$INSTALL_DIR/webapps
CLASSES_DIR=$WEBAPPS_DIR/konakartadmin/WEB-INF/classes
LIB_DIR=$WEBAPPS_DIR/konakartadmin/WEB-INF/lib
CLASSPATH=$LIB_DIR/konakartadmin_multistore.jar:$LIB_DIR/konakartadmin.jar:$LIB_DIR/commons-configuration-1.1.jar:$LIB_DIR/commons-lang-2.4.jar:$LIB_DIR/commons-collections-3.1.jar:$LIB_DIR/commons-logging-1.0.4.jar:$LIB_DIR/postgresql-8.2-504.jdbc3.jar:$LIB_DIR/konakart.jar:$LIB_DIR/konakart_utils.jar:$LIB_DIR/konakart_torque-3.3-RC1.jar:$CLASSES_DIR:$LIB_DIR/log4j-1.2.12.jar:$LIB_DIR/konakart_village-2.0.jar:$LIB_DIR/commons-beanutils-1.7.0.jar:$LIB_DIR/commons-dbcp-1.2.2.jar:$LIB_DIR/commons-pool-1.2.jar
echo "Classpath: $CLASSPATH"
SHARED_PRODUCTS=true
SHARED_CUSTOMERS=true
DATABASE=PostgreSQL

java -cp $CLASSPATH com.konakartadmin.utils.CreateEnterpriseDB -p $CLASSES_DIR/konakartadmin.properties -h $INSTALL_DIR -db $DATABASE -ps $SHARED_PRODUCTS -c $SHARED_CUSTOMERS -d
