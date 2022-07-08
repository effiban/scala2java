package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Defn
import scala.meta.Defn.Trait

trait DefnTraverser extends ScalaTreeTraverser[Defn]

private[scala2java] class DefnTraverserImpl(defnValTraverser: => DefnValTraverser,
                                            defnVarTraverser: => DefnVarTraverser,
                                            defnDefTraverser: => DefnDefTraverser,
                                            defnTypeTraverser: => DefnTypeTraverser,
                                            classTraverser: => ClassTraverser,
                                            traitTraverser: => TraitTraverser,
                                            objectTraverser: => ObjectTraverser)
                                           (implicit javaEmitter: JavaEmitter) extends DefnTraverser {

  override def traverse(defn: Defn): Unit = defn match {
    case valDef: Defn.Val => defnValTraverser.traverse(valDef)
    case varDef: Defn.Var => defnVarTraverser.traverse(varDef)
    case defDef: Defn.Def => defnDefTraverser.traverse(defDef)
    case typeDef: Defn.Type => defnTypeTraverser.traverse(typeDef)
    case classDef: Defn.Class => classTraverser.traverse(classDef)
    case traitDef: Trait => traitTraverser.traverse(traitDef)
    case objectDef: Defn.Object => objectTraverser.traverse(objectDef)
    case _ => emitComment(s"UNSUPPORTED: $defn")
  }
}

object DefnTraverser extends DefnTraverserImpl(
  DefnValTraverser,
  DefnVarTraverser,
  DefnDefTraverser,
  DefnTypeTraverser,
  ClassTraverser,
  TraitTraverser,
  ObjectTraverser
)