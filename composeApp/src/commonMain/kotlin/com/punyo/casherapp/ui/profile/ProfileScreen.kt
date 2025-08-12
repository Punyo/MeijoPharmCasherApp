package com.punyo.casherapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import casherapplication.composeapp.generated.resources.Res
import casherapplication.composeapp.generated.resources.*

@Composable
fun ProfileScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = stringResource(Res.string.nav_profile),
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.profile_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(Res.string.profile_description),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}