package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn
import scala.meta.Defn.Trait

trait DefnTraverser extends ScalaTreeTraverser[Defn]

private[traversers] class DefnTraverserImpl(defnValTraverser: => DefnValTraverser,
                                            defnVarTraverser: => DefnVarTraverser,
                                            defnDefTraverser: => DefnDefTraverser,
                                            defnTypeTraverser: => DefnTypeTraverser,
                                            classTraverser: => ClassTraverser,
                                            traitTraverser: => TraitTraverser,
                                            objectTraverser: => ObjectTraverser)
                                           (implicit javaWriter: JavaWriter) extends DefnTraverser {

  import javaWriter._

  override def traverse(defn: Defn): Unit = defn match {
    case valDef: Defn.Val => defnValTraverser.traverse(valDef)
    case varDef: Defn.Var => defnVarTraverser.traverse(varDef)
    case defDef: Defn.Def => defnDefTraverser.traverse(defDef)
    case typeDef: Defn.Type => defnTypeTraverser.traverse(typeDef)
    case classDef: Defn.Class => classTraverser.traverse(classDef)
    case traitDef: Trait => traitTraverser.traverse(traitDef)
    case objectDef: Defn.Object => objectTraverser.traverse(objectDef)
    case _ => writeComment(s"UNSUPPORTED: $defn")
  }
}
