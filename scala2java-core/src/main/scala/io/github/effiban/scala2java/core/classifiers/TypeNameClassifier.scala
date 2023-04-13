package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.entities.TypeNameValues

import scala.meta.Type

trait TypeNameClassifier extends TypeClassifier[Type.Name]

object TypeNameClassifier extends TypeNameClassifier {

  final val JavaStreamLike: Set[String] = Set(
    "LazyList",
    TypeNameValues.Stream,
  )

  final val JavaListLike: Set[String] = Set(
    TypeNameValues.Seq,
    "LinearSeq",
    "IndexedSeq",
    "ArraySeq",
    TypeNameValues.List,
    TypeNameValues.ScalaVector
  )

  final val JavaSetLike: Set[String] = Set(
    TypeNameValues.Set,
    "HashSet"
  )

  final val JavaMapLike: Set[String] = Set(
    TypeNameValues.Map,
    "HashMap"
  )

  final val IterableLike: Set[String] =
    JavaStreamLike ++
      JavaListLike ++
      JavaSetLike ++
      Set("SortedSet", "TreeSet", "ListSet") ++
      JavaMapLike ++
      Set("SortedMap", "TreeMap", "ListMap")


  override def isJavaStreamLike(typeName: Type.Name): Boolean = JavaStreamLike.contains(typeName.value)

  override def isJavaListLike(typeName: Type.Name): Boolean = JavaListLike.contains(typeName.value)

  override def isJavaSetLike(typeName: Type.Name): Boolean = JavaSetLike.contains(typeName.value)

  override def isJavaMapLike(typeName: Type.Name): Boolean = JavaMapLike.contains(typeName.value)
}
