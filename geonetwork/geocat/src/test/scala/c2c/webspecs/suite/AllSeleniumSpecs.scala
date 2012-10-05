package c2c.webspecs.suite

import org.specs2.Specification
import org.specs2.runner.SpecificationsFinder
import c2c.webspecs.geonetwork.geocat.spec.WP7.selenium._
import c2c.webspecs.geonetwork.geocat.spec.WP10.selenium._
import c2c.webspecs.geonetwork.geocat.spec.WP15.selenium._
import c2c.webspecs.geonetwork.geocat.spec.WP16.selenium._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.specification.Step
import c2c.webspecs.Properties
import org.specs2.main.Arguments

@RunWith(classOf[JUnitRunner])
class AllSeleniumSpecs extends AbstractAllSpecs("Selenium Specifications: All Work Packages",
      classOf[WP7Selenium],
      classOf[WP10Selenium],
      classOf[WP15Selenium],
	  classOf[WP16Selenium])	  

class WP10Selenium extends AbstractAllSpecs("WP 10: Reusable Object Selenium Tests",
      classOf[CreateEditDeleteUserSeleniumSpec],
      classOf[ViewNonValidatedObjectsSeleniumSpec])

class WP7Selenium extends AbstractAllSpecs("WP 7: Geocat Search UI",
      classOf[Bug138810NoNextSearchSpec],
      classOf[SearchesReturnResultsSeleniumSpec],
      classOf[MassiveOpSeleniumSpec],
      classOf[Bug15242FormatListOrderSpec])

class WP15Selenium extends AbstractAllSpecs("WP 15: Metadata Edit",
                classOf[EditContactSpec])

class WP16Selenium extends AbstractAllSpecs("WP 16: Misc. Selenium tests ",
      classOf[SaveConfigurationSpec],
      classOf[AdminChangePasswordSpec])