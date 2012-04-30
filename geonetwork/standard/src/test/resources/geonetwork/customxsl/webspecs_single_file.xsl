<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco"
                xmlns:gml="http://www.opengis.net/gml" >

	<xsl:template match="/">
	  <xsl:apply-templates select="root/gmd:MD_Metadata | root/*[@gco:isoType = 'gmd:MD_Metadata']"/>
	</xsl:template>  
	<xsl:template match="gmd:MD_Metadata | *[@gco:isoType = 'gmd:MD_Metadata']" priority="1">
		<s1 v="{/root/strings/home}"/>
	</xsl:template>
</xsl:stylesheet>
