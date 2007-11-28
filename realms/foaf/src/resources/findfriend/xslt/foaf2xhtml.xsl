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
<xsl:param name="type" select="'TYPE_IS_NULL'"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>Find a friend</title>
  <link rel="foaf-introspection" type="application/foaf+xml" href="foaf-introspection.xml"/>
</head>

<body>
<p>
<font size="-1">
<a href="login.html">Login</a> | <a href="register.html">Register</a> | <a href="about.html">About</a> | <a href="developers.html">Developers</a>
</font>
</p>

<xsl:choose>
<!-- Result Page Layout -->
<xsl:when test="/foaf:foaf/foaf:provider">
<p>
<!-- TODO: Do not hardcode the action ... -->
<xsl:if test="$type = 'advanced'"><h2>Advanced Search</h2></xsl:if>
<table border="0">
<tr><td valign="top">
<form action="." method="get">
<a href="index.html">Find a Friend:</a>&#160;<input type="text" name="q"><xsl:if test="$query != 'QUERY_IS_NULL'"><xsl:attribute name="value"><xsl:value-of select="$query"/></xsl:attribute></xsl:if></input>
<input type="submit" value="Search" name="submit"/>
</form>
</td>
<td>
&#160;
</td>
<td valign="top">
<font size="-1">
<!--DEBUG2:<xsl:value-of select="$type"/>--> <!--<xsl:choose><xsl:when test="$type = 'simple'"><a href="advanced-search.html?q={$query}">Advanced Search</a></xsl:when><xsl:otherwise><a href="index.html?q={$query}">Simple Search</a></xsl:otherwise></xsl:choose><br/>--> <a href="?q={$query}&amp;yanel.resource.viewid=source">XML view</a>
</font>
</td></tr>
</table>
</p>
<xsl:apply-templates select="/foaf:foaf/foaf:provider"/>
<p>
Copyright &#169; 2007 Wyona
</p>
</xsl:when>


<!-- Homepage Layout -->
<xsl:otherwise>
<center>
<h1>Find a friend</h1>
<p>
<!-- TODO: Do not hardcode the action ... -->
<table border="0">
<tr><td valign="top">
<form action="." method="get">
<input type="text" name="q"><xsl:if test="$query != 'QUERY_IS_NULL'"><xsl:attribute name="value"><xsl:value-of select="$query"/></xsl:attribute></xsl:if></input>
<input type="submit" value="Search" name="submit"/>
</form>
</td>
<td>&#160;</td>
<td valign="top">
<font size="-1">
<!--DEBUG1:<xsl:value-of select="$type"/>-->  <!--<xsl:choose><xsl:when test="$type = 'simple'"><a href="advanced-search.html?q={$query}">Advanced Search</a></xsl:when><xsl:otherwise><a href="index.html?q={$query}">Simple Search</a></xsl:otherwise></xsl:choose> |-->  <a href="?q={$query}&amp;yanel.resource.viewid=source">XML view</a>
</font>
</td></tr>
</table>
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
  <h4>Search Result Provider: <a href="{@source-domain}"><xsl:value-of select="@source-name"/></a></h4>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="foaf:no-results">
  <p>No results from this provider!</p>
</xsl:template>

<xsl:template match="foaf:result">
  <p>
  <b><xsl:choose><xsl:when test="foaf:mime-type/@suffix = 'rdf'"><a href="print.html?href={foaf:url}"><xsl:value-of select="foaf:title"/></a></xsl:when><xsl:otherwise><a href="{foaf:url}"><xsl:value-of select="foaf:title"/></a></xsl:otherwise></xsl:choose></b>
  <br/>
  <xsl:value-of select="foaf:excerpt"/>
  <div class="original-href"><a href="{foaf:url}"><xsl:value-of select="foaf:url"/></a> (Profile Provider: TODO)</div>
  <a href="sign-in.html?usecase=add-friend&amp;href={foaf:url}">Add to your address book</a> | <a href="sign-in.html">Send invitation</a><xsl:if test="foaf:mime-type/@suffix = 'rdf'"> | <a href="print.html?href={foaf:url}">Print</a></xsl:if>
  </p>
</xsl:template>

</xsl:stylesheet>
