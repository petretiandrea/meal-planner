package io.github.petretiandrea.mealplanner.web.features.plan.rest

data class CreateMealPlanRequest(
    val numberOfPlans: Int,
    val targetCarbs: Double,
    val targetProteins: Double,
    val targetFats: Double,
    val foodIds: List<String> = emptyList(),
    val foods: List<FoodDto> = emptyList()
)

data class FoodDto(
    val name: String,
    val carbs: Double,
    val proteins: Double,
    val fats: Double
)