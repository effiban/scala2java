package effiban.scala2java

import effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Mod.Final
import scala.meta.Term

trait TermParamTraverser extends ScalaTreeTraverser[Term.Param]

private[scala2java] class TermParamTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                 typeTraverser: => TypeTraverser,
                                                 nameTraverser: => NameTraverser,
                                                 javaModifiersResolver: JavaModifiersResolver)
                                                (implicit javaEmitter: JavaEmitter) extends TermParamTraverser {
  import javaEmitter._

  // method parameter declaration
  override def traverse(termParam: Term.Param): Unit = {
    annotListTraverser.traverseMods(termParam.mods, onSameLine = true)
    val mods = javaOwnerContext match {
      case Lambda => termParam.mods
      case _ => termParam.mods :+ Final()
    }
    val modifierNames = javaModifiersResolver.resolve(mods, List(classOf[Final]))
    emitModifiers(modifierNames)
    termParam.decltpe.foreach(declType => {
      typeTraverser.traverse(declType)
      emit(" ")
    })
    nameTraverser.traverse(termParam.name)
    // TODO handle 'default'
  }
}

object TermParamTraverser extends TermParamTraverserImpl(
  AnnotListTraverser,
  TypeTraverser,
  NameTraverser,
  JavaModifiersResolver
)