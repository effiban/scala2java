package io.github.effiban.scala2java.testsuites

import io.github.effiban.scala2java.testwriters.TestJavaWriter
import io.github.effiban.scala2java.writers.JavaWriter
import org.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{BeforeAndAfterEach, OneInstancePerTest, OptionValues}

import java.io.StringWriter

class UnitTestSuite extends AnyFunSuite
  with MockitoSugar
  with Matchers
  with OptionValues
  with OneInstancePerTest
  with BeforeAndAfterEach
  with TableDrivenPropertyChecks {

  implicit protected val outputWriter: StringWriter = new StringWriter()
  implicit protected val javaWriter: JavaWriter = new TestJavaWriter(outputWriter)
}