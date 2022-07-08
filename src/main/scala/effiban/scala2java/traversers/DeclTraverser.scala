package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter
import effiban.scala2java.writers.JavaWriter.writeComment

import scala.meta.Decl

trait DeclTraverser extends ScalaTreeTraverser[Decl]

private[scala2java] class DeclTraverserImpl(declValTraverser: => DeclValTraverser,
                                            declVarTraverser: => DeclVarTraverser,
                                            declDefTraverser: => DeclDefTraverser,
                                            declTypeTraverser: => DeclTypeTraverser)
                                           (implicit javaWriter: JavaWriter) extends DeclTraverser {

  override def traverse(decl: Decl): Unit = decl match {
    case valDecl: Decl.Val => declValTraverser.traverse(valDecl)
    case varDecl: Decl.Var => declVarTraverser.traverse(varDecl)
    case defDecl: Decl.Def => declDefTraverser.traverse(defDecl)
    case typeDecl: Decl.Type => declTypeTraverser.traverse(typeDecl)
    case _ => writeComment(s"UNSUPPORTED: $decl")
  }
}

object DeclTraverser extends DeclTraverserImpl(DeclValTraverser,
  DeclVarTraverser,
  DeclDefTraverser,
  DeclTypeTraverser)
