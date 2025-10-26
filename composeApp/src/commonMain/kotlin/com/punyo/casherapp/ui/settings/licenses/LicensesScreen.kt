package com.punyo.casherapp.ui.settings.licenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import com.punyo.casherapp.ui.component.NavigateBackButton
import meijopharmcasherapp.composeapp.generated.resources.Res
import meijopharmcasherapp.composeapp.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun LicensesScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    val libraries by rememberLibraries {
        Res.readBytes("files/aboutlibraries.json").decodeToString()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        NavigateBackButton(
            onNavigateBack = onNavigateBack,
            text = stringResource(Res.string.settings_title),
        )

        LibrariesContainer(
            modifier = Modifier.fillMaxSize(),
            colors = LibraryDefaults.libraryColors(),
            libraries = libraries,
        )
    }
}
