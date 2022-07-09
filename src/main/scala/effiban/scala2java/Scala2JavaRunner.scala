package effiban.scala2java

import java.io.File
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

    val scalaFiles = scalaFilePaths.map(pathStr => Paths.get(pathStr).toFile)
    scalaFiles.foreach(scalaFile => Scala2JavaTranslator.translate(scalaFile, maybeJavaOutputDir.map(new File(_))))
  }
}




