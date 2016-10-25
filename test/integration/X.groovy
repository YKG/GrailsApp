import grails.util.GrailsWebUtil
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.web.mapping.DefaultUrlMappingEvaluator
import org.codehaus.groovy.grails.web.mapping.DefaultUrlMappingParser
import org.codehaus.groovy.grails.web.mapping.DefaultUrlMappingsHolder
import org.codehaus.groovy.grails.web.mapping.RegexUrlMapping
import org.codehaus.groovy.grails.web.mapping.UrlMapping
import org.codehaus.groovy.grails.web.mapping.UrlMappingEvaluator
import org.codehaus.groovy.grails.web.mapping.exceptions.UrlMappingException
import org.springframework.core.io.ByteArrayResource

/**
 * Created by ykg on 12/10/16.
 */
class X extends AbstractGrailsControllerTests{
    public UrlMappingEvaluator evaluator

    protected void setUp() {
        super.setUp()
        servletContext.setAttribute(GrailsApplication.APPLICATION_ID, ga)
        evaluator = new DefaultUrlMappingEvaluator(servletContext)
    }

    def mappingScript = '''
mappings {
  "/book/$author/$title/$test" {
      controller = "book"
      action = "show"
  }
  "/blog/$entry/$year?/$month?/$day?" {
     controller = "blog"
     action = "show"
  }
  "/surveys/$action?" {
      controller = "survey"
  }
  "/files/$path**?" {
      controller = "files"
  }
  "/filenameext/$fname$fext?" {
      controller = "download"
  }
  "/another/arbitrary/something-$prefix.$ext" {
      controller = "myFiles"
      action = "index"
  }
  "/foo"(controller:"foo", parseRequest:true)

  "/foo2"(controller: "foo") {
       parseRequest = true
   }

  "/foo3" {
       controller = "foo"
       parseRequest = true
   }

  name foo4: "/foo4" {
       controller = "foo"
       parseRequest = true
  }

  "/bar"(uri:"/x/y")

  "/surveys/view/$id" {
      controller = "survey"
      action = "viewById"
      constraints {
         id(matches:/\\d+/)
      }
  }
  "/surveys/view/$name" {
      controller = "survey"
      action = "viewByName"
  }
  "/reports/$foo" {
      controller = 'reporting'
      action = 'view'
  }
  "/ykg/$foo" {
      controller = 'mainCtrl'
      action = 'ykg'
  }
}
'''
    void testMaptoURI() {
        def res = new ByteArrayResource(mappingScript.bytes)
        def mappings = evaluator.evaluateMappings(res)

        def holder = new DefaultUrlMappingsHolder(mappings)

        def info = holder.match("/bar")
        assertEquals "/x/y", info.getURI()
    }

    void testConstraintAsTiebreaker2() {
        // test that two similar rules that only differ by # of constraints are evaluated correctly
        def holder = new DefaultUrlMappingsHolder(evaluator.evaluateMappings(new ByteArrayResource(mappingScript.bytes)))

        def info = holder.match("/surveys/view/1%2523")
        assertNotNull info
        assertEquals 'survey', info.controllerName
        assertEquals 'viewById', info.actionName

        info = holder.match("/surveys/view/foo")
        assertNotNull info
        assertEquals 'survey', info.controllerName
        assertEquals 'viewByName', info.actionName
    }

    void testConstraintAsTiebreaker3() {
        // test that two similar rules that only differ by # of constraints are evaluated correctly
        def holder = new DefaultUrlMappingsHolder(evaluator.evaluateMappings(new ByteArrayResource(mappingScript.bytes)))

        def info = holder.match("/ykg/a%25b")
        assertNotNull info
        assertEquals 'mainCtrl', info.controllerName
        assertEquals 'ykg', info.actionName
    }
}
