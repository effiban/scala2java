package io.github.effiban.scala2java.core.integrationtest

import io.github.effiban.scala2java.test.utils.integration.runner.IntegrationTestRunner
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.{Path, Paths}

class CoreIntegrationTestRunner extends AnyFunSuite with IntegrationTestRunner {

  override protected def resolveTestFilesBasePath(): Path = Paths.get(getClass.getClassLoader.getResource("testfiles").toURI)
  override protected def resolveSelectedTestPath(): String = Option(System.getenv("INTEGRATION_TEST_PATH")).getOrElse("")
}
