package io.github.effiban.scala2java.core.integrationtest.matchers

import io.github.effiban.scala2java.core.integrationtest.matchers.FileMatchers.SameFileContentsMessage
import org.scalatest.matchers.{MatchResult, Matcher}

import java.nio.file.Path

trait FileMatchers {

  class FileContentsEqualMatcher(expectedPath: Path) extends Matcher[Path] {

    override def apply(actualPath: Path): MatchResult = {
      val maybeFileMismatchMessage = FileMismatchMessageGenerator.generate(actualPath, expectedPath)

      MatchResult(matches = maybeFileMismatchMessage.isEmpty,
        rawFailureMessage = maybeFileMismatchMessage.getOrElse(""),
        rawNegatedFailureMessage = SameFileContentsMessage)
    }
  }
}

object FileMatchers extends FileMatchers {
  final val SameFileContentsMessage = "Actual and expected files have the same contents"

  def equalContentsOf(actualPath: Path): Matcher[Path] = new FileContentsEqualMatcher(actualPath)
}