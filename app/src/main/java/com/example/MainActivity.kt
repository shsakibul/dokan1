package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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

data class NavigationItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val screen: Screen,
    val testTag: String
)
