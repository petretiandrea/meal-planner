package io.github.petretiandrea.mealplanner.web.features.plan.domain

import io.github.petretiandrea.mealplanner.domain.MealPlan
import java.util.*

typealias JobId = UUID

sealed interface MealPlanJob {
    val jobId: JobId

    companion object {
        fun computing(jobId: JobId) = MealPlanJobComputing(jobId)
        fun ready(jobId: JobId, plans: List<MealPlan>) = MealPlanJobReady(jobId, plans)

        fun error(jobId: JobId, error: String = "") = MealPlanJobError(jobId, error)
    }
}

data class MealPlanJobComputing(
    override val jobId: JobId,
) : MealPlanJob


data class MealPlanJobReady(
    override val jobId: JobId,
    val plans: List<MealPlan>
) : MealPlanJob

data class MealPlanJobError(
    override val jobId: JobId,
    val error: String
) : MealPlanJob