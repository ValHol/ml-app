package ar.valhol.mlapp.data

data class Category(val id: String,
                    val name: String,
                    val picture: String?)

data class Address(val state_id: String,
                   val state_name: String,
                   val city_id: String,
                   val city_name: String)

data class Result(val id: String,
                  val title: String,
                  val price: Number,
                  val currency_id: String,
                  val available_quantity: Number,
                  val sold_quantity: Number,
                  val condition: String,
                  val thumbnail: String,
                  val address: Address)

data class ApiSearch(val site_id: String,
                     val results: List<Result>)