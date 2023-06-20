package io.github.petretiandrea.mealplanner.domain

import java.util.stream.Stream

interface MealPlanner {
    fun generatePlans(
        dataset: FoodDataset,
        targetMacro: Macro
    ): Stream<MealPlan>
}

sealed interface FoodConstraint {
    fun isValid(food: Food): Boolean
    fun isValidGrams(grams: Double): Boolean
}
data class FixedWeight(
    val grams: Double,
    private val epsilon: Double = 5.0
) : FoodConstraint {
    override fun isValid(food: Food) = isValidGrams(food.grams)
    override fun isValidGrams(grams: Double): Boolean =
        grams in (this.grams - epsilon)..(this.grams + epsilon)
}
data class RangeWeight(
    val minGrams: Double,
    val maxGrams: Double,
    private val epsilon: Double = 5.0
): FoodConstraint {
    override fun isValid(food: Food) = isValidGrams(food.grams)
    override fun isValidGrams(grams: Double): Boolean =
        grams in (minGrams - epsilon)..(maxGrams + epsilon)
}