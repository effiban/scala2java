package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaModifier
import effiban.scala2java.orderings.JavaModifierOrdering
import effiban.scala2java.transformers.ScalaToJavaModifierTransformer

import scala.meta.Mod

trait JavaModifiersResolver {

  def resolve(params: JavaModifiersResolverParams): List[JavaModifier]
}

class JavaModifiersResolverImpl(javaAllowedModifiersResolver: JavaAllowedModifiersResolver,
                                javaSupplementalModifiersResolver: JavaSupplementalModifiersResolver) extends JavaModifiersResolver {

  override def resolve(params: JavaModifiersResolverParams): List[JavaModifier] = {
    import params._

    val modifierNamesBuilder = Set.newBuilder[JavaModifier]

    // Transform the Scala modifiers into corresponding Java modifiers, when allowed
    val allowedJavaModifiers = javaAllowedModifiersResolver.resolve(javaTreeType, javaScope)
    modifierNamesBuilder ++= transform(scalaMods, allowedJavaModifiers)

    // Add additional Java-specific modifiers which are required by the params
    modifierNamesBuilder ++= javaSupplementalModifiersResolver.resolve(params)

    modifierNamesBuilder.result()
      .toList
      .sorted(JavaModifierOrdering)
  }

  private def transform(inputScalaMods: List[Mod], allowedJavaModifiers: Set[JavaModifier]): List[JavaModifier] = {
    inputScalaMods
      .map(ScalaToJavaModifierTransformer.transform)
      .collect { case Some(javaModifier) => javaModifier }
      .distinct
      .filter(allowedJavaModifiers.contains)
  }
}

object JavaModifiersResolver extends JavaModifiersResolverImpl(
  JavaAllowedModifiersResolver,
  JavaSupplementalModifiersResolver
)
