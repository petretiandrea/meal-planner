package io.github.petretiandrea.mealplanner.domain

import java.util.stream.Stream

interface MealPlanner {
    fun generatePlan(targetCarbs: Double, targetProteins: Double, targetFats: Double): Stream<MealPlan>
}