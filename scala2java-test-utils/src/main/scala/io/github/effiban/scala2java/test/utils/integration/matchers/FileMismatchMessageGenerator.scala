package io.github.effiban.scala2java.test.utils.integration.matchers

import java.nio.file.{Files, Path}
import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.math.{floor, log10, max, min}

private[matchers] object FileMismatchMessageGenerator {

  final val Divider = " |"
  final val Highlight = "~"
  final val Missing = "<MISSING>"
  final val Expected = "EXPECTED"
  final val Actual = "ACTUAL"

  def generate(expectedPath: Path, actualPath: Path): Option[String] = {
    val expectedLines = Files.readAllLines(expectedPath).asScala.toSeq
    val actualLines = Files.readAllLines(actualPath).asScala.toSeq

    findMismatchingLineNum(expectedLines, actualLines)
      .map(mismatchingLineNum => generate(expectedLines, actualLines, mismatchingLineNum))
  }

  private def generate(expectedLines: Seq[String], actualLines: Seq[String], mismatchingLineNum: Int): String = {
    val maxNumLines = max(actualLines.size, expectedLines.size)
    val maxLineNumDigits = numDigitsOf(maxNumLines)
    val maxExpectedLineLength = maxOrZero(expectedLines)
    val maxActualLineLength = maxOrZero(actualLines)
    val maxRowLength = maxLineNumDigits + maxExpectedLineLength + maxActualLineLength + (2 * Divider.length)

    val diffBuilder = new mutable.StringBuilder(500)
    diffBuilder ++= "\n"
    diffBuilder ++= s"Actual file did not match expected file at line ${mismatchingLineNum + 1}:\n"
    diffBuilder ++= "\n"
    diffBuilder ++= generateHeader(maxLineNumDigits, maxExpectedLineLength)

    (0 until maxNumLines).foreach(lineNum => {
      if (mismatchingLineNum == lineNum - 1 || mismatchingLineNum == lineNum) {
        diffBuilder ++= generateHighlightedRow(maxRowLength)
      }
      diffBuilder ++= generateLineNumber(lineNum = lineNum, maxLineNumDigits = maxLineNumDigits)
      diffBuilder ++= Divider
      diffBuilder ++= generateExpectedLinePart(expectedLines, lineNum = lineNum, maxExpectedLineLength = maxExpectedLineLength)
      diffBuilder ++= Divider
      diffBuilder ++= generateActualLinePart(actualLines, lineNum)
      diffBuilder ++= "\n"
    })
    if (mismatchingLineNum == maxNumLines - 1) {
      diffBuilder ++= generateHighlightedRow(maxRowLength)
    }
    diffBuilder.toString()
  }

  private def generateHeader(maxLineNumDigits: Int, maxExpectedLineLength: Int) = {
    (" " * maxLineNumDigits) + Divider + Expected + (" " * (maxExpectedLineLength - Expected.length)) + Divider + Actual + "\n"
  }

  private def generateHighlightedRow(maxRowLength: Int) = (Highlight * maxRowLength) + "\n"

  private def generateLineNumber(lineNum: Int, maxLineNumDigits: Int) = {
    (" " * (maxLineNumDigits - numDigitsOf(lineNum + 1))) + (lineNum + 1)
  }


  private def generateExpectedLinePart(expectedLines: Seq[String], lineNum: Int, maxExpectedLineLength: Int) = {
    if (lineNum < expectedLines.size) {
      expectedLines(lineNum) + (" " * (maxExpectedLineLength - expectedLines(lineNum).length))
    } else {
      Missing + (" " * (maxExpectedLineLength - Missing.length))
    }
  }

  private def generateActualLinePart(actualLines: Seq[String], lineNum: Int) = {
    if (lineNum < actualLines.size) {
      actualLines(lineNum)
    } else {
      Missing
    }
  }

  private def findMismatchingLineNum(expectedLines: Seq[String], actualLines: Seq[String]) = {
    val minNumLines = min(expectedLines.size, actualLines.size)

    val maybeMismatchingLineNum = (0 until minNumLines)
      .find(lineNum => expectedLines(lineNum).trim != actualLines(lineNum).trim)

    maybeMismatchingLineNum match {
      case Some(mismatchingLineNum) => Some(mismatchingLineNum)
      case None if expectedLines.size != actualLines.size => Some(minNumLines)
      case _ => None
    }
  }

  private def numDigitsOf(num: Int) = floor(log10(num)).toInt + 1

  private def maxOrZero(expectedLines: Seq[String]) = {
    if (expectedLines.isEmpty) 0 else expectedLines.map(_.length).max
  }
}

