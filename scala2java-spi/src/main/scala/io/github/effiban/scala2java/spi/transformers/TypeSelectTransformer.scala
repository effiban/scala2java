package io.github.effiban.scala2java.spi.transformers

import scala.meta.Type

/** A transformer which can modify a given Scala [[Type.Select]] (qualified type) to any Type */
trait TypeSelectTransformer extends DifferentTypeTransformer0[Type.Select, Type]

object TypeSelectTransformer {

  /** The empty transformer which returns None, indicating that no transformation is needed */
  def Empty: TypeSelectTransformer = _ => None
}