package effiban.scala2java

import effiban.scala2java.traversers.ScalaTreeTraversers
import effiban.scala2java.writers.{ConsoleJavaWriter, JavaWriter}

import java.io.File
import java.nio.file.Files
import scala.meta.Source
import scala.meta.inputs.Input

object Scala2JavaTranslator {

  def translate(scalaSourceFile: File): Unit = {
    val text = Files.readString(scalaSourceFile.toPath)
    val input = Input.VirtualFile(scalaSourceFile.getAbsolutePath, text)
    val sourceTree = input.parse[Source].get

    implicit val javaWriter: JavaWriter = ConsoleJavaWriter
    val traversers = new ScalaTreeTraversers
    traversers.sourceTraverser.traverse(sourceTree)
  }
}




