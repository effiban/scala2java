package effiban.scala2java

import scala.meta.Type

trait TypeTupleTraverser extends ScalaTreeTraverser[Type.Tuple]

private[scala2java] class TypeTupleTraverserImpl(implicit javaEmitter: JavaEmitter) extends TypeTupleTraverser {
  import javaEmitter._

  //tuple as type, e.g. x: (Int, String).
  override def traverse(tupleType: Type.Tuple): Unit = {
    //TODO if only 2 params, can be translated into Java Map.Entry, Apache Pair etc.
    //TODO if more than 2, can be converted to a JOOL tuple (should be an input option)
    emitComment(tupleType.toString())
  }
}

object TypeTupleTraverser extends TypeTupleTraverserImpl