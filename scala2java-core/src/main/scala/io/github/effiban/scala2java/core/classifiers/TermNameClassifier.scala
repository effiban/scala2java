package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.entities.TermNameValues

import scala.meta.Term

trait TermNameClassifier {
  def isJavaStreamLike(termName: Term.Name): Boolean

  def isJavaListLike(termName: Term.Name): Boolean

  def isJavaSetLike(termName: Term.Name): Boolean

  def isJavaMapLike(termName: Term.Name): Boolean

  def hasApplyMethod(termName: Term.Name): Boolean

  def hasEmptyMethod(termName: Term.Name): Boolean

  def supportsNoArgInvocation(termName: Term.Name): Boolean
}

object TermNameClassifier extends TermNameClassifier {

  final val JavaStreamLike: Set[String] = Set(
    "LazyList",
    TermNameValues.Stream,
  )

  final val JavaListLike: Set[String] = Set(
    TermNameValues.Seq,
    "LinearSeq",
    "IndexedSeq",
    "ArraySeq",
    TermNameValues.List,
    TermNameValues.ScalaVector
  )

  final val JavaSetLike: Set[String] = Set(
    TermNameValues.Set,
    "HashSet"
  )

  final val JavaMapLike: Set[String] = Set(
    TermNameValues.Map,
    "HashMap"
  )

  final val IterableLike: Set[String] =
    JavaStreamLike ++
      JavaListLike ++
      JavaSetLike ++
      Set("SortedSet", "TreeSet", "ListSet") ++
      JavaMapLike ++
      Set("SortedMap", "TreeMap", "ListMap")


  final val ObjectsWithApplyMethod = Set(
    TermNameValues.ScalaArray,
    TermNameValues.ScalaRange,
    TermNameValues.ScalaOption,
    TermNameValues.ScalaSome,
    TermNameValues.ScalaRight,
    TermNameValues.ScalaLeft,
    TermNameValues.Try,
    TermNameValues.ScalaSuccess,
    TermNameValues.ScalaFailure,
    TermNameValues.Future
  ) ++ IterableLike

  final val ObjectsWithEmptyMethod = Set(
    TermNameValues.ScalaArray,
    TermNameValues.ScalaOption
  ) ++ IterableLike

  // TODO - add more
  final val SupportNoArgInvocations = Set(
    TermNameValues.Print,
    TermNameValues.Println
  )

  override def isJavaStreamLike(termName: Term.Name): Boolean = JavaStreamLike.contains(termName.value)

  override def isJavaListLike(termName: Term.Name): Boolean = JavaListLike.contains(termName.value)

  override def isJavaSetLike(termName: Term.Name): Boolean = JavaSetLike.contains(termName.value)

  override def isJavaMapLike(termName: Term.Name): Boolean = JavaMapLike.contains(termName.value)

  override def hasApplyMethod(termName: Term.Name): Boolean = ObjectsWithApplyMethod.contains(termName.value)

  override def hasEmptyMethod(termName: Term.Name): Boolean = ObjectsWithEmptyMethod.contains(termName.value)

  override def supportsNoArgInvocation(termName: Term.Name): Boolean = SupportNoArgInvocations.contains(termName.value)
}
