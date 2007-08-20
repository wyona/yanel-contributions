              REALM for a maven repository
*************************************************************

configure the path to your maven repo in config/data-repository.xml 

intern:
let it as it is:
<content src="../data"/>
and copy the maven repo content into the data directory
extern:
e.g
<content src="/home/simon/.m2/repository/"/>
or
<content src="/var/www/wyona.org/app/maven2/"/>
