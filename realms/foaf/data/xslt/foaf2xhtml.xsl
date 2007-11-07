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
<xsl:param name="language" select="'LANGUAGE_IS_NULL'"/>
<xsl:param name="content-language" select="'CONTENT_LANGUAGE_IS_NULL'"/>

<xsl:param name="yanel.meta.language" select="'en'"/>

<xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title><xsl:value-of select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:name"/></title>
</head>

<body>
<!-- XML Link -->
<xsl:apply-templates select="/wyona:foaf/wyona:source" mode="source"/>
<xsl:apply-templates select="/wyona:foaf/wyona:third-party-source" mode="source"/>

<!-- RDF Link -->
<xsl:apply-templates select="/wyona:foaf/wyona:source" mode="original"/>
<xsl:apply-templates select="/wyona:foaf/wyona:third-party-source" mode="original"/>

<!-- OpenSocial People data API Link -->
<xsl:apply-templates select="/wyona:foaf/wyona:source" mode="atom"/>
<xsl:apply-templates select="/wyona:foaf/wyona:third-party-source" mode="atom"/>

<h2>Profile of <xsl:value-of select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:name"/></h2>
<p>
Workplace Homepage: <a href="{/wyona:foaf/rdf:RDF/foaf:Person/foaf:workplaceHomepage/@rdf:resource}"><xsl:value-of select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:workplaceHomepage/@rdf:resource"/></a>
</p>

<h3>Friends</h3>
<ul>
<xsl:apply-templates select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:knows"/>
</ul>
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
<a href="{$yarep.back2realm}{@href}">Original RDF</a> (TODO: Protect this data!)
<br/>
</xsl:template>

<xsl:template match="wyona:third-party-source" mode="original">
<a href="{@href}">Original RDF</a>
<br/>
</xsl:template>

<xsl:template match="wyona:source" mode="atom">
<a href="{$yarep.back2realm}{@href}?yanel.resource.viewid=atom">OpenSocial People data API</a>
<br/>
</xsl:template>

<xsl:template match="wyona:third-party-source" mode="atom">
<a href="?href={@href}&amp;yanel.resource.viewid=atom">OpenSocial People data API</a>
<br/>
</xsl:template>

<xsl:template match="foaf:knows">
<xsl:for-each select="foaf:Person">
  <li><a href="{rdfs:seeAlso/@rdf:resource}"><xsl:value-of select="foaf:name"/></a></li>
</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
