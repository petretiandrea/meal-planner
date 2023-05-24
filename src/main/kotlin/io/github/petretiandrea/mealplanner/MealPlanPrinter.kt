package io.github.petretiandrea.mealplanner

import io.github.petretiandrea.mealplanner.domain.MealPlan
import java.util.*

object MealPlanPrinter {
    fun printPlan(plan: MealPlan) {
        println(
            String.format(
                Locale.ITALY,
                "Plan Stats -> %.2fkCal\tCarbs %.2fg\tPro %.2fg\tFats %.2fg",
                plan.totalCalories,
                plan.carbs,
                plan.proteins,
                plan.fats
            )
        )
        plan.foods.forEach {
            println(
                String.format(
                    Locale.ITALY,
                    "%s\t%.2fg (Carbs %.2fg Pro %.2fg Fats %.2fg %.2fkCal)",
                    it.name,
                    it.grams,
                    it.carbs,
                    it.proteins,
                    it.fats,
                    it.kCal
                )
            )
        }
    }

}