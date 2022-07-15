package effiban.scala2java

import effiban.scala2java.traversers.ScalaTreeTraversers
import effiban.scala2java.writers.{ConsoleJavaWriter, JavaWriter, JavaWriterImpl}

import java.io.FileWriter
import java.nio.file.{Files, Path, Paths}
import scala.meta.Source
import scala.meta.inputs.Input

object Scala2JavaTranslator {

  def translate(scalaPath: Path, maybeOutputJavaBasePath: Option[Path] = None): Unit = {
    val scalaText = Files.readString(scalaPath)
    val scalaFileName = scalaPath.getFileName.toString
    val input = Input.VirtualFile(scalaFileName, scalaText)
    val sourceTree = input.parse[Source].get

    implicit val javaWriter: JavaWriter = maybeOutputJavaBasePath match {
      case Some(outputJavaBasePath) => createFileJavaWriter(scalaFileName, outputJavaBasePath)
      case None => ConsoleJavaWriter
    }
    try {
      new ScalaTreeTraversers().sourceTraverser.traverse(sourceTree)
    } finally {
      javaWriter.close()
    }
  }

  private def createFileJavaWriter(scalaFileName: String, outputJavaBasePath: Path) = {
    outputJavaBasePath.toFile.mkdirs()
    val javaFileName = scalaFileName.replace("scala", "java")
    val javaFile = Paths.get(outputJavaBasePath.toString, javaFileName).toFile
    new JavaWriterImpl(new FileWriter(javaFile))
  }
}




