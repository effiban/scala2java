package io.github.effiban.scala2java.core.integrationtest

import io.github.effiban.scala2java.test.utils.integration.runner.IntegrationTestRunner
import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import java.nio.file.{Path, Paths}

class CoreIntegrationTestRunner extends AnyFunSuite with IntegrationTestRunner {

  override protected def resolveTestFilesBasePath(): Path = {
    val thisClassFile = new File(getClass.getProtectionDomain.getCodeSource.getLocation.toURI)
    var path = thisClassFile.toPath
    while (path != null && !path.endsWith("build")) {
      path = path.getParent
    }
    if (path == null) {
      throw new IllegalStateException(
        s"""Couldn't find the "build" folder among the ancestors of ${thisClassFile.getPath}""")
    }
    Path.of(path.getParent.toString, "src", "integrationTest", "scala", "testfiles")
  }
  override protected def resolveSelectedTestPath(): String = Option(System.getenv("INTEGRATION_TEST_PATH")).getOrElse("")
}
