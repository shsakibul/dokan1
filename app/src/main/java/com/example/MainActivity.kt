package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight
import com.example.data.SecurityManager
import com.example.ui.Screen
import com.example.ui.ShopViewModel
import com.example.ui.components.SimpleTopAppBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: ShopViewModel = viewModel()
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: ShopViewModel) {
    val context = LocalContext.current
    var isUnlocked by remember { mutableStateOf(!SecurityManager.isPinEnabled(context)) }

    if (!isUnlocked) {
        Surface(modifier = Modifier.fillMaxSize()) {
            PinLockScreen(onCorrectPin = { isUnlocked = true })
        }
    } else {
        val currentScreen = viewModel.navigationStack.lastOrNull() ?: Screen.Dashboard

        // Intercept system Back button
        val canGoBack = viewModel.navigationStack.size > 1
        BackHandler(enabled = canGoBack) {
            viewModel.navigateBack()
        }

        // Identify current tab index
        val activeTabIndex = when (currentScreen) {
            Screen.Dashboard -> 0
            Screen.Products, Screen.AddProduct -> 1
            Screen.DueKhata, Screen.AddCustomer, is Screen.CustomerDetail, is Screen.CustomerAddDue -> 2
            Screen.History -> 3
            Screen.Settings, Screen.CategoryManagement -> 4
        }

        // Determine Top Bar details
        val topBarTitle = when (currentScreen) {
            Screen.Dashboard -> "সহজ হিসাব — ড্যাশবোর্ড"
            Screen.Products -> "দোকানের পণ্য তালিকা"
            Screen.AddProduct -> "নতুন পণ্য যোগ"
            Screen.DueKhata -> "বাকির খাতা (লেনদেন)"
            Screen.AddCustomer -> "নতুন বাকি খাতা"
            is Screen.CustomerDetail -> "খাতা বিবরণী"
            is Screen.CustomerAddDue -> "বাকি যোগ করুন"
            Screen.History -> "লেনদেনের ইতিহাস"
            Screen.Settings -> "দোকান সেটিংস"
            Screen.CategoryManagement -> "ক্যাটাগরি ব্যবস্থাপনা"
        }

        val showBackButton = currentScreen !in listOf(
            Screen.Dashboard,
            Screen.Products,
            Screen.DueKhata,
            Screen.History,
            Screen.Settings
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SimpleTopAppBar(
                    title = topBarTitle,
                    navigationIcon = if (showBackButton) Icons.Default.ArrowBack else null,
                    onNavigationClick = { viewModel.navigateBack() }
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    val navItems = listOf(
                        NavigationItem("ড্যাশবোর্ড", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, Screen.Dashboard, "nav_dashboard"),
                        NavigationItem("পণ্য", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag, Screen.Products, "nav_products"),
                        NavigationItem("বাকির খাতা", Icons.Filled.People, Icons.Outlined.People, Screen.DueKhata, "nav_due_khata"),
                        NavigationItem("হিস্ট্রি", Icons.Filled.History, Icons.Outlined.History, Screen.History, "nav_history"),
                        NavigationItem("সেটিংস", Icons.Filled.Settings, Icons.Outlined.Settings, Screen.Settings, "nav_settings")
                    )

                    navItems.forEachIndexed { index, item ->
                        val isSelected = activeTabIndex == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                // Tab Selection Behavior: Clear stack and navigate
                                viewModel.navigationStack.clear()
                                viewModel.navigationStack.add(Screen.Dashboard)
                                if (item.screen != Screen.Dashboard) {
                                    viewModel.navigationStack.add(item.screen)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label, fontSize = 11.sp) },
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    Screen.Dashboard -> DashboardScreen(viewModel = viewModel)
                    Screen.Products -> ProductsScreen(viewModel = viewModel)
                    Screen.AddProduct -> AddProductScreen(viewModel = viewModel)
                    Screen.DueKhata -> DueKhataScreen(viewModel = viewModel)
                    Screen.AddCustomer -> AddCustomerScreen(viewModel = viewModel)
                    is Screen.CustomerDetail -> CustomerDetailScreen(customerId = currentScreen.customerId, viewModel = viewModel)
                    Screen.History -> HistoryScreen(viewModel = viewModel)
                    Screen.Settings -> SettingsScreen(viewModel = viewModel)
                    Screen.CategoryManagement -> CategoryManagementScreen(viewModel = viewModel)
                    else -> DashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PinLockScreen(onCorrectPin: () -> Unit) {
    val context = LocalContext.current
    val savedPin = remember { SecurityManager.getPin(context) ?: "" }
    var enteredPin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "সহজ হিসাব — নিরাপত্তা লক",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "অ্যাপে প্রবেশ করতে ৪ সংখ্যার পিন কোড দিন",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(30.dp))

        // Bullet dots for visual feedback
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..4) {
                val isFilled = enteredPin.length >= i
                val color = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                val size = if (isFilled) 18.dp else 14.dp
                Box(
                    modifier = Modifier
                        .size(size)
                        .background(color, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (showError) {
            Text("ভুল পিন কোড! আবার চেষ্টা করুন।", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Custom Numeric Keypad
        val keys = listOf(
            listOf("১", "২", "৩"),
            listOf("৪", "৫", "৬"),
            listOf("৭", "৮", "৯"),
            listOf("C", "০", "⌫")
        )

        val digitMap = mapOf(
            "১" to "1", "২" to "2", "৩" to "3",
            "৪" to "4", "৫" to "5", "৬" to "6",
            "৭" to "7", "৮" to "8", "৯" to "9", "০" to "0"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            keys.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    row.forEach { key ->
                        val isSpecial = key == "C" || key == "⌫"
                        OutlinedButton(
                            onClick = {
                                showError = false
                                if (key == "C") {
                                    enteredPin = ""
                                } else if (key == "⌫") {
                                    if (enteredPin.isNotEmpty()) {
                                        enteredPin = enteredPin.dropLast(1)
                                    }
                                } else {
                                    if (enteredPin.length < 4) {
                                        enteredPin += digitMap[key] ?: ""
                                        if (enteredPin.length == 4) {
                                            if (enteredPin == savedPin) {
                                                onCorrectPin()
                                            } else {
                                                showError = true
                                                enteredPin = ""
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(72.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSpecial) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
                            )
                        ) {
                            Text(
                                text = key,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSpecial) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NavigationItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val screen: Screen,
    val testTag: String
)
