package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.spi.transformers.ClassNameTransformer

import java.io.File
import java.nio.file.{Path, Paths}
import scala.meta.Type

trait JavaFileResolver {

  def resolve(scalaPath: Path, outputJavaBasePath: Path): File
}

class JavaFileResolverImpl(implicit classNameTransformer: ClassNameTransformer) extends JavaFileResolver {

  override def resolve(scalaPath: Path, outputJavaBasePath: Path): File = {
    val baseFileName = scalaPath.getFileName.toString.stripSuffix(".scala")
    val outputBaseFileName = classNameTransformer.transform(Type.Name(baseFileName)).value
    outputJavaBasePath.toFile.mkdirs()
    val javaFileName = s"$outputBaseFileName.java"
    Paths.get(outputJavaBasePath.toString, javaFileName).toFile
  }
}
