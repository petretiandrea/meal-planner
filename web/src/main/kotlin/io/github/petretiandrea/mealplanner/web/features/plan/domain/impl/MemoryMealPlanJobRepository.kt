package io.github.petretiandrea.mealplanner.web.features.plan.domain.impl

import io.github.petretiandrea.mealplanner.web.features.plan.domain.JobId
import io.github.petretiandrea.mealplanner.web.features.plan.domain.MealPlanJob
import io.github.petretiandrea.mealplanner.web.features.plan.domain.MealPlanJobRepository

class MemoryMealPlanJobRepository : MealPlanJobRepository {

    private var jobs: Map<JobId, MealPlanJob> = HashMap()

    override suspend fun save(job: MealPlanJob): Boolean {
        jobs += job.jobId to job
        return true
    }

    override suspend fun getByJobId(jobId: JobId): MealPlanJob? {
        return jobs[jobId]
    }
}