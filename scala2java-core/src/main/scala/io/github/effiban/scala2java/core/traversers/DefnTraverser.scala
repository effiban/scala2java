package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ClassOrTraitContext, DefnDefContext, StatContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn
import scala.meta.Defn.Trait

trait DefnTraverser {
  def traverse(defn: Defn, context: StatContext = StatContext()): Unit
}

private[traversers] class DefnTraverserImpl(defnValTraverser: => DeprecatedDefnValTraverser,
                                            defnVarTraverser: => DeprecatedDefnVarTraverser,
                                            defnDefTraverser: => DefnDefTraverser,
                                            defnTypeTraverser: => DefnTypeTraverser,
                                            classTraverser: => ClassTraverser,
                                            traitTraverser: => TraitTraverser,
                                            objectTraverser: => ObjectTraverser)
                                           (implicit javaWriter: JavaWriter) extends DefnTraverser {

  import javaWriter._

  override def traverse(defn: Defn, context: StatContext = StatContext()): Unit = defn match {
    case valDef: Defn.Val => defnValTraverser.traverse(valDef, context)
    case varDef: Defn.Var => defnVarTraverser.traverse(varDef, context)
    case defDef: Defn.Def => defnDefTraverser.traverse(defDef, DefnDefContext(javaScope = context.javaScope))
    case typeDef: Defn.Type => defnTypeTraverser.traverse(typeDef, context)
    case classDef: Defn.Class => classTraverser.traverse(classDef, ClassOrTraitContext(context.javaScope))
    case traitDef: Trait => traitTraverser.traverse(traitDef, ClassOrTraitContext(context.javaScope))
    case objectDef: Defn.Object => objectTraverser.traverse(objectDef, context)
    case _ => writeComment(s"UNSUPPORTED: $defn")
  }
}
