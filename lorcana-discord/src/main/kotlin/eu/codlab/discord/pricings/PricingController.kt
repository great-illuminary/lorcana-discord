package eu.codlab.discord.pricings

import eu.codlab.discord.database.utils.Queue
import eu.codlab.lorcana.raw.SetDescription
import eu.codlab.tcg.pricing.models.Price
import eu.codlab.tcg.pricing.models.Product
import eu.codlab.tcg.pricing.requests.RequestLoader
import korlibs.datastructure.iterators.fastForEach
import korlibs.time.DateTime
import korlibs.time.hours
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class CategoryGroup(
    val categoryId: Int,
    val groupId: Int,
    val prices: List<Pair<Product, Price?>>
)

object PricingController {
    val queue = Queue()

    private var nextUpdateAt: DateTime = DateTime(0)
    private var map: MutableMap<SetDescription, List<CategoryGroup>> = mutableMapOf()

    suspend fun price(set: SetDescription, number: Int): Price? {
        loadPricing()

        var price: Price? = null

        // improvement : precalculate the maps from set/number directly
        map[set]?.fastForEach { categoryGroup ->
            if (null != price) return@fastForEach

            val found = categoryGroup.prices.find { it.first.number == number }?.second

            if (null != found) {
                price = found
            }
        }

        println("found $price")

        return price
    }

    suspend fun loadPricing() = suspendCoroutine<Unit> { continuation ->
        queue.post {
            if (map.isNotEmpty() && nextUpdateAt > DateTime.now()) {
                continuation.resume(Unit)
                return@post
            }

            val map: MutableMap<SetDescription, List<CategoryGroup>> = mutableMapOf()

            SetsPricingMapping.maps.forEach { (set, originalListing) ->
                originalListing.forEach { wrapper ->
                    val list: MutableList<CategoryGroup> = mutableListOf()
                    val products = RequestLoader.products(wrapper.categoryId, wrapper.groupId)
                    val prices = RequestLoader.prices(wrapper.categoryId, wrapper.groupId)

                    list.add(
                        CategoryGroup(
                            wrapper.categoryId,
                            wrapper.groupId,
                            products.map { product ->
                                product to prices.find { it.productId == product.productId }
                            }
                        )
                    )

                    println(list)

                    products
                        .sortedBy { it.number }
                        .map { println("${it.categoryId} ${it.groupId} ${it.productId} ${it.number}") }

                    map[set] = list
                }
            }

            PricingController.map = map
            nextUpdateAt = DateTime.now().plus(1.hours)

            continuation.resume(Unit)
        }
    }
}
