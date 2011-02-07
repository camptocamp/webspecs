package org.fao.geonet

import java.util.UUID
import xml.Node

class Constants(config:Config) {
  lazy val user = Config.userPrefix+config.specName
  lazy val pass = UUID.randomUUID.toString.takeRight(8)
  lazy val groupId = Config.findGroupIds()(_ contains user) match {
    case ids if ids.isEmpty => throw new IllegalStateException("You must call setUpTestEnv before calling groupId")
    case ids => ids.head
  }
  lazy val userId = Config.findUserIds()(_ contains user) match {
    case ids if ids.isEmpty => throw new IllegalStateException("You must call setUpTestEnv before calling userId")
    case ids => ids.head
  }
  private def sampleTemplates(filter:Node) = CswGetRecordsRequest(filter, maxRecords = 20, resultType = ResultTypes.results) {
      response =>
        Log(Log.Constants, response.xml)
        val ids = response.xml.fold(throw _, _ \\ "info" \ "id")
        assert(ids.nonEmpty, "No template metadata found" )
        ids.toList map {_.text}
    }
  lazy val sampleDataTemplateIds = {
    val isTemplate = PropertyIsEqualTo("_isTemplate","y").xml
    val data = PropertyIsEqualTo("type","dataset").xml
    sampleTemplates(<ogc:And>{isTemplate}{data}</ogc:And>)
  }
  lazy val sampleServiceTemplateIds = {
    val isTemplate = PropertyIsEqualTo("_isTemplate","y").xml
    val service = PropertyIsEqualTo("type","service").xml
    sampleTemplates(<ogc:And>{isTemplate}{service}</ogc:And>)
  }
}
