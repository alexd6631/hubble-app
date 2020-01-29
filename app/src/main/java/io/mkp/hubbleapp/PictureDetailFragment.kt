package io.mkp.hubbleapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.unaryPlus
import androidx.fragment.app.Fragment
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.foundation.DrawImage
import androidx.ui.foundation.SimpleImage
import androidx.ui.layout.*
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.MaterialTheme
import io.mkp.hubbleapp.utils.image
import io.mkp.hubbleapp.utils.observe
import io.monkeypatch.konfetti.mvvm.livedata.toLivedata


class PictureDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val id = arguments?.getString("id") ?: error("No id provided")
        val viewModel = Container.pictureDetailViewModel(id)

        val layout = LinearLayout(requireContext())
        layout.setContent {
            MaterialTheme {
                val detail = +observe(viewModel.detail.toLivedata)
                if (detail != null) {
                    val image = +image(detail.imageUrl!!)
                    if (image != null) {
                        Container(modifier = Expanded) {
                            SimpleImage(image)
                        }
                    } else {
                        ShowProgress()
                    }
                } else {
                    ShowProgress()
                }

            }
        }
        return layout
    }
}