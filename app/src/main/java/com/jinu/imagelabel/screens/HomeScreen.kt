package com.jinu.imagelabel.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jinu.imagelabel.R
import com.jinu.imagelabel.mvvm.MainViewModel
import com.jinu.imagelabel.navigation.Screens
import com.jinu.imagelabel.ui.theme.items.ModelSelection

class HomeScreen(private val navController: NavController, private val viewModel: MainViewModel) {
    @Composable
    fun View() {
        Surface {
            val staLazy = rememberLazyListState()
            Column(modifier = Modifier.fillMaxSize()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 30.dp, end = 10.dp)
                        .fillMaxHeight(0.35f)
                ) {
                    Text(
                        "DoctorAI", fontWeight = FontWeight(1000),
                        fontSize = 46.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily(
                            Font(
                                resId = R.font.roboto_mono_medium,
                                FontWeight.SemiBold,
                                FontStyle.Normal
                            )
                        )
                    )
                    Text(
                        stringResource(id = R.string.discription), fontWeight = FontWeight(1000),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 30.dp, top = 10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily(
                            Font(
                                resId = R.font.roboto_mono_thin,
                                FontWeight.Thin,
                                FontStyle.Normal
                            )
                        ),
                    )
                }


                Text(
                    stringResource(id = R.string.available_models), fontWeight = FontWeight(1000),
                    fontSize = 23.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 30.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily(
                        Font(
                            resId = R.font.roboto_mono_medium,
                            FontWeight.ExtraBold,
                            FontStyle.Normal
                        )
                    ),
                )

                val list = arrayListOf<ModelSelection>()
                list.add(ModelSelection(img = R.drawable.brain_tumor, "Brain Tumour", ""))
                list.add(ModelSelection(img = R.drawable.brain_tumor, "", ""))
                list.add(ModelSelection(img = R.drawable.brain_tumor, "", ""))



                LazyRow(
                    modifier = Modifier
                        .fillMaxHeight(0.7f)
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 20.dp), state = staLazy
                ) {
                    items(list) {
                        Box(
                            Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            Image(
                                painter = painterResource(id = it.img),
                                contentDescription = "",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Fit
                            )

                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .align(
                                        Alignment.BottomCenter
                                    ),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Text(
                                    it.title, fontWeight = FontWeight(1000),
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontFamily = FontFamily(
                                        Font(
                                            resId = R.font.roboto_mono_medium,
                                            FontWeight.ExtraBold,
                                            FontStyle.Normal
                                        )
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }

            }
        }


    }
}