package io.mkp.hubbleapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Card
import androidx.ui.tooling.preview.Preview
import io.mkp.hubbleapp.list.ListPicturesViewModel
import io.mkp.hubbleapp.core.models.HubblePicture
import utils.observe
import io.monkeypatch.konfetti.mvvm.livedata.toLivedata

class ListPicturesFragment : Fragment() {
    private val viewModel by lazy {
        Container.listPicturesViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("I am created")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = LinearLayout(requireContext())
        layout.setContent {
            MaterialTheme {
                ListPicturesView(viewModel) { pic ->
                    findNavController().navigate(R.id.pictureDetailFragment, bundleOf("id" to pic.id))
                }
            }
        }
        return layout
    }
}

typealias PicSelected = (HubblePicture) -> Unit

@Composable
fun ListPicturesView(
    viewModel: ListPicturesViewModel,
    picSelected: PicSelected
) {
    val pics = +observe(viewModel.pictures.toLivedata)
    val loading = pics == null

    if (loading) {
        ShowProgress()
    } else {
        FlexColumn(
            mainAxisSize = LayoutSize.Expand,
            crossAxisSize = LayoutSize.Expand,
            crossAxisAlignment = CrossAxisAlignment.Center
        ) {
            inflexible {
                Padding(16.dp) {
                    Column {
                        Padding(bottom = 8.dp) {
                            TextField(
                                value = viewModel.filter.value ?: "",
                                onValueChange = { viewModel.filter.value = it }
                            )
                        }
                        Divider(color = Color.Blue, height = 1.dp)
                    }

                }
            }

            flexible(flex = 1f) {
                PicturesList(pics, picSelected)
            }
        }
    }
}

@Composable
fun ShowProgress() {
    Column(
        mainAxisSize = LayoutSize.Expand,
        mainAxisAlignment = MainAxisAlignment.Center,
        crossAxisAlignment = CrossAxisAlignment.Center
    ) {
        androidx.ui.layout.Container(expanded = true) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun PicturesList(
    pics: List<HubblePicture>?,
    picSelected: PicSelected
) {
    VerticalScroller {
        Column(modifier = Spacing(16.dp)) {
            pics?.forEach { pic ->
                PictureCard(pic, picSelected)
            }
        }
    }
}

@Composable
private fun PictureCard(
    pic: HubblePicture,
    picSelected: PicSelected
) {
    Ripple(bounded = true) {
        Clickable(onClick = { picSelected(pic) }) {
            Card {
                Column(modifier = Spacing(16.dp), crossAxisSize = LayoutSize.Expand) {
                    Text(pic.name)
                    Text(pic.mission)
                }

            }
        }
    }

    HeightSpacer(height = 8.dp)
}

@Preview
@Composable
fun PictureCardPreview() {
    MaterialTheme {
        PictureCard(
            HubblePicture(
                "id1", "Name", "mission"
            )
        ) { }
    }
}

@Preview
@Composable
fun PictureListsPreview() {
    MaterialTheme {
        PicturesList(
            listOf(
                HubblePicture(
                    "id1", "Name", "mission"
                ),
                HubblePicture(
                    "id2", "Name", "mission"
                )
            )
        ) { }
    }
}