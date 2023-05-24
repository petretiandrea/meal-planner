package io.github.petretiandrea.mealplanner.domain

import java.util.stream.Collector
import java.util.stream.Collectors
import kotlin.math.abs

data class Macro(
    val carbs: Double = 0.0,
    val proteins: Double = 0.0,
    val fats: Double = 0.0
) {
    companion object {
        fun foodCollector(): Collector<Food, *, Macro> {
            return Collectors.reducing(
                Macro(),
                { food -> Macro(food.carbs, food.proteins, food.fats) },
                { acc, macro -> acc + macro }
            )
        }
    }

    operator fun plus(food: Food): Macro {
        return this + Macro(food.carbs, food.proteins, food.fats)
    }

    operator fun plus(macro: Macro): Macro {
        return Macro(
            carbs + macro.carbs,
            proteins + macro.proteins,
            fats + macro.fats,
        )
    }

    fun similarity(target: Macro): Double {
        val deltaCarbs = abs(target.carbs - this.carbs)
        val deltaProteins = abs(target.proteins - this.proteins)
        val deltaFats = abs(target.fats - this.fats)
        return -(deltaCarbs + deltaProteins + deltaFats)
    }
}