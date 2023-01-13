package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.collectors.SourceCollector
import io.github.effiban.scala2java.core.entities.FileExtensions
import io.github.effiban.scala2java.spi.transformers.FileNameTransformer

import java.io.File
import java.nio.file.{Path, Paths}
import scala.meta.{Init, Source}

trait JavaFileResolver {

  def resolve(scalaPath: Path, scalaSource: Source, outputJavaBasePath: Path): File
}

class JavaFileResolverImpl(mainClassInitsCollector: SourceCollector[Init])
                          (implicit fileNameTransformer: FileNameTransformer) extends JavaFileResolver {

  override def resolve(scalaPath: Path, scalaSource: Source, outputJavaBasePath: Path): File = {
    val baseFileName = scalaPath.getFileName.toString.stripSuffix(s".${FileExtensions.Scala}")
    val scalaMainClassInits = mainClassInitsCollector.collect(scalaSource)
    val outputBaseFileName = fileNameTransformer.transform(baseFileName, scalaMainClassInits)
    outputJavaBasePath.toFile.mkdirs()
    val javaFileName = s"$outputBaseFileName.${FileExtensions.Java}"
    Paths.get(outputJavaBasePath.toString, javaFileName).toFile
  }
}
