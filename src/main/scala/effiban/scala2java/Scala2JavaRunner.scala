package effiban.scala2java

import effiban.scala2java.Scala2JavaTranslator.translate

import java.nio.file.Paths
import scala.Console.err
import scala.sys.exit

object Scala2JavaRunner {

  def main(args: Array[String]): Unit = {
    val (maybeJavaOutputDir, scalaFilePaths) = args.toList match {
      case Nil =>
        err.println("At least one scala file must be provided.")
        exit(-1)
      case scalaFilePath :: Nil => (None, List(scalaFilePath))
      case javaOutputDir :: scalaFilePaths => (Some(javaOutputDir), scalaFilePaths)
    }

    val scalaFiles = scalaFilePaths.map(Paths.get(_))
    scalaFiles.foreach(scalaFile => translate(scalaFile, maybeJavaOutputDir.map(Paths.get(_))))
  }
}




