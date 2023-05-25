package io.github.petretiandrea.mealplanner.web.features.foods

import org.springframework.web.bind.annotation.RestController

@RestController
interface FoodRestController {
    suspend fun getAvailableFoods(): List<FoodResponseDto>

    suspend fun updateFood(foodId: String, foodUpdateDto: FoodUpdateDto): FoodResponseDto

    suspend fun createFood(foodUpdateDto: FoodUpdateDto): FoodResponseDto
}