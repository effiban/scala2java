package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.spi.transformers.FileNameTransformer

import java.io.File
import java.nio.file.{Path, Paths}

trait JavaFileResolver {

  def resolve(scalaPath: Path, outputJavaBasePath: Path): File
}

class JavaFileResolverImpl(implicit fileNameTransformer: FileNameTransformer) extends JavaFileResolver {

  override def resolve(scalaPath: Path, outputJavaBasePath: Path): File = {
    val baseFileName = scalaPath.getFileName.toString.stripSuffix(".scala")
    val outputBaseFileName = fileNameTransformer.transform(baseFileName)
    outputJavaBasePath.toFile.mkdirs()
    val javaFileName = s"$outputBaseFileName.java"
    Paths.get(outputJavaBasePath.toString, javaFileName).toFile
  }
}
