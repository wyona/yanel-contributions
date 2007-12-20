<?xml version="1.0"?>

<!--
IMPORTANT NOTE: See also src/resources/foaf/src/java/org/wyona/yanel/impl/resources/foaf/foaf2opensocial.xsl
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/2005/Atom"
                >
<!--
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                >
-->
  
  <xsl:param name="yanel.path.name" select="'YANEL_PATH_NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'YANEL_PATH_IS_NULL'"/>
  
  <xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>
  
  <xsl:template match="/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:foaf="http://xmlns.com/foaf/0.1/">
    <entry>
      <id><xsl:value-of select="$yanel.path"/></id>
      <title><xsl:value-of select="/rdf:RDF/foaf:Person/foaf:name"/></title>
    </entry>
  </xsl:template>
  
</xsl:stylesheet>
