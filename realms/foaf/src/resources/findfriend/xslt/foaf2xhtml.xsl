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
  <link rel="foaf-introspection" type="application/foaf+xml" href="foaf-introspection.xml"/>
</head>

<body>
<p>
<a href="">Sign in</a> | <a href="">Sign up</a> | <a href="?q={$query}&amp;yanel.resource.viewid=source">XML view</a>
</p>
<xsl:choose>
<xsl:when test="/foaf:foaf/foaf:provider">
<p>
<!-- TODO: Do not hardcode the action ... -->
<form action="." method="get">
<input type="text" name="q"><xsl:if test="$query != 'QUERY_IS_NULL'"><xsl:attribute name="value"><xsl:value-of select="$query"/></xsl:attribute></xsl:if></input>
<input type="submit" value="Search" name="submit"/>
<br/>
<a href="advanced-search.html?q={$query}">Advanced Search</a>
</form>
</p>
<xsl:apply-templates select="/foaf:foaf/foaf:provider"/>
<p>
Copyright &#169; 2007 Wyona
</p>
</xsl:when>


<xsl:otherwise>
<center>
<h1>Find a friend</h1>

<p>
<!-- TODO: Do not hardcode the action ... -->
<form action="." method="get">
<input type="text" name="q"><xsl:if test="$query != 'QUERY_IS_NULL'"><xsl:attribute name="value"><xsl:value-of select="$query"/></xsl:attribute></xsl:if></input>
<input type="submit" value="Search" name="submit"/>
</form>
<br/>
<a href="advanced-search.html">Advanced Search</a>
</p>

<p>
Copyright &#169; 2007 Wyona
</p>
</center>
</xsl:otherwise>
</xsl:choose>
</body>
</html>
</xsl:template>

<xsl:template match="foaf:provider">
  <hr/>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="foaf:result">
  <p>
  <b><a href="{foaf:url}"><xsl:value-of select="foaf:title"/></a></b>
  <br/>
  <xsl:value-of select="foaf:excerpt"/>
  <br/>
  <a href="sign-in.html">Add to your address book</a> | <a href="sign-in.html">Send invitation</a> | <a href="">Print</a>
  </p>
</xsl:template>

</xsl:stylesheet>
