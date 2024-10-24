package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.collectors.SourceCollector
import io.github.effiban.scala2java.core.entities.FileExtensions

import java.io.File
import java.nio.file.{Path, Paths}
import scala.meta.{Name, Source}

trait JavaFileResolver {

  def resolve(scalaPath: Path, scalaSource: Source, outputJavaBasePath: Path): File
}

class JavaFileResolverImpl(javaTopLevelTypeNameCollector: SourceCollector[Name]) extends JavaFileResolver {

  override def resolve(scalaPath: Path, scalaSource: Source, outputJavaBasePath: Path): File = {
    val baseFileName = scalaPath.getFileName.toString.stripSuffix(s".${FileExtensions.Scala}")
    val javaTopLevelTypeNames = javaTopLevelTypeNameCollector.collect(scalaSource)
    // TODO handle multiple Java types in same file - create a synthetic file name?
    val outputBaseFileName = javaTopLevelTypeNames.headOption
      .map(_.value)
      .getOrElse(baseFileName)
    outputJavaBasePath.toFile.mkdirs()
    val javaFileName = s"$outputBaseFileName.${FileExtensions.Java}"
    Paths.get(outputJavaBasePath.toString, javaFileName).toFile
  }
}
