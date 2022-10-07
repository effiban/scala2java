package effiban.scala2java

import effiban.scala2java.Scala2JavaTranslator.translate

import java.nio.file.{Path, Paths}
import scala.Console.err
import scala.sys.exit

object Scala2JavaRunner {

  private val JavaOutputDirRegex = "--outDir=(.+)".r
  private val StyledCommentLine = "=" * 80

  def main(args: Array[String]): Unit = {

    val (maybeJavaOutputDir, scalaFilePaths) = args.toList match {
      case Nil => (None, Nil)
      case JavaOutputDirRegex(javaOutputDir) :: scFilePaths => (Some(javaOutputDir), scFilePaths)
      case scFilePaths => (None, scFilePaths)
    }

    if (scalaFilePaths.nonEmpty) {
      translateAll(maybeJavaOutputDir, scalaFilePaths)
    } else {
      printUsage()
    }
  }

  private def translateAll(maybeJavaOutputDir: Option[String], scalaFilePaths: List[String]): Unit = {
    val scalaPaths = scalaFilePaths.map(Paths.get(_))
    maybeJavaOutputDir match {
      case Some(javaOutputDir) =>
        scalaPaths.foreach(scalaFile => translate(scalaFile, Some(Paths.get(javaOutputDir))))
        println(s"Successfully generated ${scalaPaths.length} Java file(s) in output dir $javaOutputDir")
      case None =>
        scalaPaths.foreach(scalaFile => {
          printConsoleFileHeader(scalaFile)
          translate(scalaFile)
        })
    }
  }


  private def printConsoleFileHeader(scalaFile: Path): Unit = {
    println(
      s"""|
          |$StyledCommentLine
          |   Translation of Scala file: $scalaFile
          |$StyledCommentLine
          |
          |""".stripMargin
    )
  }

  private def printUsage(): Unit = {
    err.println(
      """Usage: [--outDir=JAVA_OUTPUT_DIR] SCALA_FILE_1 [SCALA_FILE_2 ...]
        |
        |   - If a Java output dir is specified, the translated files will be created there.
        |   - If no Java output dir is specified, the translated files will be printed to the standard output.
        |""".stripMargin)
    exit(1)
  }
}




