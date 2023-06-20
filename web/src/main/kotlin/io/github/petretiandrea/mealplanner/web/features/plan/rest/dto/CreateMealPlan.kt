package io.github.petretiandrea.mealplanner.web.features.plan.rest.dto

data class CreateMealPlan(
    val numberOfPlans: Int,
    val targetCarbs: Double,
    val targetProteins: Double,
    val targetFats: Double,
    val foods: List<FoodConstraintWrapperDto> = emptyList()
)

data class FoodConstraintWrapperDto(
    val name: String,
    val carbs: Double,
    val proteins: Double,
    val fats: Double,
    val constraint: FixedWeightConstraintDto? // TODO: add others constraints
)

data class FixedWeightConstraintDto(
    val type: String,
    val grams: Double
)