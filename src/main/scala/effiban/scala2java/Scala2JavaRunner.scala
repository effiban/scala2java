package effiban.scala2java

import java.nio.file.Paths

object Scala2JavaRunner {

  def main(args: Array[String]): Unit = {
    val sourceFilePaths = args
    val sourceFiles = sourceFilePaths.map(pathStr => Paths.get(pathStr).toFile).toList
    Scala2JavaTranslator.translate(sourceFiles)
  }
}




