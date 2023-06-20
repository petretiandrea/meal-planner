package io.github.petretiandrea.mealplanner.web.features.plan.rest.impl

import io.github.petretiandrea.mealplanner.domain.*
import io.github.petretiandrea.mealplanner.web.features.foods.FoodResponseDto
import io.github.petretiandrea.mealplanner.web.features.plan.rest.*
import io.github.petretiandrea.mealplanner.web.features.plan.domain.*
import io.github.petretiandrea.mealplanner.web.features.plan.domain.FoodDataset
import io.github.petretiandrea.mealplanner.web.features.plan.rest.dto.CreateMealPlan
import io.github.petretiandrea.mealplanner.web.features.plan.rest.dto.MealPlanResponse
import io.github.petretiandrea.mealplanner.web.features.plan.rest.dto.PlanDto
import io.github.petretiandrea.mealplanner.web.features.plan.rest.dto.StatusPlan
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PlanRequestControllerImpl(
    private val mealPlanJobDispatcher: MealPlanJobDispatcher,
) : PlanRequestRestController {

    override suspend fun createPlanRequest(request: CreateMealPlan): ResponseEntity<String> {
        val jobRequest = MealPlanJobRequest(
            Macro(request.targetCarbs, request.targetProteins, request.targetFats),
            request.numberOfPlans,
            createFoodDataset(request)
        )
        return mealPlanJobDispatcher.dispatchJob(jobRequest)
            ?.let { ResponseEntity.status(HttpStatus.CREATED).body(it.jobId.toString()) }
            ?: ResponseEntity.internalServerError().build()
    }

    override suspend fun getPlanRequest(requestId: String): ResponseEntity<MealPlanResponse> {
        return mealPlanJobDispatcher.getJobById(JobId.fromString(requestId))
            ?.let { mealJobToResponse(it) }
            ?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    override suspend fun createPlanSync(request: CreateMealPlan): ResponseEntity<MealPlanResponse> {
        val jobRequest = MealPlanJobRequest(
            Macro(request.targetCarbs, request.targetProteins, request.targetFats),
            request.numberOfPlans,
            createFoodDataset(request)
        )

        return mealPlanJobDispatcher.dispatchJob(jobRequest)
            ?.let { mealPlanJobDispatcher.waitJobById(it.jobId) }
            ?.let { ResponseEntity.ok(mealJobToResponse(it)) }
            ?: ResponseEntity.notFound().build()
    }

    private fun mealJobToResponse(mealJob: MealPlanJob) = when (mealJob) {
        is MealPlanJobComputing -> MealPlanResponse(mealJob.jobId.toString(), StatusPlan.COMPUTING)
        is MealPlanJobReady -> MealPlanResponse(
            mealJob.jobId.toString(),
            StatusPlan.READY,
            mapPlansToDtos(mealJob.plans)
        )

        is MealPlanJobError -> MealPlanResponse(mealJob.jobId.toString(), StatusPlan.ERROR, error = mealJob.error)
    }

    private fun createFoodDataset(request: CreateMealPlan): FoodDataset = when {
        request.foods.isNotEmpty() -> FoodDataset.raw(
            request.foods.map {
                FoodWithConstraint(
                    Food(
                        it.name,
                        it.carbs,
                        it.fats,
                        it.proteins,
                        100.0
                    ),
                    it.constraint?.let { constraint -> FixedWeight(constraint.grams) }
                )
            })

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