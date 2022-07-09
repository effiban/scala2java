package effiban.scala2java

import effiban.scala2java.traversers.ScalaTreeTraversers
import effiban.scala2java.writers.{ConsoleJavaWriter, JavaWriter, JavaWriterImpl}

import java.io.{File, FileWriter}
import java.nio.file.{Files, Paths}
import scala.meta.Source
import scala.meta.inputs.Input

object Scala2JavaTranslator {

  def translate(scalaFile: File, maybeOutputDir: Option[File] = None): Unit = {
    val text = Files.readString(scalaFile.toPath)
    val input = Input.VirtualFile(scalaFile.getAbsolutePath, text)
    val sourceTree = input.parse[Source].get

    implicit val javaWriter: JavaWriter = maybeOutputDir match {
      case Some(outputDir) => createFileJavaWriter(scalaFile, outputDir)
      case None => ConsoleJavaWriter
    }
    try {
      new ScalaTreeTraversers().sourceTraverser.traverse(sourceTree)
    } finally {
      javaWriter.close()
    }
  }

  private def createFileJavaWriter(scalaFile: File, outputDir: File) = {
    outputDir.mkdirs()
    val javaFileName = scalaFile.getName.replace("scala", "java")
    val javaFile = Paths.get(outputDir.getAbsolutePath, javaFileName).toFile
    new JavaWriterImpl(new FileWriter(javaFile))
  }
}




