package c2c.webspecs.geonetwork

import c2c.webspecs.{XmlValueFactory, AbstractGetRequest}


/**
 * Perform a metadata.batch.delete request
 *
 * User: jeichar
 * Date: 1/19/12
 * Time: 10:36 AM
 */
case object MetadataBatchDelete extends AbstractGetRequest("metadata.batch.delete", XmlValueFactory)