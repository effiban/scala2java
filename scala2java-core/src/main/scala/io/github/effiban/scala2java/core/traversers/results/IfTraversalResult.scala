package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Term.If

case class IfTraversalResult(`if`: If, uncertainReturn: Boolean = false)
