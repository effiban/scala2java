package effiban.scala2java.testsuites

import effiban.scala2java.entities.NoOwner
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.{JavaEmitter, TestJavaEmitter}
import org.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, OneInstancePerTest, OptionValues}

import java.io.StringWriter

class UnitTestSuite extends AnyFunSuite
  with MockitoSugar
  with Matchers
  with OptionValues
  with OneInstancePerTest
  with BeforeAndAfterEach {

  implicit protected val outputWriter: StringWriter = new StringWriter()
  implicit protected val javaEmitter: JavaEmitter = new TestJavaEmitter(outputWriter)

  override def beforeEach(): Unit = {
    super.beforeEach()
    javaScope = NoOwner
  }
}
