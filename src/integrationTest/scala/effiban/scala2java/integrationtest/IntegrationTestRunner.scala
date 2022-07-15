package effiban.scala2java.integrationtest

import effiban.scala2java.Scala2JavaTranslator.translate
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, OptionValues}

import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters._
import scala.jdk.StreamConverters._
import scala.util.Using

class IntegrationTestRunner extends AnyFunSuite
  with Matchers
  with OptionValues
  with BeforeAndAfterAll {

  private val scalaBasePath = pathOfResource("scala")
  private val expectedJavaBasePath = pathOfResource("expectedjava")
  private var outputJavaBasePath: Path = _

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

  Using(Files.walk(scalaBasePath)) { stream =>
    stream.toScala(LazyList)
      .filterNot(Files.isDirectory(_))
      .foreach(test)
  }

  private def test(scalaPath: Path): Unit = {
    val scalaFileName = scalaPath.getFileName.toString
    test(s"translate $scalaFileName to Java") {
      val relativePath = scalaBasePath.relativize(scalaPath.getParent)
      val javaFileName = scalaFileName.replace("scala", "java")
      val expectedJavaPath = pathOf(expectedJavaBasePath, relativePath, javaFileName)
      val outputJavaPath = pathOf(outputJavaBasePath, relativePath, javaFileName)

      translate(scalaPath, Some(outputJavaPath.getParent))

      withClue(s"Output java file not found at $outputJavaPath") {
        outputJavaPath.toFile.exists() shouldBe true
      }
      verifyJavaFileContents(outputJavaPath, expectedJavaPath)
    }
  }

  private def pathOfResource(resourceName: String) = Paths.get(getClass.getClassLoader.getResource(resourceName).toURI)
  private def pathOf(basePath: Path, relativePath: Path, fileName: String) = Paths.get(basePath.toString, relativePath.toString, fileName)

  private def verifyJavaFileContents(outputJavaPath: Path, expectedJavaPath: Path): Unit = {
    val actualLines = Files.readAllLines(outputJavaPath).asScala
    val expectedLines = Files.readAllLines(expectedJavaPath).asScala

    withClue("Generated Java file has incorrect number of lines: ") {
      actualLines.size shouldBe expectedLines.size
    }

    actualLines.indices.foreach(lineNum => {
      withClue(s"Generated Java code doesn't match expected at line ${lineNum + 1}: ") {
        actualLines(lineNum) shouldBe expectedLines(lineNum)
      }
    })
  }
}
