package io.github.petretiandrea.mealplanner.web.features.plan.domain.impl

import io.github.petretiandrea.mealplanner.domain.FoodDataset
import io.github.petretiandrea.mealplanner.domain.impl.genetic.GeneticMealPlanner
import io.github.petretiandrea.mealplanner.web.features.plan.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.stream.Collectors

class MealPlanJobDispatcherImpl(
    coroutineScope: CoroutineScope,
    private val mealPlanJobRepository: MealPlanJobRepository
) : MealPlanJobDispatcher {

    private val requestJobQueue = Channel<Pair<MealPlanJob, MealPlanJobRequest>>()
    private var completeJobChannels = mapOf<JobId, MutableSharedFlow<MealPlanJob>>()

    init {
        createPlannerActor(coroutineScope)
    }

    override suspend fun dispatchJob(mealPlanJobRequest: MealPlanJobRequest): MealPlanJob? {
        val job = MealPlanJob.computing(UUID.randomUUID())
        mealPlanJobRepository.save(job)
        completeJobChannels += job.jobId to MutableSharedFlow<MealPlanJob>(replay = 1)
        requestJobQueue.send(job to mealPlanJobRequest)
        return job
    }

    override suspend fun waitJobById(jobId: JobId): MealPlanJob? {
        val waitJob = when (val job = mealPlanJobRepository.getByJobId(jobId)) {
            is MealPlanJobComputing -> completeJobChannels[jobId]
            is MealPlanJobError -> flowOf(job)
            is MealPlanJobReady -> flowOf(job)
            null -> emptyFlow()
        }
        return waitJob?.firstOrNull()
    }

    override suspend fun getJobById(jobId: JobId): MealPlanJob? {
        return mealPlanJobRepository.getByJobId(jobId)
    }

    private fun createPlannerActor(coroutineScope: CoroutineScope) {
        requestJobQueue.receiveAsFlow()
            .map { (job, request) ->
                job to kotlin.runCatching {
                    val planner = GeneticMealPlanner()
                    val dataset = createDatasetFromRequest(request)
                    planner.generatePlans(dataset, request.targetMacro)
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
            .onEach {
                mealPlanJobRepository.save(it)
                completeJobChannels[it.jobId]?.emit(it)
                completeJobChannels -= it.jobId
            }
            .launchIn(scope = coroutineScope)
    }

    private fun createDatasetFromRequest(request: MealPlanJobRequest): FoodDataset = when(request.foodDataset) {
        is io.github.petretiandrea.mealplanner.web.features.plan.domain.FoodDataset.Raw -> FoodDataset.of(request.foodDataset.foods)
        else -> throw UnsupportedOperationException("Unsupported food dataset")
    }
}