package effiban.scala2java.contexts

import effiban.scala2java.entities.Decision.Decision

trait ShouldReturnValueIndicator {
  val shouldReturnValue: Decision
}
