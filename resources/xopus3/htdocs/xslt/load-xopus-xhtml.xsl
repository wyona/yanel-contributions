<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xhtml"
>

<xsl:template match="/">
<xhtml:html>
  <head>
    <title>Load Xopus ...</title>
    <script language="JavaScript" src="/xopus/xopus.js"/>
  </head>
  <body bgcolor="#FFFFFF">
    <div xopus="true" autostart="true">
      ...Xopus hasn't started yet...
      <xml>
        <pipeline
            xml="?xml"
            xsd="?xsd">
            
          <view id="defaultView" default="true">
            <transform xsl="/xopusPlugins/preparexinclude.xsl"/>
            <resolveXIncludes/>
            <transform xsl="?xslt">
              <param name="some_param_name">some_param_value</param>
            </transform>
          </view>
          <view id="treeView">
            <transform xsl="/xopus/media/treeview/tree.xsl"></transform>
          </view>
        </pipeline>
      </xml>
    </div>
  </body>
</xhtml:html>
</xsl:template>

</xsl:stylesheet>
