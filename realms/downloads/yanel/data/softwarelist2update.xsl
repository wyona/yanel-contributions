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
        <xsl:for-each select="//yanel:software[generate-id(.)=generate-id(key('id-version-revision', concat(yanel:id,-v-,yanel:version,-r-,yanel:revision)))][yanel:type = $type]">
          <rdf:li rdf:resource="urn:wyona:application:{yanel:type}:{yanel:id}-v-{yanel:version}-r-{yanel:revision}"/>
        </xsl:for-each>
      </rdf:Seq>
    </um:versions>
  </rdf:Description>
  </xsl:for-each>

<xsl:for-each select="//yanel:software[generate-id(.)=generate-id(key('id-version-revision', concat(yanel:id,-v-,yanel:version,-r-,yanel:revision)))]">
  <rdf:Description rdf:about="urn:wyona:application:{yanel:type}:{yanel:id}-v-{yanel:version}-r-{yanel:revision}">
    <um:id><xsl:value-of select="yanel:id"/></um:id>
    <um:title><xsl:value-of select="yanel:title"/></um:title>
    <um:version><xsl:value-of select="yanel:version"/></um:version>
    <um:revision><xsl:value-of select="yanel:revision"/></um:revision>
    <um:changelog><xsl:value-of select="yanel:changeLog"/></um:changelog>
    <xsl:variable name="id-version-revision"><xsl:value-of select="concat(yanel:id,-v-,yanel:version,-r-,yanel:revision)"/></xsl:variable>
    <um:targetApplication>
      <rdf:Seq>
        <xsl:for-each select="//yanel:software[generate-id(.)=generate-id(key('targetApplication', yanel:targetApplication))]">
          <rdf:li rdf:resource="urn:wyona:targetApplication:{yanel:targetApplication}:{yanel:id}-v-{yanel:version}-r-{yanel:revision}"/>
        </xsl:for-each>
      </rdf:Seq>
    </um:targetApplication>
  </rdf:Description>
</xsl:for-each>  

  <rdf:Description rdf:about="urn:wyona:targetApplication:tomcat:yanel-0.0.2-dev-r22926">
    <um:id>Apache Tomcat</um:id>
    <um:minVersion>5.0.0</um:minVersion>
    <um:maxVersion>5.5.*</um:maxVersion>
    <um:updateLink>http://localhost:8081/yanel/downloads/yanel-updates/wyona-yanel-UPDATE-0.0.2-dev-r22926.war</um:updateLink>
  </rdf:Description>

  <rdf:Description rdf:about="urn:wyona:application:updater:yanel-updater-0.0.2-dev-r22926">
    <um:id>yanel-updater@wyona.org</um:id>
    <um:title>Yanel Updater</um:title>
    <um:version>0.0.2-dev</um:version>
    <um:revision>22926</um:revision>
    <um:changelog>this fixed and that improved</um:changelog>
    <um:targetApplication>
      <rdf:Seq>
        <rdf:li rdf:resource="urn:wyona:targetApplication:updater:yanel-updater-0.0.2-dev-r22926"/>
      </rdf:Seq>
    </um:targetApplication>
  </rdf:Description>
  
  <rdf:Description rdf:about="urn:wyona:targetApplication:updater:yanel-updater-0.0.2-dev-r22926">
    <um:id>yanel-webapp@wyona.org</um:id>
    <um:minVersion>0.0.1</um:minVersion>
    <um:maxVersion>1.0.0</um:maxVersion>
    <um:updateLink>http://localhost:8081/yanel/downloads/yanel-updates/wyona-yanel-updater-0.0.2-dev-r22926.war</um:updateLink>
  </rdf:Description>
  
</rdf:RDF>
</xsl:template>


</xsl:stylesheet>