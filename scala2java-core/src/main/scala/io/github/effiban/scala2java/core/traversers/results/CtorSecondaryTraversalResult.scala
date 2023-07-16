package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.{Ctor, Type}

case class CtorSecondaryTraversalResult(tree: Ctor.Secondary,
                                        className: Type.Name,
                                        javaModifiers: List[JavaModifier] = Nil) extends StatWithJavaModifiersTraversalResult
