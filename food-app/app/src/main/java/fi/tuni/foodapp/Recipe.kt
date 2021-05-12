package fi.tuni.foodapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.net.URL

@JsonIgnoreProperties(ignoreUnknown = true)
data class Recipe(var id: Int? = null, var title: String? = null, var image: URL? = null, var imageType: String? = null)