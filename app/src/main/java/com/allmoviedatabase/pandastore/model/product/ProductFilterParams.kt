package com.allmoviedatabase.pandastore.model.product

data class ProductFilterParams(
    var page: Int = 1,
    var searchQuery: String? = null,
    var categoryId: Int? = null,
    var minPrice: Double? = null,
    var maxPrice: Double? = null,
    var sortBy: String? = null, // Varsayılan sıralama yok
    var brand: String? = null
) {
    // Filtreleri sıfırlamak için
    fun clearFilters() {
        page = 1
        searchQuery = null
        categoryId = null
        minPrice = null
        maxPrice = null
        sortBy = null
        brand = null
    }
}