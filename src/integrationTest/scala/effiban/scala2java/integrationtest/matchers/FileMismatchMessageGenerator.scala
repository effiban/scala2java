package effiban.scala2java.integrationtest.matchers

import java.nio.file.{Files, Path}
import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.math.{floor, log10, max, min}

private[matchers] object FileMismatchMessageGenerator {

  final val Divider = " |"
  final val Highlight = "~"
  final val Missing = "<MISSING>"
  final val Actual = "ACTUAL"
  final val Expected = "EXPECTED"

  def generate(actualPath: Path, expectedPath: Path): Option[String] = {
    val actualLines = Files.readAllLines(actualPath).asScala.toSeq
    val expectedLines = Files.readAllLines(expectedPath).asScala.toSeq

    findMismatchingLineNum(actualLines, expectedLines)
      .map(mismatchingLineNum => generate(actualLines, expectedLines, mismatchingLineNum))
  }

  private def generate(actualLines: Seq[String], expectedLines: Seq[String], mismatchingLineNum: Int) = {
    val maxNumLines = max(actualLines.size, expectedLines.size)
    val maxLineNumDigits = numDigitsOf(maxNumLines)
    val maxActualLineLength = actualLines.map(_.length).max
    val maxExpectedLineLength = expectedLines.map(_.length).max
    val maxRowLength = maxLineNumDigits + maxActualLineLength + maxExpectedLineLength + (2 * Divider.length)

    val diffBuilder = new mutable.StringBuilder(500)
    diffBuilder ++= "\n"
    diffBuilder ++= s"Actual file did not match expected file at line ${mismatchingLineNum + 1}:\n"
    diffBuilder ++= "\n"
    diffBuilder ++= generateHeader(maxLineNumDigits, maxActualLineLength)

    (0 until maxNumLines).foreach(lineNum => {
      if (mismatchingLineNum == lineNum - 1 || mismatchingLineNum == lineNum) {
        diffBuilder ++= generateHighlightedRow(maxRowLength)
      }
      diffBuilder ++= generateLineNumber(lineNum = lineNum, maxLineNumDigits = maxLineNumDigits)
      diffBuilder ++= Divider
      diffBuilder ++= generateActualLinePart(actualLines, lineNum = lineNum, maxActualLineLength = maxActualLineLength)
      diffBuilder ++= Divider
      diffBuilder ++= generateExpectedLinePart(expectedLines, lineNum)
      diffBuilder ++= "\n"
    })
    if (mismatchingLineNum == maxNumLines - 1) {
      diffBuilder ++= generateHighlightedRow(maxRowLength)
    }
    diffBuilder.toString()
  }

  private def generateHeader(maxLineNumDigits: Int, maxActualLineLength: Int) = {
    (" " * maxLineNumDigits) + Divider + Actual + (" " * (maxActualLineLength - Actual.length)) + Divider + Expected + "\n"
  }

  private def generateHighlightedRow(maxRowLength: Int) = (Highlight * maxRowLength) + "\n"

  private def generateLineNumber(lineNum: Int, maxLineNumDigits: Int) = {
    (" " * (maxLineNumDigits - numDigitsOf(lineNum + 1))) + (lineNum + 1)
  }

  private def generateActualLinePart(actualLines: Seq[String], lineNum: Int, maxActualLineLength: Int) = {
    if (lineNum < actualLines.size) {
      actualLines(lineNum) + (" " * (maxActualLineLength - actualLines(lineNum).length))
    } else {
      Missing + (" " * (maxActualLineLength - Missing.length))
    }
  }

  private def generateExpectedLinePart(expectedLines: Seq[String], lineNum: Int) = {
    if (lineNum < expectedLines.size) {
      expectedLines(lineNum)
    } else {
      Missing
    }
  }

  private def findMismatchingLineNum(actualLines: Seq[String], expectedLines: Seq[String]): Option[Int] = {
    val minNumLines = min(actualLines.size, expectedLines.size)

    val maybeMismatchingLineNum = (0 until minNumLines)
      .find(lineNum => actualLines(lineNum) != expectedLines(lineNum))

    maybeMismatchingLineNum match {
      case Some(mismatchingLineNum) => Some(mismatchingLineNum)
      case None if actualLines.size != expectedLines.size => Some(minNumLines)
      case _ => None
    }
  }

  private def numDigitsOf(num: Int) = floor(log10(num)).toInt + 1
}

