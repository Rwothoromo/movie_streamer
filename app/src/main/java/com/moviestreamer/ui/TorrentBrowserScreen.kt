package com.moviestreamer.ui

import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviestreamer.R
import com.moviestreamer.player.PlayerActivity

private data class LegalTorrentSource(
    val title: String,
    val description: String,
    val browseUrl: String
)

@androidx.media3.common.util.UnstableApi
@Composable
fun TorrentBrowserScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var magnetLink by remember { mutableStateOf("") }

    val legalSources = remember {
        listOf(
            LegalTorrentSource(
                title = "Internet Archive — Feature Films",
                description = "Browse public-domain and open-license movies. Many items provide legal torrent downloads.",
                browseUrl = "https://archive.org/details/feature_films"
            ),
            LegalTorrentSource(
                title = "Internet Archive — Open Source Movies",
                description = "Open culture, documentaries, animation, and community-released films with permissive licensing.",
                browseUrl = "https://archive.org/details/opensource_movies"
            ),
            LegalTorrentSource(
                title = "Public Domain Torrents",
                description = "Classic public-domain films that can be legally downloaded or streamed via torrent.",
                browseUrl = "https://publicdomaintorrents.info/"
            )
        )
    }

    fun startStreamingFromMagnet() {
        val trimmedMagnet = magnetLink.trim()
        if (trimmedMagnet.isBlank()) {
            Toast.makeText(context, context.getString(R.string.enter_magnet_link), Toast.LENGTH_SHORT).show()
            return
        }

        val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: context.filesDir
        viewModel.startTorrentStream(trimmedMagnet, downloadDir) { file ->
            if (file != null && file.exists()) {
                val intent = Intent(context, PlayerActivity::class.java).apply {
                    putExtra(PlayerActivity.EXTRA_VIDEO_URL, file.toURI().toString())
                    putExtra(PlayerActivity.EXTRA_MOVIE_TITLE, "Torrent Stream")
                    putExtra(PlayerActivity.EXTRA_CONTENT_ID, "torrent_${trimmedMagnet.hashCode()}")
                }
                context.startActivity(intent)
                viewModel.stopTorrentStream()
            } else {
                Toast.makeText(context, context.getString(R.string.failed_torrent_stream), Toast.LENGTH_LONG).show()
                viewModel.stopTorrentStream()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack) {
                    Text(stringResource(R.string.back), color = Color.White)
                }
                Button(
                    onClick = { magnetLink = "" },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
                ) {
                    Text(stringResource(R.string.dismiss), color = Color.White)
                }
            }
        }

        item {
            Column {
                Text(
                    text = stringResource(R.string.torrent_browser_title),
                    color = Color.White,
                    fontSize = 34.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.torrent_browser_subtitle),
                    color = Color.LightGray,
                    fontSize = 16.sp
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.torrent_browser_note),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.torrent_browser_tip),
                        color = Color(0xFFBDBDBD),
                        fontSize = 14.sp
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.enter_magnet_link),
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = magnetLink,
                        onValueChange = { magnetLink = it },
                        label = { Text(stringResource(R.string.magnet_uri)) },
                        placeholder = { Text("magnet:?xt=urn:btih:...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = ::startStreamingFromMagnet,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D7D46))
                        ) {
                            Text(stringResource(R.string.start_streaming), color = Color.White)
                        }
                        OutlinedButton(onClick = { magnetLink = "" }) {
                            Text(stringResource(R.string.cancel), color = Color.White)
                        }
                    }
                }
            }
        }

        if (uiState.torrentStatus != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2A1F))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = uiState.torrentStatus.orEmpty(),
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = uiState.torrentProgress.coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = viewModel::stopTorrentStream) {
                            Text(stringResource(R.string.cancel), color = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.browse_legal_sources),
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(legalSources) { source ->
            LegalTorrentSourceCard(
                source = source,
                onOpen = { uriHandler.openUri(source.browseUrl) }
            )
        }
    }
}

@Composable
private fun LegalTorrentSourceCard(
    source: LegalTorrentSource,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = source.title,
                color = Color.White,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = source.description,
                color = Color(0xFFBDBDBD),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onOpen,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF))
            ) {
                Text(stringResource(R.string.open_source_site), color = Color.White)
            }
        }
    }
}
