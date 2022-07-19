package tech.qmachines.builder.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class MutableProperty(
    val immutableClass: KClass<*>,
    val mutableClass: KClass<*> = Any::class,
    val translateBuilders: TranslateBuilders = TranslateBuilders.NEVER,
    val toMutableMethod: String = "%p",
    val toImmutableMethod: String = "%p"
)


