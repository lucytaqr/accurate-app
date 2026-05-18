package com.accurate.userdirectory.presentation.adduser

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.accurate.userdirectory.core.designsystem.AccurateColors
import com.accurate.userdirectory.core.designsystem.components.AccurateButton
import com.accurate.userdirectory.core.designsystem.components.AccurateTextField
import com.accurate.userdirectory.domain.model.Gender

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddUserViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.onPhotoSelected(uri?.toString())
    }

    LaunchedEffect(state.submitSuccess) {
        if (state.submitSuccess) onNavigateBack()
    }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it.text)
            viewModel.onClearMessage()
        }
    }

    var cityDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Edit User" else "Tambah User", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AccurateColors.Surface,
                    titleContentColor = AccurateColors.TextPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AccurateColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo Picker
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(AccurateColors.SurfaceSoft)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!state.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = state.photoUri,
                        contentDescription = "Foto User",
                        modifier = Modifier.size(100.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(AccurateColors.TextPrimary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = "Ganti Foto",
                            tint = AccurateColors.Surface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = "Tambah Foto",
                            tint = AccurateColors.TextTertiary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Foto",
                            style = MaterialTheme.typography.bodySmall,
                            color = AccurateColors.TextTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Name
            AccurateTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                label = "Nama Lengkap",
                placeholder = "Masukkan nama lengkap",
                isError = state.fieldErrors.containsKey("name"),
                errorMessage = state.fieldErrors["name"]
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Email
            AccurateTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChanged,
                label = "Email",
                placeholder = "contoh@email.com",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                isError = state.fieldErrors.containsKey("email"),
                errorMessage = state.fieldErrors["email"]
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Phone
            AccurateTextField(
                value = state.phoneNumber,
                onValueChange = viewModel::onPhoneNumberChanged,
                label = "No. Handphone",
                placeholder = "08123456789",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone,
                isError = state.fieldErrors.containsKey("phoneNumber"),
                errorMessage = state.fieldErrors["phoneNumber"]
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Address
            AccurateTextField(
                value = state.address,
                onValueChange = viewModel::onAddressChanged,
                label = "Alamat",
                placeholder = "Masukkan alamat",
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))

            // City Dropdown
            ExposedDropdownMenuBox(
                expanded = cityDropdownExpanded,
                onExpandedChange = { cityDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.city,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kota") },
                    placeholder = { Text("Pilih kota") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded) },
                    isError = state.fieldErrors.containsKey("city"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccurateColors.PrimaryPink,
                        unfocusedBorderColor = AccurateColors.Border
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = cityDropdownExpanded,
                    onDismissRequest = { cityDropdownExpanded = false }
                ) {
                    state.cities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city.name) },
                            onClick = {
                                viewModel.onCityChanged(city.name)
                                cityDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            if (state.fieldErrors.containsKey("city")) {
                Text(
                    state.fieldErrors["city"] ?: "",
                    color = AccurateColors.Error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Gender
            Text(
                "Jenis Kelamin",
                style = MaterialTheme.typography.bodyLarge,
                color = AccurateColors.TextPrimary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                listOf(Gender.Male, Gender.Female).forEach { gender ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.onGenderChanged(gender) }
                    ) {
                        RadioButton(
                            selected = state.gender == gender,
                            onClick = { viewModel.onGenderChanged(gender) },
                            colors = RadioButtonDefaults.colors(selectedColor = AccurateColors.PrimaryPink)
                        )
                        Text(gender.displayName, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            if (state.fieldErrors.containsKey("gender")) {
                Text(
                    state.fieldErrors["gender"] ?: "",
                    color = AccurateColors.Error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            AccurateButton(
                text = if (state.isEditMode) "Simpan Perubahan" else "Simpan User",
                onClick = { viewModel.onSubmit() },
                enabled = true,
                isLoading = state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
