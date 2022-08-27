package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaModifiersResolverParams}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait ObjectTraverser extends ScalaTreeTraverser[Defn.Object]

private[traversers] class ObjectTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                              templateTraverser: => TemplateTraverser,
                                              javaModifiersResolver: JavaModifiersResolver)
                                             (implicit javaWriter: JavaWriter) extends ObjectTraverser {

  import javaWriter._

  override def traverse(objectDef: Defn.Object): Unit = {
    writeLine()
    writeComment("originally a Scala object")
    writeLine()
    annotListTraverser.traverseMods(objectDef.mods)
    writeTypeDeclaration(modifiers = resolveJavaModifiers(objectDef),
      typeKeyword = "class",
      name = s"${objectDef.name.toString}")
    val outerJavaScope = javaScope
    javaScope = JavaTreeType.Class
    templateTraverser.traverse(objectDef.templ)
    javaScope = outerJavaScope
  }

  private def resolveJavaModifiers(objectDef: Defn.Object) = {
    val params = JavaModifiersResolverParams(
      scalaTree = objectDef,
      scalaMods = objectDef.mods,
      javaTreeType = JavaTreeType.Class,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(params)
  }
}

