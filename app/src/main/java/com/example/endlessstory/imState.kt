package com.example.endlessstory
import android.media.Image

sealed interface ImState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : ImState

    /**
     * Still loading
     */
    object Loading : ImState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : ImState

    /**
     * The image was generated
     */
    data class Images(val new_images: List<Image>) : ImState

}
