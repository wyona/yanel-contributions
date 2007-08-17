              REALM for a maven repository
*************************************************************

configure the path to your maven repo in config/data-repository.xml 

e.g
<content src="/home/simon/.m2/repository/"/>
or
<content src="/var/www/wyona.org/app/maven2/"/>

add an info file to the repo (e.g. /var/www/wyona.org/app/maven2/info.xhtml)
which is showing up if accessing a directory (e.g. http://www.wyona.org/maven2/xalan/xalan/2.7.0/)

example content of such a file (info.xhtml):

<html>
  <head>
    <title>Wyona Maven Repository</title>
    <style></style>
  </head>
  <body>
    <h1>Wyona Maven Repository</h1>
    <p>This is the wyona maven repository. This is not thought to be used by humans, rather by maven.</p>
  </body>
</html>

