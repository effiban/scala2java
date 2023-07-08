package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.Decision.{Decision, No}

// TODO - when expecting a return value which is a lambda, need another flag for returnability inside the lambda body
case class BlockContext(override val shouldReturnValue: Decision = No) extends ShouldReturnValueIndicator
