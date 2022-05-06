package com.effiban.scala2java

import org.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, OneInstancePerTest}

import java.io.StringWriter

class UnitTestSuite extends AnyFunSuite
  with MockitoSugar
  with Matchers
  with OneInstancePerTest
  with BeforeAndAfterEach {

  protected val outputWriter: StringWriter = new StringWriter()
  implicit protected val javaEmitter: JavaEmitter = new TestJavaEmitter(outputWriter)
}
