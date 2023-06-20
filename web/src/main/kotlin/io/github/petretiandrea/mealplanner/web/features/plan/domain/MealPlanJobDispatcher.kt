package io.github.petretiandrea.mealplanner.web.features.plan.domain

import io.github.petretiandrea.mealplanner.web.features.plan.domain.impl.MealPlanJobDispatcherImpl
import io.github.petretiandrea.mealplanner.web.features.plan.domain.impl.MemoryMealPlanJobRepository
import kotlinx.coroutines.CoroutineScope

interface MealPlanJobDispatcher {
    suspend fun dispatchJob(mealPlanJobRequest: MealPlanJobRequest): MealPlanJob?
    suspend fun waitJobById(jobId: JobId): MealPlanJob?
    suspend fun getJobById(jobId: JobId): MealPlanJob?

    companion object {
        fun default(
            coroutineScope: CoroutineScope,
            jobRepository: MealPlanJobRepository = MemoryMealPlanJobRepository()
        ): MealPlanJobDispatcherImpl {
            return MealPlanJobDispatcherImpl(coroutineScope, jobRepository)
        }
    }
}