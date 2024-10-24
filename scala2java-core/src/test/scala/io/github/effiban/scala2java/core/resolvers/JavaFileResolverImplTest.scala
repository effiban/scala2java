package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.collectors.SourceCollector
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import java.nio.file.{Files, Paths}
import scala.meta.{Name, Source, Type, XtensionQuasiquoteTerm}

class JavaFileResolverImplTest extends UnitTestSuite {

  private val Name1A = "Name1A"
  private val Name1B = "Name1B"
  private val Name2 = "Name2"

  private val TheSource = Source(List(q"class MyClass"))

  private val javaTopLevelTypeNameCollector: SourceCollector[Name] = mock[SourceCollector[Name]]

  test("resolve() when there are two top-level Java types, and the first name is different than the Scala file name") {

    val scalaBaseDir = Files.createTempDirectory("scalaBaseDir").toFile
    scalaBaseDir.deleteOnExit()

    val outputJavaBaseDir = Files.createTempDirectory("outputJavaBaseDir").toFile
    outputJavaBaseDir.deleteOnExit()

    val scalaFile = Paths.get(scalaBaseDir.getAbsolutePath, s"$Name1A.scala").toFile
    scalaFile.createNewFile()
    scalaFile.deleteOnExit()

    val expectedJavaAbsolutePath = Paths.get(outputJavaBaseDir.getAbsolutePath, s"$Name1B.java").toFile.getAbsolutePath

    when(javaTopLevelTypeNameCollector.collect(eqTree(TheSource))).thenReturn(List(Type.Name(Name1B), Type.Name(Name2)))

    val javaFileResolver = new JavaFileResolverImpl(javaTopLevelTypeNameCollector)
    val file = javaFileResolver.resolve(scalaFile.toPath, TheSource, outputJavaBaseDir.toPath)
    file.getAbsolutePath shouldBe expectedJavaAbsolutePath
  }

  test("resolve() when there is one top-level Java type, and its name is different than the Scala file name") {

    val scalaBaseDir = Files.createTempDirectory("scalaBaseDir").toFile
    scalaBaseDir.deleteOnExit()

    val outputJavaBaseDir = Files.createTempDirectory("outputJavaBaseDir").toFile
    outputJavaBaseDir.deleteOnExit()

    val scalaFile = Paths.get(scalaBaseDir.getAbsolutePath, s"$Name1A.scala").toFile
    scalaFile.createNewFile()
    scalaFile.deleteOnExit()

    val expectedJavaAbsolutePath = Paths.get(outputJavaBaseDir.getAbsolutePath, s"$Name2.java").toFile.getAbsolutePath

    when(javaTopLevelTypeNameCollector.collect(eqTree(TheSource))).thenReturn(List(Type.Name(Name2)))

    val javaFileResolver = new JavaFileResolverImpl(javaTopLevelTypeNameCollector)
    val file = javaFileResolver.resolve(scalaFile.toPath, TheSource, outputJavaBaseDir.toPath)
    file.getAbsolutePath shouldBe expectedJavaAbsolutePath
  }

  test("resolve() when there is one top-level Java type, and its name is the same as the Scala file name") {

    val scalaBaseDir = Files.createTempDirectory("scalaBaseDir").toFile
    scalaBaseDir.deleteOnExit()

    val outputJavaBaseDir = Files.createTempDirectory("outputJavaBaseDir").toFile
    outputJavaBaseDir.deleteOnExit()

    val scalaFile = Paths.get(scalaBaseDir.getAbsolutePath, s"$Name1A.scala").toFile
    scalaFile.createNewFile()
    scalaFile.deleteOnExit()

    val expectedJavaAbsolutePath = Paths.get(outputJavaBaseDir.getAbsolutePath, s"$Name1A.java").toFile.getAbsolutePath

    when(javaTopLevelTypeNameCollector.collect(eqTree(TheSource))).thenReturn(List(Type.Name(Name1A)))

    val javaFileResolver = new JavaFileResolverImpl(javaTopLevelTypeNameCollector)
    val file = javaFileResolver.resolve(scalaFile.toPath, TheSource, outputJavaBaseDir.toPath)
    file.getAbsolutePath shouldBe expectedJavaAbsolutePath
  }

  test("resolve() when there is no top-level Java type") {

    val scalaBaseDir = Files.createTempDirectory("scalaBaseDir").toFile
    scalaBaseDir.deleteOnExit()

    val outputJavaBaseDir = Files.createTempDirectory("outputJavaBaseDir").toFile
    outputJavaBaseDir.deleteOnExit()

    val scalaFile = Paths.get(scalaBaseDir.getAbsolutePath, s"$Name1A.scala").toFile
    scalaFile.createNewFile()
    scalaFile.deleteOnExit()

    val expectedJavaAbsolutePath = Paths.get(outputJavaBaseDir.getAbsolutePath, s"$Name1A.java").toFile.getAbsolutePath

    when(javaTopLevelTypeNameCollector.collect(eqTree(TheSource))).thenReturn(Nil)

    val javaFileResolver = new JavaFileResolverImpl(javaTopLevelTypeNameCollector)
    val file = javaFileResolver.resolve(scalaFile.toPath, TheSource, outputJavaBaseDir.toPath)
    file.getAbsolutePath shouldBe expectedJavaAbsolutePath
  }
}
