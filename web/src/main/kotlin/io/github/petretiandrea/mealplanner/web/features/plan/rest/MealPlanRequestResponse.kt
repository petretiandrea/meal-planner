package io.github.petretiandrea.mealplanner.web.features.plan.rest

data class MealPlanRequestResponse(
    val requestId: String,
    val status: StatusPlan,
    val plans: List<PlanDto> = emptyList(),
    val error: String? = null
)

enum class StatusPlan {
    COMPUTING,
    READY,
    ERROR
}
