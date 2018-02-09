---
layout: docs
title: Foldable
permalink: /docs/typeclasses/foldable/
---

## Foldable


The `Foldable` typeclass abstracts the ability to combine the contents of a data structure into a resultant value.
If the typeclass instance is a Collection 
A Foldable typeclass instance implements two methods 

 - `foldLeft(fa: HK<F, A>, b: B, f: (B, A) -> B): B` eagerly folds `fa` from left-to-right.
 - `foldRight(fa: HK<F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B>` lazily folds `fa` from right-to-left.
 
 
 over the computational context of a type constructor.
Examples of type constructors that can implement instances of the Functor typeclass include `Option`, `NonEmptyList`,
`List` and many other datatypes that include a `map` function with the shape `fun F<B>.map(f: (A) -> B): F<B>` where `F`
refers to `Option`, `List` or any other type constructor whose contents can be transformed.


Foldable type class instances can be defined for data structures that can be 
folded to a summary value.

In the case of a collection (such as `List` or `Set`), these methods will fold 
together (combine) the values contained in the collection to produce a single 
result. Most collection types have `foldLeft` methods, which will usually be 
used by the associated `Foldable[_]` instance.

`Foldable[F]` is implemented in terms of two basic methods:

 - `foldLeft(fa, b)(f)` eagerly folds `fa` from left-to-right.
 - `foldRight(fa, b)(f)` lazily folds `fa` from right-to-left.

We have a short intro mentioning what behavior they abstract, and which other typeclasses it inherits from
sometimes a section depicting the main use case is placed here, i.e. Applicative has a long desctiption of applicative builders
then go through the main combinators/operators/functions with a short description of what they can be used for. This also includes a snippet
afterwards we mention any piece of syntax available for the operators
then we have a short section mentioning that Arrow provides laws for this typeclass
and finally we link all the datatypes that provide an implementation of this typeclass


```kotlin:ank
 foldable<ListKWHK>().fold(StringMonoidInstance, ListKW(listOf("a", "b", "c")))
    foldable<ListKWHK>().foldMap(StringMonoidInstance, ListKW(listOf(1,2,3,6))){it.toString()}

    foldable<ListKWHK>().reduceLeftToOption(ListKW(emptyList<Int>()), {it.toString()}, { acc, e -> acc + e})
    foldable<ListKWHK>().reduceLeftToOption(ListKW(listOf(1,2,3,4)), {it.toString()}, {str, i -> str + i})
    foldable<ListKWHK>().reduceRightToOption(ListKW(listOf(1,2,3,4)), {it.toString()}, {i, lStr -> lStr.map { i.toString() + it }}).value()
    foldable<ListKWHK>().reduceRightToOption(ListKW(emptyList<Int>()), {it.toString()}, {i, lStr -> lStr.map { i.toString() + it }}).value()
    foldable<ListKWHK>().find(ListKW(listOf(1,2,3,4))){ it % 2 == 0}
    foldable<ListKWHK>().exists(ListKW(listOf(1,2,3,4))){ it % 2 == 0}
    foldable<ListKWHK>().forall(ListKW(listOf(1,2,3,4))){ it > 0}
    foldable<ListKWHK>().isEmpty(ListKW(listOf(1,2,3,4)))
    foldable<ListKWHK>().combineAll(IntMonoid, ListKW(listOf(1,2,3,4)))
    foldable<ListKWHK>().traverse_(Option.applicative(), ListKW(listOf(1,2,3,4))) { Option.pure(it) }
    foldable<ListKWHK>().sequence_(Option.applicative(), ListKW(listOf(1,2,3,4)).map { Option.pure(it) })
```