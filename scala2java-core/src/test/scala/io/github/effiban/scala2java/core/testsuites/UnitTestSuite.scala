package io.github.effiban.scala2java.core.testsuites

import io.github.effiban.scala2java.core.testwriters.TestJavaWriter
import io.github.effiban.scala2java.core.writers.JavaWriter
import org.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{BeforeAndAfterEach, OneInstancePerTest, OptionValues, TryValues}

import java.io.StringWriter

class UnitTestSuite extends AnyFunSuite
  with MockitoSugar
  with Matchers
  with OptionValues
  with TryValues
  with OneInstancePerTest
  with BeforeAndAfterEach
  with TableDrivenPropertyChecks {

  implicit protected val outputWriter: StringWriter = new StringWriter()
  implicit protected val javaWriter: JavaWriter = new TestJavaWriter(outputWriter)
}
