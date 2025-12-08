package com.reborn.wasteless.data


data class WasteItem (
    val wasteId: String,
    val name: String,
    val portionWeight: Double //In grams per portion
)

//Objects for waste items, follow dataclass WasteItem format
object WasteItemsRepository {
    val unavoidableItems = listOf(
        WasteItem("fruit_seeds",    "Fruit Seeds",      10.0),
        WasteItem("fruit_peels",    "Fruit Peels",      30.0),
        WasteItem("egg_shells",     "Eggshells",        5.0),
        WasteItem("chicken_bones",  "Chicken Bones",    40.0),
        WasteItem("fish_skin",      "Fish Skin",        20.0),
        WasteItem("prawn_scales",   "Prawn Scales",     10.0)
    )

    val avoidableItems = listOf(
        WasteItem("rice",       "Rice",         150.0),
        WasteItem("noodles",    "Noodles",      150.0),
        WasteItem("cookies",    "Cookies",      30.0),
        WasteItem("asparagus",  "Asparagus",    90.0),
        WasteItem("chicken",    "Chicken",      120.0),
        WasteItem("beef",       "Beef",         120.0),
        WasteItem("eggs",       "Eggs",         50.0),
        WasteItem("milk",       "Milk",         240.0),
        WasteItem("apples",     "Apples",       150.0),
        WasteItem("pork",       "Pork",         120.0)
    )

    val foodRelatedItems = listOf(
        WasteItem("plastic_bag",            "Plastic Bag",             5.0),
        WasteItem("plastic_bottled_water",  "Plastic Bottled Water",   12.0),
        WasteItem("disposable_utensils",    "Disposable Utensils",     5.0),
        WasteItem("plastic_cups",           "Plastic Cups",            7.0),
        WasteItem("paper_plates",           "Paper Plates",            10.0),
        WasteItem("containers_plastic",     "Containers (Plastic)",    20.0),
        WasteItem("containers_paper",       "Containers (Paper)",      15.0),
    )

    fun getItemsForWasteType(type: WasteType): List<WasteItem> = when (type) {
        WasteType.UNAVOIDABLE -> unavoidableItems
        WasteType.AVOIDABLE -> avoidableItems
        WasteType.FOOD_RELATED -> foodRelatedItems
    }
}