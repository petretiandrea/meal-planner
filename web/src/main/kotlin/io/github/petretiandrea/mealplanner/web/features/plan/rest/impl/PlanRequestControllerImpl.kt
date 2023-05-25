package io.github.petretiandrea.mealplanner.web.features.plan.rest.impl

import io.github.petretiandrea.mealplanner.domain.Food
import io.github.petretiandrea.mealplanner.domain.Macro
import io.github.petretiandrea.mealplanner.domain.MealPlan
import io.github.petretiandrea.mealplanner.web.features.foods.FoodResponseDto
import io.github.petretiandrea.mealplanner.web.features.plan.rest.*
import io.github.petretiandrea.mealplanner.web.features.plan.domain.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PlanRequestControllerImpl(
    private val mealPlanJobDispatcher: MealPlanJobDispatcher,
) : PlanRequestRestController {

    override suspend fun createPlanRequest(request: CreateMealPlanRequest): ResponseEntity<String> {
        val jobRequest = MealPlanJobRequest(
            Macro(request.targetCarbs, request.targetProteins, request.targetFats),
            request.numberOfPlans,
            createFoodDataset(request)
        )
        return mealPlanJobDispatcher.dispatchJob(jobRequest)
            ?.let { ResponseEntity.status(HttpStatus.CREATED).body(it.jobId.toString()) }
            ?: ResponseEntity.internalServerError().build()
    }

    override suspend fun getPlanRequest(requestId: String): ResponseEntity<MealPlanRequestResponse> {
        return mealPlanJobDispatcher.getJobById(JobId.fromString(requestId))?.let {
            when (it) {
                is MealPlanJobComputing -> MealPlanRequestResponse(requestId, StatusPlan.COMPUTING)
                is MealPlanJobReady -> MealPlanRequestResponse(
                    requestId,
                    StatusPlan.READY,
                    mapPlansToDtos(it.plans)
                )

                is MealPlanJobError -> MealPlanRequestResponse(requestId, StatusPlan.ERROR, error = it.error)
            }
        }?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    private fun createFoodDataset(request: CreateMealPlanRequest): FoodDataset = when {
        request.foods.isNotEmpty() -> FoodDataset.raw(
            request.foods.map {
                Food(
                    it.name,
                    it.carbs,
                    it.fats,
                    it.proteins,
                    100.0
                )
            })

        request.foodIds.isNotEmpty() -> FoodDataset.lazy(request.foodIds)
        else -> FoodDataset.raw(emptyList())
    }

    private fun mapPlansToDtos(plans: List<MealPlan>): List<PlanDto> {
        return plans.map {
            PlanDto(
                it.foods.map { FoodResponseDto(it.name, it.name, it.carbs, it.proteins, it.fats, it.grams) }.toList(),
                it.carbs,
                it.proteins,
                it.fats,
                it.totalCalories
            )
        }
    }
}