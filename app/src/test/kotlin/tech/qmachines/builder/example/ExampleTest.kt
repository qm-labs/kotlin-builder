package tech.qmachines.builder.example

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ExampleTest: FreeSpec({

    "using the example builder" {

        val builder = Example.Builder(constant = "world")

        builder.value = 3
        builder.list += listOf("hello", "world")
        builder.set += setOf("a", "b", "c")
        builder.map += mapOf(1 to "y", 2 to "z")
        builder.another.value = 5
        builder.builderMap[4] = AnotherExample.Builder(4)
        builder.builderMap[5] = AnotherExample.Builder().apply { value = 5 }

        val obj = builder.build()

        obj.value shouldBe 3
        obj.list shouldBe listOf("hello", "world")
        obj.set shouldBe setOf("a", "b", "c")
        obj.map shouldBe mapOf(1 to "y", 2 to "z")
        obj.another.value shouldBe 5
        obj.builderMap[4]?.value shouldBe 4
        obj.builderMap[5]?.value shouldBe 5
        obj.constant shouldBe "world"

        val builderAgain = obj.builder()


        builderAgain.value shouldBe 3
        builderAgain.list shouldBe listOf("hello", "world")
        builderAgain.set shouldBe setOf("a", "b", "c")
        builderAgain.map shouldBe mapOf(1 to "y", 2 to "z")
        builderAgain.another.value shouldBe 5
        builderAgain.builderMap[4]?.value shouldBe 4
        builderAgain.builderMap[5]?.value shouldBe 5
        builderAgain.constant shouldBe "world"
    }


})