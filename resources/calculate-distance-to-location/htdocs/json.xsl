<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:template match="/">{
"locations":[
<xsl:for-each select="/locations/loc">{"location":"<xsl:value-of select="@name"/>","distance":"<xsl:value-of select="@distance"/>km","link":"<xsl:value-of select="@id"/>.html"}<xsl:if test="position() != last()">,
</xsl:if></xsl:for-each>
]
}
</xsl:template>
</xsl:stylesheet>
