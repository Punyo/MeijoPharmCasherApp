package com.punyo.casherapp.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.punyo.casherapp.ui.theme.ThemeMode
import meijopharmcasherapp.composeapp.generated.resources.Res
import meijopharmcasherapp.composeapp.generated.resources.settings_theme_dark
import meijopharmcasherapp.composeapp.generated.resources.settings_theme_light
import meijopharmcasherapp.composeapp.generated.resources.settings_theme_section_title
import meijopharmcasherapp.composeapp.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinInject()) {
    val currentThemeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        ThemeSection(
            currentThemeMode = currentThemeMode,
            onThemeModeSelected = { viewModel.setThemeMode(it) },
        )
    }
}

@Composable
private fun ThemeSection(
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.settings_theme_section_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Column(modifier = Modifier.selectableGroup()) {
                ThemeModeOption(
                    text = stringResource(Res.string.settings_theme_light),
                    selected = currentThemeMode == ThemeMode.LIGHT,
                    onClick = { onThemeModeSelected(ThemeMode.LIGHT) },
                )

                ThemeModeOption(
                    text = stringResource(Res.string.settings_theme_dark),
                    selected = currentThemeMode == ThemeMode.DARK,
                    onClick = { onThemeModeSelected(ThemeMode.DARK) },
                )
            }
        }
    }
}

@Composable
private fun ThemeModeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
