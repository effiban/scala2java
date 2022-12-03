package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.FileNameTransformer

import java.nio.file.{Files, Paths}

class JavaFileResolverImplTest extends UnitTestSuite {

  private val InitialClassName = "MyInitialClass"
  private val FinalClassName = "MyFinalClass"

  private implicit val fileNameTransformer: FileNameTransformer = mock[FileNameTransformer]

  test("resolve") {

    val scalaBaseDir = Files.createTempDirectory("scalaBaseDir").toFile
    scalaBaseDir.deleteOnExit()

    val outputJavaBaseDir = Files.createTempDirectory("outputJavaBaseDir").toFile
    outputJavaBaseDir.deleteOnExit()

    val scalaFile = Paths.get(scalaBaseDir.getAbsolutePath, s"$InitialClassName.scala").toFile
    scalaFile.createNewFile()
    scalaFile.deleteOnExit()

    val expectedJavaAbsolutePath = Paths.get(outputJavaBaseDir.getAbsolutePath, s"$FinalClassName.java").toFile.getAbsolutePath

    when(fileNameTransformer.transform(InitialClassName)).thenReturn(FinalClassName)

    new JavaFileResolverImpl().resolve(scalaFile.toPath, outputJavaBaseDir.toPath).getAbsolutePath shouldBe expectedJavaAbsolutePath
  }

}
