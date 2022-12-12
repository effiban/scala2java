package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Decl, Defn}

trait DefnValToDeclVarTransformer {

  def transform(defnVal: Defn.Val, javaScope: JavaScope): Option[Decl.Var]
}

object DefnValToDeclVarTransformer {
  def Empty: DefnValToDeclVarTransformer = (_, _) => None
}
