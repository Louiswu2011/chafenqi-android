package com.nltv.chafenqi.view.updater

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nltv.chafenqi.R
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.extension.nameplateChunithmBottomColor
import com.nltv.chafenqi.extension.nameplateMaimaiBottomColor
import com.nltv.chafenqi.storage.SettingsStore
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UpdaterQRCodePage(snackbarHostState: SnackbarHostState) {
    val model: UpdaterViewModel = viewModel()
    val uriHandler = LocalUriHandler.current
    val logoPainter = painterResource(id = R.drawable.app_icon)
    val pagerState = rememberPagerState { 2 }

    val context = LocalContext.current
    val store = SettingsStore(context)

    val shouldUseAlternativeUrl by store.uploadShouldUseAlternativeUrl.collectAsStateWithLifecycle(initialValue = false)

    val maiQrCodePainter = rememberQrCodePainter(model.buildUri(1, shouldUseAlternativeUrl)) {
        logo {
            painter = logoPainter
            padding = QrLogoPadding.Natural(.1f)
            shape = QrLogoShape.roundCorners(.5f)
            size = 0.2f
        }

        shapes {
            darkPixel = QrPixelShape.Default
        }

        colors {
            dark = QrBrush.solid(nameplateMaimaiBottomColor)
        }
    }
    val chuQrCodePainter = rememberQrCodePainter(model.buildUri(0, shouldUseAlternativeUrl)) {
        logo {
            painter = logoPainter
            padding = QrLogoPadding.Natural(.1f)
            shape = QrLogoShape.roundCorners(.5f)
            size = 0.2f
        }

        shapes {
            darkPixel = QrPixelShape.Default
        }

        colors {
            dark = QrBrush.solid(nameplateChunithmBottomColor)
        }
    }

    ModalBottomSheet(onDismissRequest = { model.shouldShowQRCode = false }) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Box {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (it) {
                        0 -> Image(
                            painter = maiQrCodePainter,
                            contentDescription = "maimai QRCode",
                            Modifier.fillMaxWidth()

                        )

                        1 -> Image(
                            painter = chuQrCodePainter,
                            contentDescription = "chunithm QRCode",
                            Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(vertical = SCREEN_PADDING),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    model.openWeChatScan(uriHandler, snackbarHostState)
                }) {
                    Text(text = "跳转到微信")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "为了保证您的数据安全，请不要随意分享您的二维码")
            }
        }
    }
}
