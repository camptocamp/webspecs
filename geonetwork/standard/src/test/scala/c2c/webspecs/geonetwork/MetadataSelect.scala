package c2c.webspecs
package geonetwork

/**
 * Select All Metadata
 * User: jeichar
 * Date: 1/19/12
 * Time: 10:32 AM
 */
case object SelectAll extends AbstractGetRequest("metadata.select", XmlValueFactory, SP('selected -> "add-all"))