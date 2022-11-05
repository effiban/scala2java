package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.Decision.{Decision, No}

case class CatchHandlerContext(override val shouldReturnValue: Decision = No) extends ShouldReturnValueIndicator
