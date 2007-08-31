<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:foaf="http://www.wyona.org/foaf/1.0"
>

<xsl:output method="xhtml"/>
<!-- NOTE: Must correspond with the mime-type delivered by the server. See src/java/org/wyona/yanel/impl/resources/DirectoryResource.java -->
<!--
<xsl:output method="html"/>
-->
<xsl:param name="query" select="'QUERY_IS_NULL'"/>



<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>Find a friend</title>
</head>

<body>
<h1>Find a friend</h1>
<p>
<a href="?q={$query}&amp;yanel.resource.viewid=source">XML view</a>
</p>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
