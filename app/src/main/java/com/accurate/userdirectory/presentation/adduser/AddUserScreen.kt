package com.accurate.userdirectory.presentation.adduser
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AccurateColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .statusBarsPadding()
        ) {
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
                ),
                windowInsets = WindowInsets(0.dp)
            )

            Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(AccurateColors.PrimaryPink))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name
                AccurateTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChanged,
                    label = "Nama Lengkap",
                    placeholder = "Masukkan nama lengkap",
                    isRequired = true,
                    isError = state.fieldErrors.containsKey("name"),
                    errorMessage = state.fieldErrors["name"]
                )

                // Email
                AccurateTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = "Email",
                    placeholder = "nama@email.com",
                    isRequired = true,
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                    isError = state.fieldErrors.containsKey("email"),
                    errorMessage = state.fieldErrors["email"]
                )

                // Phone
                AccurateTextField(
                    value = state.phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChanged,
                    label = "No. Handphone",
                    placeholder = "08xxxxxxxxxx",
                    isRequired = true,
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone,
                    isError = state.fieldErrors.containsKey("phoneNumber"),
                    errorMessage = state.fieldErrors["phoneNumber"]
                )

                // Address
                AccurateTextField(
                    value = state.address,
                    onValueChange = viewModel::onAddressChanged,
                    label = "Alamat",
                    placeholder = "Masukkan alamat lengkap",
                    isRequired = false,
                    minLines = 3,
                    maxLines = 5,
                    singleLine = false
                )

                // City Dropdown
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = buildAnnotatedString {
                            append("Kota")
                            withStyle(SpanStyle(color = AccurateColors.Error)) {
                                append(" *")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = AccurateColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = cityDropdownExpanded,
                        onExpandedChange = { cityDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.city,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Pilih kota", color = AccurateColors.TextTertiary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded) },
                            isError = state.fieldErrors.containsKey("city"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccurateColors.Border,
                                unfocusedBorderColor = AccurateColors.Border,
                                errorBorderColor = AccurateColors.Error,
                                focusedContainerColor = AccurateColors.Surface,
                                unfocusedContainerColor = AccurateColors.Surface
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
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                // Gender
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = buildAnnotatedString {
                            append("Jenis Kelamin")
                            withStyle(SpanStyle(color = AccurateColors.Error)) {
                                append(" *")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = AccurateColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = AccurateColors.PrimaryPink,
                                        unselectedColor = AccurateColors.Border
                                    )
                                )
                                Text(
                                    gender.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AccurateColors.TextPrimary
                                )
                            }
                        }
                    }
                    if (state.fieldErrors.containsKey("gender")) {
                        Text(
                            state.fieldErrors["gender"] ?: "",
                            color = AccurateColors.Error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                AccurateButton(
                    text = if (state.isEditMode) "Simpan Perubahan" else "Simpan User",
                    onClick = { viewModel.onSubmit() },
                    enabled = true,
                    isLoading = state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
