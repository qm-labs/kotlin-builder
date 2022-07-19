package tech.qmachines.builder.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@MutablePropertyType(
    MutableProperty(
        immutableClass = Set::class,
        mutableClass = MutableSet::class,
        translateBuilders = TranslateBuilders.TYPE_AND_TYPE_ARGUMENTS,
        toMutableMethod = "%p.toMutableSet()"
    )
)
annotation class MutableSetProperty