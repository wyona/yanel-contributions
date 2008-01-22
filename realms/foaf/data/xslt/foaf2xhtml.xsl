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
<xsl:param name="username" select="'foaf:uin'"/>
<xsl:param name="language" select="'LANGUAGE_IS_NULL'"/>
<xsl:param name="content-language" select="'CONTENT_LANGUAGE_IS_NULL'"/>

<xsl:param name="yanel.meta.language" select="'en'"/>
<xsl:param name="yanel.toolbar-status" select="'TOOLBAR_STATUS_IS_NULL'"/>

<xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title><xsl:value-of select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:name"/></title>
  <link rel="neutron-introspection" type="application/neutron+xml" href="?yanel.resource.usecase=introspection"/>
  <link rel="meta" type="application/rdf+xml" title="FOAF" href="{$name-without-suffix}.rdf"/>
  <link rel="stylesheet" type="text/css" href="{$yarep.back2realm}css/profile.css"/>
</head>

<body>
<table border="0">
<tr height="50">
<td><a href="{$yarep.back2realm}index.html">Find A Friend</a></td><td align="right"><xsl:choose><xsl:when test="$username != 'foaf:uin'">Signed in as <b><xsl:value-of select="$username"/></b>&#160;(<a href="?yanel.usecase=logout">Logout</a>)</xsl:when><xsl:otherwise><a href="{$yarep.back2realm}login.html">Login</a></xsl:otherwise></xsl:choose> | <xsl:choose><xsl:when test="$yanel.toolbar-status = 'on'"><a href="?yanel.toolbar=off">Disable Toolbar</a></xsl:when><xsl:otherwise><a href="?yanel.toolbar=on">Enable Toolbar</a></xsl:otherwise></xsl:choose></td>
</tr>

<tr>
<td colspan="2">
<fieldset>
<legend><b><xsl:if test="/wyona:foaf/wyona:third-party-source">Third-Party </xsl:if>Profile of <xsl:value-of select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:name"/></b></legend>
<table border="0">
<tr>
<td>
<xsl:apply-templates select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:workplaceHomepage"/>
<xsl:apply-templates select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:homepage"/>
<xsl:apply-templates select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:phone"/>
<xsl:apply-templates select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:weblog"/>
<xsl:apply-templates select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:depiction"/>


<xsl:if test="/wyona:foaf/rdf:RDF/foaf:Person/foaf:knows">
<h3>People which <xsl:value-of select="/wyona:foaf/rdf:RDF/foaf:Person/foaf:name"/> knows about</h3>
</xsl:if>
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

<br/><br/>
<xsl:choose><xsl:when test="$username = 'foaf:uin'"><xsl:apply-templates select="/wyona:foaf/wyona:third-party-source" mode="add-friend-uin"/><xsl:apply-templates select="/wyona:foaf/wyona:source" mode="add-friend-uin"/></xsl:when><xsl:otherwise><xsl:apply-templates select="/wyona:foaf/wyona:third-party-source" mode="add-friend"/><xsl:apply-templates select="/wyona:foaf/wyona:source" mode="add-friend"/></xsl:otherwise></xsl:choose>
</td>
</tr>
</table>
</fieldset>
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

<xsl:template match="wyona:third-party-source" mode="add-friend-uin">
  <a href="{$yarep.back2realm}add-friend.html?href={@href}&amp;remote">Add to your address book</a> | <a href="{$yarep.back2realm}send-invitation.html?profile-of-friend={@href}&amp;remote">Send invitation</a>
</xsl:template>

<xsl:template match="wyona:source" mode="add-friend-uin">
  <a href="{$yarep.back2realm}add-friend.html?href={@href}&amp;local">Add to your address book</a> | <a href="{$yarep.back2realm}send-invitation.html?profile-of-friend={@href}&amp;local">Send invitation</a>
</xsl:template>

<xsl:template match="wyona:third-party-source" mode="add-friend">
  <a href="{$yarep.back2realm}profiles/{$username}/add-friend.html?href={@href}&amp;remote">Add to your address book</a> | <a href="{$yarep.back2realm}profiles/{$username}/send-invitation.html?profile-of-friend={@href}&amp;remote">Send invitation</a>
</xsl:template>

<xsl:template match="wyona:source" mode="add-friend">
  <a href="{$yarep.back2realm}profiles/{$username}/add-friend.html?href={@href}&amp;local">Add to your address book</a> | <a href="{$yarep.back2realm}profiles/{$username}/send-invitation.html?profile-of-friend={@href}&amp;local">Send invitation</a>
</xsl:template>


<xsl:template match="wyona:source" mode="original">
<br/>
<a href="{$yarep.back2realm}{@href}"><img src="{$yarep.back2realm}foafTiny.gif" alt="FOAF RDF" border="0"/></a>
<br/><br/>
</xsl:template>

<xsl:template match="wyona:third-party-source" mode="original">
<br/>
<a href="{@href}"><img src="{$yarep.back2realm}foafTiny.gif" alt="Original FOAF RDF" border="0"/></a> (<a href="http://www.w3.org/RDF/Validator/ARPServlet?URI={@href}&amp;PARSE=Parse+URI%3A+&amp;TRIPLES_AND_GRAPH=PRINT_TRIPLES&amp;FORMAT=PNG_EMBED">Validate RDF</a>)
<br/><br/>
</xsl:template>

<xsl:template match="wyona:source" mode="atom">
<a href="{$yarep.back2realm}feeds/people/{$name-without-suffix}">OpenSocial People data API</a>
<br/>
</xsl:template>

<xsl:template match="wyona:third-party-source" mode="atom">
<a href="?href={@href}&amp;yanel.resource.viewid=atom">OpenSocial People data API</a> (<a href="http://code.google.com/apis/opensocial/docs/gdata/people/developers_guide_protocol.html">TODO</a>)
<br/>
</xsl:template>

<xsl:template match="foaf:knows">
<xsl:for-each select="foaf:Person">
  <li>
  <xsl:choose>
    <xsl:when test="rdfs:seeAlso/@rdf:resource">
      <xsl:variable name="rdf-link" select="rdfs:seeAlso/@rdf:resource"/>
      <xsl:choose>
        <xsl:when test="starts-with($rdf-link, 'http')">
          <a href="print.html?href={$rdf-link}"><xsl:value-of select="foaf:name"/></a>
        </xsl:when>
        <xsl:otherwise>
          <a href="{substring-before($rdf-link, '.rdf')}.html"><xsl:value-of select="foaf:name"/></a>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="foaf:homepage">
      <a href="{foaf:homepage/@rdf:resource}"><xsl:value-of select="foaf:name"/></a>
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
<xsl:if test="$yanel.toolbar-status='on'">
&#160;(<a href="?phone">Privacy</a>)
</xsl:if>
</p>
</xsl:template>

<xsl:template match="foaf:weblog">
<p>
Weblog: <a href="{@rdf:resource}"><xsl:value-of select="@rdf:resource"/></a>
</p>
</xsl:template>

<xsl:template match="foaf:depiction">
<p>
<img src="{@rdf:resource}" height="200"/>
</p>
</xsl:template>

<xsl:template match="@dc:description" mode="person">
&#160;(<xsl:value-of select="."/>)
</xsl:template>

</xsl:stylesheet>
