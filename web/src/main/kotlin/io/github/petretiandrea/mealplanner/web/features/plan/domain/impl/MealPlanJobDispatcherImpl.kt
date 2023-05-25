package io.github.petretiandrea.mealplanner.web.features.plan.domain.impl

import io.github.petretiandrea.mealplanner.domain.FoodRepository
import io.github.petretiandrea.mealplanner.domain.MealPlanner
import io.github.petretiandrea.mealplanner.domain.impl.genetic.GeneticMealPlanner
import io.github.petretiandrea.mealplanner.web.features.plan.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.*
import java.util.stream.Collectors

class MealPlanJobDispatcherImpl(
    coroutineScope: CoroutineScope,
    private val mealPlanJobRepository: MealPlanJobRepository
) : MealPlanJobDispatcher {

    private val requestJobQueue = Channel<Pair<MealPlanJob, MealPlanJobRequest>>()

    init {
        createPlannerActor(coroutineScope)
    }

    override suspend fun dispatchJob(mealPlanJobRequest: MealPlanJobRequest): MealPlanJob? {
        val job = MealPlanJob.computing(UUID.randomUUID())
        mealPlanJobRepository.save(job)
        requestJobQueue.send(job to mealPlanJobRequest)
        return job
    }

    override suspend fun getJobById(jobId: JobId): MealPlanJob? {
        return mealPlanJobRepository.getByJobId(jobId)
    }

    private fun createPlannerActor(coroutineScope: CoroutineScope) {
        requestJobQueue.receiveAsFlow()
            .map { (job, request) ->
                job to kotlin.runCatching {
                    val planner = createPlannerFromRequest(request)
                    planner.generatePlans(request.targetMacro)
                        .limit(request.numberOfPlans.toLong())
                        .collect(Collectors.toList())
                }
            }
            .map { (job, result) ->
                result.fold(
                    { plans -> MealPlanJob.ready(job.jobId, plans) },
                    { e -> MealPlanJob.error(job.jobId, e.message ?: "Unknown error") }
                )
            }
            .onEach { mealPlanJobRepository.save(it) }
            .launchIn(scope = coroutineScope)
    }

    private fun createPlannerFromRequest(request: MealPlanJobRequest): MealPlanner =
        when (request.foodDataset) {
            is FoodDataset.ByIds -> TODO()
            is FoodDataset.Raw -> GeneticMealPlanner(request.foodDataset.foods)
        }
}