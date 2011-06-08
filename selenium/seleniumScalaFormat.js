/*
 * Format for Selenium Remote Control Java client.
 */

var subScriptLoader = Components.classes["@mozilla.org/moz/jssubscript-loader;1"].getService(Components.interfaces.mozIJSSubScriptLoader);
subScriptLoader.loadSubScript('chrome://selenium-ide/content/formats/remoteControl.js', this);

this.name = "scala-specs2";

function useSeparateEqualsForArray() {
    return false;
}

function testMethodName(testName) {
    return "test" + capitalize(testName);
}

function assertTrue(expression) {
    return expression.toString()+" must beTrue";
}

function verifyTrue(expression) {
    return "verifyTrue(" + expression.toString() + ");";
}

function assertFalse(expression) {
    return expression.toString() + "must beFalse";
}

function verifyFalse(expression) {
    return "verifyFalse(" + expression.toString() + ");";
}

function assignToVariable(type, variable, expression) {
    return "val " + variable + " = " + expression.toString();
}

function ifCondition(expression, callback) {
    return "if (" + expression.toString() + ") {\n" + callback() + "}";
}

function joinExpression(expression) {
    return expression.toString() + ".mkString(',')";
}

function waitFor(expression) {
    return "waitFor("+expression.setup+","+expression.toString+")"
}

function assertOrVerifyFailure(line, isAssert) {
  return "throwA[Throwable] {"+line+"}";
}

Equals.prototype.toString = function() {
        return this.e1.toString() + " == " + this.e2.toString();
};

Equals.prototype.assert = function() {
    return  this.e1.toString() + " must_== " + this.e2.toString();
};

Equals.prototype.verify = function() {
    return "verifyEquals(" + this.e1.toString() + ", " + this.e2.toString() + ");";
};

NotEquals.prototype.toString = function() {
    return this.e1.toString() + " != " + this.e2.toString() ;
};

NotEquals.prototype.assert = function() {
    return this.e1.toString() + " must_!= " + this.e2.toString();
};

NotEquals.prototype.verify = function() {
    return "verifyNotEquals(" + this.e1.toString() + ", " + this.e2.toString() + ");";
};

RegexpMatch.prototype.toString = function() {
  return this.expression + " =~ \"\"\""" + this.pattern + "\"\"\"";
};

function pause(milliseconds) {
    return "Thread.sleep(" + parseInt(milliseconds, 10) + ");";
}

function echo(message) {
    return "println(" + xlateArgument(message) + ");";
}

function statement(expression) {
    return expression.toString();
}

function array(value) {
    var str = 'Array(';
    for (var i = 0; i < value.length; i++) {
        str += string(value[i]);
        if (i < value.length - 1) str += ", ";
    }
    str += ')';
    return str;
}

function nonBreakingSpace() {
    return "\"\\u00a0\"";
}

CallSelenium.prototype.toString = function() {
    var result = '';
    if (this.negative) {
        result += '!';
    }
    if (options.receiver) {
        result += options.receiver + '.';
    }
    result += this.message;
    result += '(';
    for (var i = 0; i < this.args.length; i++) {
        result += this.args[i];
        if (i < this.args.length - 1) {
            result += ', ';
        }
    }
    result += ')';
    return result;
};

function formatComment(comment) {
    return comment.comment.replace(/.+/mg, function(str) {
            return "// " + str;
        });
}

/**
 * Returns a string representing the suite for this formatter language.
 *
 * @param testSuite  the suite to format
 * @param filename   the file the formatted suite will be saved as
 */
function formatSuite(testSuite, filename) {
    var suiteClass = /^(\w+)/.exec(filename)[1];
    suiteClass = suiteClass[0].toUpperCase() + suiteClass.substring(1);
    
    var formattedSuite = "import junit.framework.Test;\n"
        + "import junit.framework.TestSuite;\n"
        + "\n"
        + "public class " + suiteClass + " {\n"
        + "\n"
        + indents(1) + "public static Test suite() {\n"
        + indents(2) + "TestSuite suite = new TestSuite();\n";
        
    for (var i = 0; i < testSuite.tests.length; ++i) {
        var testClass = testSuite.tests[i].getTitle();
        formattedSuite += indents(2)
            + "suite.addTestSuite(" + testClass + ".class);\n";
    }

    formattedSuite += indents(2) + "return suite;\n"
        + indents(1) + "}\n"
        + "\n"
        + indents(1) + "public static void main(String[] args) {\n"
        + indents(2) + "junit.textui.TestRunner.run(suite());\n"
        + indents(1) + "}\n"
        + "}\n";
    
    return formattedSuite;
}

this.options = {
    receiver: "selenium",
    environment: "*chrome",
    packageName: "com.example.tests",
    superClass: "SeleneseTestCase",
    indent: 'tab',
    initialIndents: '2'
};

options.header =
    "package ${packageName};\n" +
    "\n" +
    "import org.openqa.selenium.firefox.FirefoxDriver;\n" +
    "import org.openqa.selenium.WebDriverBackedSelenium;\n" +
    "import org.junit.After;\n" +
    "import org.junit.Before;\n" +
    "import org.junit.Test;\n" +
    "import java.util.regex.Pattern;\n" +
    "\n" +
    "public class ${className} extends ${superClass} {\n" + 
    indents(1) + "@Before\n" +
    indents(1) + "public void setUp() throws Exception {\n" +
    indents(2) + "WebDriver driver = new FirefoxDriver();\n" +
    indents(2) + 'String baseUrl = "${baseURL}";\n' +
    indents(2) + 'Selenium selenium = new WebDriverBackedSelenium(driver, baseUrl);\n' +
    indents(2) + "selenium.start();\n" +
    indents(1) + "}\n" +
    "\n" +
    indents(1) + "@Test\n" +
    indents(1) + "public void ${methodName}() throws Exception {\n";

options.footer =
    indents(1) + "}\n" +
    "\n" +
    indents(1) + "@After\n" +
    indents(1) + "public void tearDown() throws Exception {\n" +
    indents(2) + "selenium.stop();\n" +
    indents(1) + "}\n" +
    "}\n";

this.configForm = 
    '<description>Variable for Selenium instance</description>' +
    '<textbox id="options_receiver" />' +
    '<description>Environment</description>' +
    '<textbox id="options_environment" />' +
    '<description>Package</description>' +
    '<textbox id="options_packageName" />' +
    '<description>Superclass</description>' +
    '<textbox id="options_superClass" />';