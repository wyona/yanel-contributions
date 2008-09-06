<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xhtml"
>

<!--
See http://xopus.com/Documentation/Developer%20Guide%5CHow%20To%5CUpgrade%20from%20Xopus%202%20to%203.new
-->

<xsl:template match="/">
<xhtml:html>
  <head>
    <title>Load Xopus ...</title>
    <script language="JavaScript" src="yanel/resource-types/http%3a%2f%2fwww.wyona.org%2fyanel%2fresource%2f1.0%3a%3axopus3/Xopus-3.2.10/xopus/xopus.js"/>
<!--
BACK2REALM/yanel/resource-types/http%3a%2f%2fwww.wyona.org%2fyanel%2fresource%2f1.0%3a%3atinymce/js/ajaxlookup.js
-->
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
