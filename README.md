# Wither

(This README, in all its ridiculousness, is entirely human-made)

Have you ever had to bring in multiple receivers or contexts?
Did you end up with code that looks like this?

```kotlin
context(Foo, Bar, Baz) {
  // ...
  if (someCondition) {
    context(A, B) {
      context(makeCFromAandB()) {
        context(nowMakeDAsWellUsingC()) {
          with(contextOf<Foo>()) { // oops, I actually really need Foo as a receiver here...
            finallyDoTheGoshDarnThing()
          }
        }
      }
    }
  }
}
```
Introducinnnnng, Wither!
Your code can be flattened down to look like this instead:
```kotlin
context(Foo, Bar, Baz)
// ...
if (someCondition) {
  context(A, B)
  context(makeCFromAandB())
  context(nowMakeDAsWellUsingC())
  with(contextOf<Foo>()) // oops, I actually really need Foo as a receiver here...
  finallyDoTheGoshDarnThing()
}
```
Isn't that beautiful!
Oh, how, you may ask? ✨Compiler magic✨
[Kotlin DataFrame](https://github.com/Kotlin/dataframe) uses similar magic, so this'll *probably* stay *somewhat* supported (at least for `with`, but `context` is hacky and might not!)
Features:

- `with` and `context` can contain multiple values `with("Hi", 42)`, with semantics similar to context parameter groups (for `with`, currently, the values are introduced as extension receivers, so scope pollution may apply!)

- multiple `with` and `context` calls can override each other:
```kotlin
with("Hi")
with("Low")
println(contextOf<String>()) // prints "Low"
```

- `with`s and `context`s are scoped inside of the local block, and so you can have use them inside an if or a when branch, and it won't affect other call sites. This means you can also have them in lambdas!

Try it out today! All it takes is just:
```kotlin
plugins {
  id("io.github.kyay10.wither") version "0.0.2"
}
```
and simply import `io.github.kyay10.wither.with` or `io.github.kyay10.wither.context`

I'll eventually add multi-version support, but for now, the plugin is only tested against 2.4.10. YMMV on other versions.
