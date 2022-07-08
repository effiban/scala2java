package effiban.scala2java

import effiban.scala2java.traversers.SourceTraverser

import scala.meta.Source
import scala.meta.inputs.Input

object Scala2JavaRunner {

  def main(args: Array[String]): Unit = {

    val sourceFilePaths = args

    val sourceTrees = sourceFilePaths.map(pathStr => {
      val path = java.nio.file.Paths.get(pathStr)
      val bytes = java.nio.file.Files.readAllBytes(path)
      val text = new String(bytes, "UTF-8")
      val input = Input.VirtualFile(path.toString, text)
      input.parse[Source].get
    })

    sourceTrees.foreach(SourceTraverser.traverse)
  }
}




