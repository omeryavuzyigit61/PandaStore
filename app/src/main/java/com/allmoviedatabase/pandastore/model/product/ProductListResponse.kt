package com.allmoviedatabase.pandastore.model.product

data class ProductListResponse(
    val data: List<ProductDto>,
    val meta: MetaDto
)