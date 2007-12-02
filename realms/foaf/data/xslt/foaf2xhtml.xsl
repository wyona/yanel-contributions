<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns:xi="http://www.w3.org/2001/XInclude"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:foaf="http://xmlns.com/foaf/0.1/"
  xmlns:wyona="http://www.wyona.org/foaf/1.0"
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
>

<!-- IMPORTANT: Needs to correspond to the mime-type which is sent by the server! -->
<xsl:output method="xhtml" encoding="UTF-8"/>
<!--
<xsl:output method="html"/>
-->

<xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
<xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
<xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
<xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>
<xsl:param name="username" select="'USERNAME_IS_NULL'"/>
<xsl:param name="language" select="'LANGUAGE_IS_NULL'"/>
<xsl:param name="content-language" select="'CONTENT_LANGUAGE_IS_NULL'"/>

<xsl:param name="yanel.meta.language" select="'en'"/>

<xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title><xsl:value-of select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:name"/></title>
  <link rel="neutron-introspection" type="application/neutron+xml" href="?yanel.resource.usecase=introspection"/>
  <link rel="meta" type="application/rdf+xml" title="FOAF" href="{$name-without-suffix}.rdf"/>
</head>

<body>
<table border="1">
<tr>
<td colspan="2"><a href="{$yarep.back2realm}index.html">Find A Friend</a></td><td align="right"><xsl:choose><xsl:when test="$username != 'USERNAME_IS_NULL'"><a href="?yanel.usecase=logout">Logout</a></xsl:when><xsl:otherwise><a href="{$yarep.back2realm}login.html">Login</a></xsl:otherwise></xsl:choose> | <a href="?yanel.toolbar=on">Toolbar</a></td>
</tr>
<tr>
<td>
<h2>Profile of <xsl:value-of select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:name"/></h2>

<xsl:apply-templates select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:workplaceHomepage"/>
<xsl:apply-templates select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:homepage"/>
<xsl:apply-templates select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:phone"/>


<h3>People which <xsl:value-of select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:name"/> knows about</h3>
<!--
<h3>Friends</h3>
-->
<ul>
<xsl:apply-templates select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:knows"/>
</ul>
</td>

<td>
&#160;&#160;
</td>

<td valign="top">
<!-- XML Link -->
<xsl:apply-templates select="/wyona:foaf/wyona:source" mode="source"/>
<xsl:apply-templates select="/wyona:foaf/wyona:third-party-source" mode="source"/>

<!-- RDF Link -->
<xsl:apply-templates select="/wyona:foaf/wyona:source" mode="original"/>
<xsl:apply-templates select="/wyona:foaf/wyona:third-party-source" mode="original"/>

<!-- OpenSocial People data API Link -->
<xsl:apply-templates select="/wyona:foaf/wyona:source" mode="atom"/>
<xsl:apply-templates select="/wyona:foaf/wyona:third-party-source" mode="atom"/>
</td>
</tr>
</table>
</body>
</html>
</xsl:template>

<xsl:template match="wyona:source" mode="source">
<a href="?yanel.resource.viewid=source">XML</a>
<br/>
</xsl:template>

<xsl:template match="wyona:third-party-source" mode="source">
<a href="?href={@href}&amp;yanel.resource.viewid=source">XML</a>
<br/>
</xsl:template>

<xsl:template match="wyona:source" mode="original">
<a href="{$yarep.back2realm}{@href}">FOAF RDF</a>
<br/>
</xsl:template>

<xsl:template match="wyona:third-party-source" mode="original">
<a href="{@href}">Original FOAF RDF</a>
<br/>
</xsl:template>

<xsl:template match="wyona:source" mode="atom">
<a href="?yanel.resource.viewid=atom">OpenSocial People data API</a>
<br/>
</xsl:template>

<xsl:template match="wyona:third-party-source" mode="atom">
<a href="?href={@href}&amp;yanel.resource.viewid=atom">OpenSocial People data API</a>
<br/>
</xsl:template>

<xsl:template match="foaf:knows">
<xsl:for-each select="foaf:Person">
  <li>
  <xsl:choose>
    <xsl:when test="rdfs:seeAlso/@rdf:resource">
  <a href="print.html?href={rdfs:seeAlso/@rdf:resource}"><xsl:value-of select="foaf:name"/></a>
    </xsl:when>
    <xsl:otherwise>
  <xsl:value-of select="foaf:name"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates select="@dc:description" mode="person"/></li>
</xsl:for-each>
</xsl:template>

<xsl:template match="foaf:workplaceHomepage">
<p>
Workplace Homepage: <a href="{@rdf:resource}"><xsl:value-of select="@rdf:resource"/></a>
</p>
</xsl:template>

<xsl:template match="foaf:homepage">
<p>
Personal Homepage: <a href="{@rdf:resource}"><xsl:value-of select="@rdf:resource"/></a>
</p>
</xsl:template>

<xsl:template match="foaf:phone">
<p>
Phone: <xsl:value-of select="@rdf:resource"/>
</p>
</xsl:template>

<xsl:template match="@dc:description" mode="person">
&#160;(<xsl:value-of select="."/>)
</xsl:template>

</xsl:stylesheet>
