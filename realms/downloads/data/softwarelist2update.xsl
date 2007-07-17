<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
  xmlns:um="http://www.wyona.org/update-manager/1.0#"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
>

<xsl:output indent="yes"/>

<xsl:key name="type" match="yanel:software" use="yanel:type"/>
<xsl:key name="id-version-revision" match="yanel:software" use="concat(yanel:id,-v-,yanel:version,-r-,yanel:revision)"/>
<xsl:key name="targetApplication" match="yanel:software" use="yanel:targetApplication"/>

<xsl:template match="/">
<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
     xmlns:um="http://www.wyona.org/update-manager/1.0#">

  <rdf:Description rdf:about="urn:wyona:application">
    <um:types>
      <rdf:Seq>
        <xsl:for-each select="//yanel:software[generate-id(.)=generate-id(key('type',yanel:type))]">
          <rdf:li rdf:resource="urn:wyona:application:{yanel:type}"/>
        </xsl:for-each>
      </rdf:Seq>
    </um:types>
  </rdf:Description>  

<xsl:for-each select="//yanel:software[generate-id(.)=generate-id(key('type',yanel:type))]">
  <rdf:Description rdf:about="urn:wyona:application:{yanel:type}">
    <um:type><xsl:value-of select="yanel:type"/></um:type>
    <xsl:variable name="type"><xsl:value-of select="yanel:type"/></xsl:variable>
    <um:versions>
      <rdf:Seq>
        <xsl:for-each select="//yanel:software[yanel:type = $type]">
          <rdf:li rdf:resource="urn:wyona:application:{yanel:type}:{yanel:id}-v-{yanel:version}-r-{yanel:revision}"/>
        </xsl:for-each>
      </rdf:Seq>
    </um:versions>
  </rdf:Description>
  </xsl:for-each>

  <xsl:apply-templates select="/yanel:softwarelist/yanel:software"/>
</rdf:RDF>
</xsl:template>

<xsl:template match="yanel:software">
<rdf:Description rdf:about="urn:wyona:application:{yanel:type}:{yanel:id}-v-{yanel:version}-r-{yanel:revision}">
    <um:id><xsl:value-of select="yanel:id"/></um:id>
    <um:title><xsl:value-of select="yanel:title"/></um:title>
    <um:version><xsl:value-of select="yanel:version"/></um:version>
    <um:revision><xsl:value-of select="yanel:revision"/></um:revision>
    <um:changelog><xsl:value-of select="yanel:changeLog"/></um:changelog>
    <um:targetApplicationId><xsl:value-of select="yanel:targetApplicationId"/></um:targetApplicationId>
    <um:targetApplicationMinVersion><xsl:value-of select="yanel:targetApplicationMinVersion"/></um:targetApplicationMinVersion>
    <um:targetApplicationMaxVersion><xsl:value-of select="yanel:targetApplicationMaxVersion"/></um:targetApplicationMaxVersion>
    <um:targetApplicationMinRevision><xsl:value-of select="yanel:targetApplicationMinRevision"/></um:targetApplicationMinRevision>
    <um:targetApplicationMaxRevision><xsl:value-of select="yanel:targetApplicationMaxRevision"/></um:targetApplicationMaxRevision>
    <um:updateLink><xsl:value-of select="yanel:updateLink"/></um:updateLink>
    <!-- <um:md5><xsl:value-of select="yanel:md5"/></um:md5> -->
  </rdf:Description>
</xsl:template>

</xsl:stylesheet>