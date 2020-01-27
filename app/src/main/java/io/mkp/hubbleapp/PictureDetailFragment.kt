package io.mkp.hubbleapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.unaryPlus
import androidx.fragment.app.Fragment
import androidx.ui.core.setContent
import androidx.ui.foundation.DrawImage
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.MaterialTheme
import io.mkp.hubbleapp.utils.image


class PictureDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("Detail view created")

        val layout = LinearLayout(requireContext())
        layout.setContent {
            MaterialTheme {
                val image = +image("http://google.fr")
                if (image != null) {
                    DrawImage(image)
                } else {
                    Column(
                        mainAxisAlignment = MainAxisAlignment.Center,
                        crossAxisAlignment = CrossAxisAlignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        return layout
    }
}