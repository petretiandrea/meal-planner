
# Meal Planner
A simple implementation of meal plan generator by using genetic algorithms.
Given a set of available foods with its macro values and a set of target macros (carbs, proteins, fats) the algorithm
compute a proper weight of each food to achieve the target macros.

## Usage
```
Usage: java -jar meal-planner.jar options_list
Options: 
    --debug, -d [false] -> Debug log 
    --targetCarbs, -carb -> Target Carbs (always required) { Double }
    --targetFats, -fat -> Target Fats (always required) { Double }
    --targetProteins, -pro -> Target Proteins (always required) { Double }
    --plans, -plans [4] -> Number of plans { Int }
    --foodFile, -foods [foods.csv] -> Foods file { String }
    --help, -h -> Usage info 
```
Carbs, proteins and fats are supposed to be in grams. 
The foods file must be a simple CSV with the following syntax:
```csv
name;carbs;fats;protein
bread;50;3;7
arachids;11;52;26
mermalade;37;0;0
```
Again, macros are expressed in grams per 100g of product.

The algorithm also assume the following kcal:
```kotlin
const val KCAL_PROTEIN_PER_GRAM = 4
const val KCAL_CARB_PER_GRAM = 4
const val KCAL_FAT_PER_GRAM = 9
```

### Food Constraints
You can use csv file to define a food constraint such as fixed weight for a specific food item. Actually the following constraints are supported:
- FIXED WEIGHT: fix weights for multiple foods, for example you can fix bread to 60g (By default, there is a ±5 grams of toleration)
- RANGE WEIGHT: Working In Progress


## Example
Consider the following CSV saved as `simple-foods.csv` and these macro as target (Carbs: 40g, Proteins: 15g, Fat: 10g)
```csv
name;carbs;fats;protein
bread;50;3;7
arachids;11;52;26
mermalade;37;0;0
```
Run it:
`java -jar meal-planner.jar -carb 40 -fat 10 -pro 15 --foodFile ./simple-foods.csv`.

The algorithm by default produces 4 plans from the best to  worst (you can change this by adding `-plans` option):
```
Plan Stats -> 287,02kCal	Carbs 40,13g	Pro 9,08g	Fats 10,02g
bread	74,00g (Carbs 37,00g Pro 5,18g Fats 2,22g 188,70kCal)
arachids	15,00g (Carbs 1,65g Pro 3,90g Fats 7,80g 92,40kCal)
mermalade	4,00g (Carbs 1,48g Pro 0,00g Fats 0,00g 5,92kCal)
---------------------------------
Plan Stats -> 293,18kCal	Carbs 40,24g	Pro 9,34g	Fats 10,54g
bread	74,00g (Carbs 37,00g Pro 5,18g Fats 2,22g 188,70kCal)
arachids	16,00g (Carbs 1,76g Pro 4,16g Fats 8,32g 98,56kCal)
mermalade	4,00g (Carbs 1,48g Pro 0,00g Fats 0,00g 5,92kCal)
---------------------------------
Plan Stats -> 293,18kCal	Carbs 40,24g	Pro 9,34g	Fats 10,54g
bread	74,00g (Carbs 37,00g Pro 5,18g Fats 2,22g 188,70kCal)
arachids	16,00g (Carbs 1,76g Pro 4,16g Fats 8,32g 98,56kCal)
mermalade	4,00g (Carbs 1,48g Pro 0,00g Fats 0,00g 5,92kCal)
---------------------------------
Plan Stats -> 293,18kCal	Carbs 40,24g	Pro 9,34g	Fats 10,54g
bread	74,00g (Carbs 37,00g Pro 5,18g Fats 2,22g 188,70kCal)
arachids	16,00g (Carbs 1,76g Pro 4,16g Fats 8,32g 98,56kCal)
mermalade	4,00g (Carbs 1,48g Pro 0,00g Fats 0,00g 5,92kCal)
---------------------------------
```

### Example for Fixed Weight
The following CSV set weight for bread to 60g:
```
name;carbs;fats;protein;fixed_weight
bread;50;3;7;60
arachids;11;52;26
mermalade;37;0;0
```

## Running Web Service
The web service is written using spring boot due to the possibility to integrate immediately with meal planner core.
TODO: complete guide