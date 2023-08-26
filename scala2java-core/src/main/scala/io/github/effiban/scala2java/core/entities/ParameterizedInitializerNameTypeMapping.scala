package io.github.effiban.scala2java.core.entities

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaArray

import scala.meta.{Term, Type}

trait ParameterizedInitializerNameTypeMapping {

  def typeInitializedBy(termName: Term.Name): Option[Type.Ref]
}

object ParameterizedInitializerNameTypeMapping extends ParameterizedInitializerNameTypeMapping {

  private val InitializerNameToType = Map(
    Term.Name(TermNameValues.ScalaRange) -> Type.Name(TypeNameValues.ScalaRange),
    Term.Name(TermNameValues.ScalaOption) -> Type.Name(TypeNameValues.ScalaOption),
    Term.Name(TermNameValues.ScalaSome) -> Type.Name(TypeNameValues.ScalaOption),
    Term.Name(TermNameValues.ScalaRight) -> Type.Name(TypeNameValues.Either),
    Term.Name(TermNameValues.ScalaLeft) -> Type.Name(TypeNameValues.Either),
    Term.Name(TermNameValues.Try) -> Type.Name(TypeNameValues.Try),
    Term.Name(TermNameValues.ScalaSuccess) -> Type.Name(TypeNameValues.Try),
    Term.Name(TermNameValues.ScalaFailure) -> Type.Name(TypeNameValues.Try),
    Term.Name(TermNameValues.Future) -> Type.Name(TypeNameValues.Future),
    Term.Name(TermNameValues.Stream) -> Type.Name(TypeNameValues.Stream),
    Term.Name(TermNameValues.ScalaArray) -> ScalaArray,
    Term.Name(TermNameValues.List) -> Type.Name(TypeNameValues.List),
    Term.Name(TermNameValues.ScalaVector) -> Type.Name(TypeNameValues.ScalaVector),
    Term.Name(TermNameValues.Seq) -> Type.Name(TypeNameValues.Seq),
    Term.Name(TermNameValues.Set) -> Type.Name(TypeNameValues.Set),
    Term.Name(TermNameValues.Map) -> Type.Name(TypeNameValues.Map)
  )
  // TODO add more collection initializers and durations

  def typeInitializedBy(termName: Term.Name): Option[Type.Ref] =
    InitializerNameToType
      .find { case(aTermName, _) => aTermName.structure == termName.structure }
      .map { case (_, typeName) => typeName }
}
