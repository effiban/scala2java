package io.github.effiban.scala2java.test.utils.integration.matchers

import io.github.effiban.scala2java.test.utils.integration.matchers.FileMatchers.SameFileContentsMessage
import org.scalatest.matchers.{MatchResult, Matcher}

import java.nio.file.Path

/** Scalatest matchers for file and path objects */
trait FileMatchers {

  /**
   * A Scalatest [[Matcher]] which verifies that the contents of the expected file are equal to the actual.<br>
   * If they are not, it will produce a nicely formatted error message with the expected and actual contents side-by-side.
   *
   * @param expectedPath the expected [[Path]]
   */
  class FileContentsEqualMatcher(expectedPath: Path) extends Matcher[Path] {

    override def apply(actualPath: Path): MatchResult = {
      val maybeFileMismatchMessage = FileMismatchMessageGenerator.generate(expectedPath, actualPath)

      MatchResult(matches = maybeFileMismatchMessage.isEmpty,
        rawFailureMessage = maybeFileMismatchMessage.getOrElse(""),
        rawNegatedFailureMessage = SameFileContentsMessage)
    }
  }
}

object FileMatchers extends FileMatchers {
  final val SameFileContentsMessage = "Actual and expected files have the same contents"

  /** Convenience method for asserting with a [[FileContentsEqualMatcher]]
   *
   * @param expectedPath path of the expected file to compare
   * @return a matcher for comparing the contents of the given expected file
   */
  def equalContentsOf(expectedPath: Path): Matcher[Path] = new FileContentsEqualMatcher(expectedPath)
}