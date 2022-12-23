package io.github.effiban.scala2java.spi.transformers

import scala.meta.Type

/** A transformer which can modify a given Scala [[Type.Name]] (e.g. name of class/trait) */
trait TypeNameTransformer extends SameTypeTransformer[Type.Name]

object TypeNameTransformer {

  /** The default transformer which returns the type name unchanged, indicating that no transformation is needed */
  def Identity: TypeNameTransformer = identity[Type.Name]
}