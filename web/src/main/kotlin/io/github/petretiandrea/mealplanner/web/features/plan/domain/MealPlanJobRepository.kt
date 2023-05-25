package io.github.petretiandrea.mealplanner.web.features.plan.domain

interface MealPlanJobRepository {
    suspend fun save(job: MealPlanJob): Boolean
    suspend fun getByJobId(jobId: JobId): MealPlanJob?
}