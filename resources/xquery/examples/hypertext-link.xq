xquery version "1.0";
(:
: This function generates an XHTML <a>---</a> element,
: representing a hyperlink in the XHTML namespace.
:
:)

declare namespace xhtml="http://www.w3.org/1999/xhtml";

declare function local:hyperlink($href as xs:anyURI, $text as xs:string) as element(xhtml:a) {
   <xhtml:a href="{$href}">{$text}</xhtml:a> };

local:hyperlink(xs:anyURI("index.html"), "contents") 
