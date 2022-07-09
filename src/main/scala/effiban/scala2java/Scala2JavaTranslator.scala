package effiban.scala2java

import effiban.scala2java.traversers.ScalaTreeTraversers
import effiban.scala2java.writers.{ConsoleJavaWriter, JavaWriter}

import java.io.File
import java.nio.file.Files
import scala.meta.Source
import scala.meta.inputs.Input

object Scala2JavaTranslator {

  def translate(scalaSourceFiles: List[File]): Unit = {

    val sourceTrees = scalaSourceFiles.map(sourceFile => {
      val text = Files.readString(sourceFile.toPath)
      val input = Input.VirtualFile(sourceFile.getAbsolutePath, text)
      input.parse[Source].get
    })

    implicit val javaWriter: JavaWriter = ConsoleJavaWriter
    val traversers = new ScalaTreeTraversers

    sourceTrees.foreach(traversers.sourceTraverser.traverse)
  }
}




