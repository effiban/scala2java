package io.github.effiban.scala2java.test.utils.integration.runner

import io.github.effiban.scala2java.core.Scala2JavaTranslator.translate
import io.github.effiban.scala2java.test.utils.integration.matchers.FileMatchers.equalContentsOf
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, OptionValues}

import java.nio.file.{Files, Path, Paths}
import scala.jdk.StreamConverters._
import scala.util.Using

trait IntegrationTestRunner
  extends Matchers
  with OptionValues
  with BeforeAndAfterAll { this: AnyFunSuiteLike =>

  private val testFilesBasePath: Path = resolveTestFilesBasePath()
  private val selectedTestPath: String = resolveSelectedTestPath()

  private var outputJavaBasePath: Path = _

  protected def resolveTestFilesBasePath(): Path

  protected def resolveSelectedTestPath(): String

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    outputJavaBasePath = Files.createTempDirectory("outputjava")
  }

  override protected def afterAll(): Unit = {
    super.afterAll()

    val outputJavaBaseDir = outputJavaBasePath.toFile
    outputJavaBaseDir.listFiles().foreach(_.delete())
    outputJavaBaseDir.delete()
  }

  Using(Files.walk(testFilesBasePath)) { stream =>
    stream.toScala(LazyList)
      .filterNot(Files.isDirectory(_))
      .filter(path => selectedTestPath.isBlank || path.toString.contains(selectedTestPath))
      .filter(_.toString.endsWith(".scala"))
      .foreach(test)
  }

  private def test(scalaPath: Path): Unit = {
    val relativePath = testFilesBasePath.relativize(scalaPath.getParent)
    test(s"translate [$relativePath]") {
      val scalaFileName = scalaPath.getFileName.toString
      val javaFileName = scalaFileName.replace("scala", "java")
      val expectedJavaPath = pathOf(testFilesBasePath, relativePath, javaFileName)
      val outputJavaPath = pathOf(outputJavaBasePath, relativePath, javaFileName)

      translate(scalaPath, Some(outputJavaPath.getParent))

      withClue(s"Output java file not found at $outputJavaPath") {
        outputJavaPath.toFile.exists() shouldBe true
      }
      outputJavaPath should equalContentsOf(expectedJavaPath)
    }
  }

  private def pathOf(basePath: Path, relativePath: Path, fileName: String) = Paths.get(basePath.toString, relativePath.toString, fileName)
}
