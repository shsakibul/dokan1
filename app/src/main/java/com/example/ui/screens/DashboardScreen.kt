package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.*
import com.example.ui.components.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val notifications by viewModel.smartNotifications.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val products by viewModel.products.collectAsState()
    val selectedTimeFilter by viewModel.selectedTimeFilter.collectAsState()

    var showNotifDialog by remember { mutableStateOf(false) }
    var showExpenseDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

    val shopName = "হাসান ভাইয়ের মুদির দোকান"
    val dateText = remember {
        val cal = Calendar.getInstance()
        val day = convertToBanglaNumerals(cal.get(Calendar.DAY_OF_MONTH).toString())
        val year = convertToBanglaNumerals(cal.get(Calendar.YEAR).toString())
        val months = arrayOf(
            "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
        )
        "$day ${months[cal.get(Calendar.MONTH)]} $year"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Welcome Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = shopName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = dateText,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = { showNotifDialog = true },
                    modifier = Modifier.testTag("notification_bell_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "সতর্কতা ও নোটিফিকেশন",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                if (notifications.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = convertToBanglaNumerals(notifications.size.toString()),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time Period Selection Row
        Text(
            text = "হিসাবের সময়সীমা নির্ধারণ করুন:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val filters = listOf(
                "TODAY" to "আজ",
                "YESTERDAY" to "গতকাল",
                "WEEK" to "সপ্তাহ",
                "MONTH" to "এই মাস",
                "ALL" to "সব"
            )
            filters.forEach { (id, label) ->
                val selected = selectedTimeFilter == id
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.selectedTimeFilter.value = id },
                    label = { Text(label, fontSize = 12.sp) },
                    modifier = Modifier.testTag("time_filter_${id.lowercase()}")
                )
            }
        }

        // Metrics Section with Professional Polish theme
        val textSuffix = if (selectedTimeFilter == "TODAY") " (আজ)" else ""

        // 1. Grand Balance Card (Modern Blue)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "মোট বিক্রয়$textSuffix",
                    fontSize = 13.sp,
                    color = Color(0xFFD1E4FF),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatBengaliAmount(stats.totalSales),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "মোট লাভ$textSuffix",
                            fontSize = 11.sp,
                            color = Color(0xFFD1E4FF),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatBengaliAmount(stats.totalProfit),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4ADE80))
                            )
                            Text(
                                text = "সক্রিয়",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Metrics Grid (Crisp 2x2 Slate-bordered cards)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    title = "দোকানের মোট বাকি",
                    value = formatBengaliAmount(stats.totalDues),
                    icon = Icons.Default.AccountBalanceWallet,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "স্টক আনুমানিক মূল্য",
                    value = formatBengaliAmount(stats.totalStockValue),
                    icon = Icons.Default.Store,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    title = "মোট ব্যয়$textSuffix",
                    value = formatBengaliAmount(stats.totalExpenses),
                    icon = Icons.Default.TrendingDown,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color(0xFF475569),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "মোট লেনদেন$textSuffix",
                    value = "${convertToBanglaNumerals(stats.transactionsCount.toString())}টি",
                    icon = Icons.Default.History,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color(0xFF475569),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Action Buttons (Polished modern style)
        Text(
            text = "দ্রুত কাজ সম্পাদন:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Quick Sell
            Card(
                onClick = { viewModel.navigateTo(Screen.Products) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("quick_action_sell")
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "দ্রুত বিক্রয়",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            // New Product
            Card(
                onClick = { viewModel.navigateTo(Screen.AddProduct) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0FDF4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "নতুন পণ্য",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            // Add Due / Customer
            Card(
                onClick = { viewModel.navigateTo(Screen.AddCustomer) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF2F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "বাকি হিসাব",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Collect Payment Card
            Card(
                onClick = { showPaymentDialog = true },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.weight(1.5f)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "পরিশোধ গ্রহণ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            // Record Expense Card
            Card(
                onClick = { showExpenseDialog = true },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.weight(1.5f)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = Color(0xFF4B5563),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ব্যয় হিসাব লিখুন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weekly Sales Trend (Beautiful Native Compose Canvas Chart)
        Text(
            text = "গত ৭ দিনের মোট বিক্রয় বিশ্লেষণ:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .padding(bottom = 16.dp)
        ) {
            val recentTxs = transactions.filter {
                it.date >= System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000 &&
                (it.type == "SALE" || it.type == "DUE_SALE")
            }

            // Generate 7 days labels and sums
            val daysData = remember(transactions) {
                List(7) { index ->
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DAY_OF_YEAR, -(6 - index))
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    val dayStart = cal.timeInMillis
                    val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1

                    val total = recentTxs.filter { it.date in dayStart..dayEnd }.sumOf { it.amount }
                    val dayNum = convertToBanglaNumerals(cal.get(Calendar.DAY_OF_MONTH).toString())
                    val dayName = when (cal.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.SUNDAY -> "রবি"
                        Calendar.MONDAY -> "সোম"
                        Calendar.TUESDAY -> "মঙ্গল"
                        Calendar.WEDNESDAY -> "বুধ"
                        Calendar.THURSDAY -> "বৃহঃ"
                        Calendar.FRIDAY -> "শুক্র"
                        Calendar.SATURDAY -> "শনি"
                        else -> ""
                    }
                    Pair(total, "$dayNum\n$dayName")
                }
            }

            val maxVal = remember(daysData) {
                val max = daysData.maxOf { it.first }
                if (max == 0.0) 1000.0 else max
            }

            if (recentTxs.isEmpty()) {
                // Empty state view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "চার্ট দেখানোর জন্য পর্যাপ্ত তথ্য নেই।",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "দোকানের বিক্রি শুরু করলে চার্ট স্বয়ংক্রিয়ভাবে দৃশ্যমান হবে।",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    daysData.forEach { (amount, label) ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // Amount text bubble
                            if (amount > 0.0) {
                                Text(
                                    text = convertToBanglaNumerals(amount.toInt().toString()),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            // Column Bar representation
                            val heightPercent = (amount / maxVal).toFloat()
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(0.7f)
                                    .width(18.dp)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.primaryContainer
                                            )
                                        )
                                    )
                                    .fillMaxHeight(heightPercent)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            // Label
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // 1. Notification Warnings Dialog
    if (showNotifDialog) {
        Dialog(onDismissRequest = { showNotifDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "সতর্কতা ও নোটিফিকেশন তালিকা",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    if (notifications.isEmpty()) {
                        Text(
                            text = "বর্তমানে কোনো সতর্কতা নেই। আপনার দোকানের সব হিসাব ও স্টক নিখুঁত অবস্থায় রয়েছে!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 24.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Box(modifier = Modifier.heightIn(max = 300.dp)) {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(notifications) { notif ->
                                    val iconColor = when (notif.type) {
                                        "ERROR" -> Color.Red
                                        "WARNING" -> MaterialTheme.colorScheme.secondary
                                        "SUCCESS" -> Color(0xFF2E7D32)
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                    val bgNotifColor = iconColor.copy(alpha = 0.08f)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(bgNotifColor)
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (notif.type) {
                                                "ERROR" -> Icons.Default.Cancel
                                                "WARNING" -> Icons.Default.Warning
                                                "SUCCESS" -> Icons.Default.CheckCircle
                                                else -> Icons.Default.Info
                                            },
                                            contentDescription = null,
                                            tint = iconColor,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = notif.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = notif.message,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showNotifDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("বন্ধ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 2. record other expenses
    if (showExpenseDialog) {
        var expTitle by remember { mutableStateOf("") }
        var expAmount by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { 
            focusManager.clearFocus()
            showExpenseDialog = false 
        }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "অন্যান্য ব্যয় যুক্ত করুন",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = expTitle,
                        onValueChange = { expTitle = it },
                        label = { Text("ব্যয়ের বিবরণ (যেমন: বিদ্যুৎ বিল, দোকান ভাড়া)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = expAmount,
                        onValueChange = { expAmount = it },
                        label = { Text("ব্যয়ের পরিমাণ (টাকা)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { 
                                focusManager.clearFocus()
                                showExpenseDialog = false 
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল")
                        }
                        Button(
                            onClick = {
                                val amt = expAmount.toDoubleOrNull() ?: 0.0
                                if (expTitle.isNotBlank() && amt > 0.0) {
                                    focusManager.clearFocus()
                                    viewModel.recordExpense(expTitle, amt)
                                    showExpenseDialog = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("সংরক্ষণ করুন")
                        }
                    }
                }
            }
        }
    }

    // 3. Quick Collect Baki Payment
    if (showPaymentDialog) {
        var selectedCustId by remember { mutableStateOf<Int?>(null) }
        var payAmount by remember { mutableStateOf("") }
        var payNote by remember { mutableStateOf("") }
        var expandedDropdown by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { 
            focusManager.clearFocus()
            showPaymentDialog = false 
        }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "বাকি টাকা পরিশোধ গ্রহণ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Customer Selector Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        val selectedCustName = customers.find { it.id == selectedCustId }?.let { "${it.name} (বকেয়া: ${it.currentDue.toInt()} টাকা)" } ?: "বাকি ক্রেতা নির্বাচন করুন"
                        OutlinedCard(
                            onClick = { expandedDropdown = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedCustName, fontSize = 14.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            customers.filter { it.currentDue > 0.0 }.forEach { customer ->
                                DropdownMenuItem(
                                    text = { Text("${customer.name} — বকেয়া: ${customer.currentDue.toInt()} টাকা") },
                                    onClick = {
                                        selectedCustId = customer.id
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = payAmount,
                        onValueChange = { payAmount = it },
                        label = { Text("পরিশোধের পরিমাণ (টাকা)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = payNote,
                        onValueChange = { payNote = it },
                        label = { Text("মন্তব্য (ঐচ্ছিক)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { 
                                focusManager.clearFocus()
                                showPaymentDialog = false 
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল")
                        }
                        Button(
                            onClick = {
                                val amt = payAmount.toDoubleOrNull() ?: 0.0
                                val custId = selectedCustId
                                if (custId != null && amt > 0.0) {
                                    focusManager.clearFocus()
                                    viewModel.recordCustomerPayment(custId, amt, payNote)
                                    showPaymentDialog = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("জমা করুন")
                        }
                    }
                }
            }
        }
    }
}
