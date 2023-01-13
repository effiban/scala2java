package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.collectors.SourceCollector
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.FileNameTransformer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import java.nio.file.{Files, Paths}
import scala.meta.{Init, Source, XtensionQuasiquoteInit, XtensionQuasiquoteTerm}

class JavaFileResolverImplTest extends UnitTestSuite {

  private val InitialClassName = "MyInitialClass"
  private val FinalClassName = "MyFinalClass"

  private val MainClassInits = List(init"MyParent1()", init"MyParent2()")
  private val TheSource = Source(List(q"class MyClass"))

  private implicit val fileNameTransformer: FileNameTransformer = mock[FileNameTransformer]
  private val mainClassInitCollector: SourceCollector[Init] = mock[SourceCollector[Init]]

  test("resolve() when has a main class with inits") {

    val scalaBaseDir = Files.createTempDirectory("scalaBaseDir").toFile
    scalaBaseDir.deleteOnExit()

    val outputJavaBaseDir = Files.createTempDirectory("outputJavaBaseDir").toFile
    outputJavaBaseDir.deleteOnExit()

    val scalaFile = Paths.get(scalaBaseDir.getAbsolutePath, s"$InitialClassName.scala").toFile
    scalaFile.createNewFile()
    scalaFile.deleteOnExit()

    val expectedJavaAbsolutePath = Paths.get(outputJavaBaseDir.getAbsolutePath, s"$FinalClassName.java").toFile.getAbsolutePath

    when(mainClassInitCollector.collect(eqTree(TheSource))).thenReturn(MainClassInits)
    when(fileNameTransformer.transform(eqTo(InitialClassName), eqTreeList(MainClassInits))).thenReturn(FinalClassName)

    val javaFileResolver = new JavaFileResolverImpl(mainClassInitCollector)
    val file = javaFileResolver.resolve(scalaFile.toPath, TheSource, outputJavaBaseDir.toPath)
    file.getAbsolutePath shouldBe expectedJavaAbsolutePath
  }

}
