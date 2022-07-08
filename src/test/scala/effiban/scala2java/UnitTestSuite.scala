package effiban.scala2java

import effiban.scala2java.TraversalContext.javaOwnerContext
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
    javaOwnerContext = NoOwner
  }
}
