package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    var shopName by remember { mutableStateOf("হাসান ভাইয়ের মুদির দোকান") }
    var shopPhone by remember { mutableStateOf("০১৭১১-২২৩৩৪৪") }
    var shopAddress by remember { mutableStateOf("মিরপুর-১০, ঢাকা") }

    var showAnalysisDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }

    // Firebase Credentials inputs
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    // Pin Inputs
    var pinInput by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isPinActive by remember { mutableStateOf(SecurityManager.isPinEnabled(context)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Shop Profile Edit Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "দোকানের তথ্য পরিবর্তন করুন",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("দোকানের নাম") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = shopPhone,
                    onValueChange = { shopPhone = it },
                    label = { Text("মোবাইল নম্বর") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = shopAddress,
                    onValueChange = { shopAddress = it },
                    label = { Text("ঠিকানা") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                )

                Button(
                    onClick = {
                        Toast.makeText(context, "দোকানের প্রোফাইল তথ্য সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("তথ্য সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Smart Analysis card launcher
        Card(
            onClick = { showAnalysisDialog = true },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth().testTag("launch_smart_analysis")
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "স্মার্ট ব্যবসায়িক বিশ্লেষণ ও স্টক অনুমান",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "কোন পণ্য বেশি বিক্রি হয় এবং কখন স্টক লাগবে দেখুন।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Category Management button
        Card(
            onClick = { viewModel.navigateTo(Screen.CategoryManagement) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().testTag("launch_category_management")
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ক্যাটাগরি ও একক ব্যবস্থাপনা",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "নতুন ক্যাটাগরি তৈরি করুন ও পণ্য একক পরিচালনা করুন।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Security & Simulating Data backups
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "নিরাপত্তা ও ডেটা ব্যাকআপ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showBackupDialog = true
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("ক্লাউড ব্যাকআপ ও অনলাইন সিঙ্ক", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        val backupSubtext = if (viewModel.isUserLoggedIn) {
                            "লগড ইন আছেন: ${viewModel.currentUserEmail}"
                        } else {
                            "আপনার সম্পূর্ণ হিসাব ক্লাউডে নিরাপদ রাখতে ব্যাকআপ চালু করুন।"
                        }
                        Text(backupSubtext, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Divider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showPinDialog = true
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("সিকিউরিটি পিন লক (PIN Lock)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        val pinSubtext = if (isPinActive) {
                            "পিন লক সক্রিয় আছে (আপনার অ্যাপ সুরক্ষিত)।"
                        } else {
                            "অ্যাপটি সুরক্ষিত করতে ৪ সংখ্যার পাসকোড সক্রিয় করুন।"
                        }
                        Text(pinSubtext, fontSize = 11.sp, color = if (isPinActive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Notification Settings Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "নোটিফিকেশন ও অ্যালার্ট সেটিংস",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 1. Low Stock Alerts Switch Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.TrendingDown, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("কম স্টক সতর্কতা", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("পণ্যের স্টক ফুরিয়ে বা কমে গেলে নোটিফিকেশন পান।", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    var lowStockEnabled by remember { mutableStateOf(NotificationPrefsManager.isLowStockEnabled(context)) }
                    Switch(
                        checked = lowStockEnabled,
                        onCheckedChange = {
                            lowStockEnabled = it
                            NotificationPrefsManager.setLowStockEnabled(context, it)
                        },
                        modifier = Modifier.testTag("toggle_low_stock_notif")
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // 2. Customer Due Alerts Switch Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("বাকির রিমাইন্ডার ও আদায় সতর্কতা", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("কাস্টমারের বকেয়া বৃদ্ধি ও পরিশোধের নোটিফিকেশন পান।", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    var dueEnabled by remember { mutableStateOf(NotificationPrefsManager.isCustomerDueEnabled(context)) }
                    Switch(
                        checked = dueEnabled,
                        onCheckedChange = {
                            dueEnabled = it
                            NotificationPrefsManager.setCustomerDueEnabled(context, it)
                        },
                        modifier = Modifier.testTag("toggle_due_notif")
                    )
                }
            }
        }
    }

    // 1. Cloud Sync & Backup Dialog
    if (showBackupDialog) {
        Dialog(onDismissRequest = { showBackupDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "ক্লাউড সিঙ্ক ও ব্যাকআপ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!viewModel.isFirebaseConnected) {
                        // Firebase not configured warning
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "⚠️ ক্লাউড কানেকশন নিষ্ক্রিয়",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "আপনার প্রজেক্টের google-services.json ফাইলটি যুক্ত না থাকায় ক্লাউড ব্যাকআপ নিষ্ক্রিয় আছে। আপনি আপনার নিজের ফায়ারবেস ব্যাকআপ ব্যবহার করতে পারেন।",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }

                    if (viewModel.isUserLoggedIn) {
                        // User is logged in
                        Text(
                            text = "লগড ইন ইমেইল: ${viewModel.currentUserEmail}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.handleBackup { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("backup_to_cloud_button"),
                            enabled = viewModel.isFirebaseConnected
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ক্লাউডে হিসাব ব্যাকআপ রাখুন")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.handleRestore { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    if (success) {
                                        showBackupDialog = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("restore_from_cloud_button"),
                            enabled = viewModel.isFirebaseConnected
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ক্লাউড থেকে হিসাব ফিরিয়ে আনুন")
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = {
                                viewModel.handleSignOut()
                                Toast.makeText(context, "সফলভাবে লগআউট করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                        ) {
                            Text("লগআউট করুন")
                        }
                    } else {
                        // User is not logged in, show Auth Forms
                        Text(
                            text = "নিরাপদ অনলাইন ব্যাকআপ ও নতুন ফোনে হিসাব পুনরুদ্ধার করতে আপনার অ্যাকাউন্ট দিয়ে প্রবেশ করুন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("ইমেইল এড্রেস") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("পাসওয়ার্ড (কমপক্ষে ৬ ডিজিট)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (emailInput.isBlank() || passwordInput.isBlank()) {
                                        Toast.makeText(context, "অনুগ্রহ করে সব তথ্য দিন!", Toast.LENGTH_SHORT).show()
                                        return@OutlinedButton
                                    }
                                    viewModel.handleSignUp(emailInput, passwordInput) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = viewModel.isFirebaseConnected
                            ) {
                                Text("নিবন্ধন")
                            }

                            Button(
                                onClick = {
                                    if (emailInput.isBlank() || passwordInput.isBlank()) {
                                        Toast.makeText(context, "অনুগ্রহ করে সব তথ্য দিন!", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    viewModel.handleSignIn(emailInput, passwordInput) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = viewModel.isFirebaseConnected
                            ) {
                                Text("লগইন")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    TextButton(
                        onClick = { showBackupDialog = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("বন্ধ করুন")
                    }
                }
            }
        }
    }

    // 2. Local Security PIN Dialog
    if (showPinDialog) {
        Dialog(onDismissRequest = { showPinDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "সিকিউরিটি পিন লক সেটিংস",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    if (isPinActive) {
                        Text(
                            text = "আপনার অ্যাপটি বর্তমানে ৪ সংখ্যার পিন লক দ্বারা সুরক্ষিত আছে।",
                            fontSize = 13.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = {
                                SecurityManager.disablePin(context)
                                isPinActive = false
                                Toast.makeText(context, "পিন লক নিষ্ক্রিয় করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                showPinDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("পিন লক সম্পূর্ণ বন্ধ করুন")
                        }
                    } else {
                        Text(
                            text = "আপনার অ্যাপটি সুরক্ষিত রাখতে একটি ৪ সংখ্যার পিন কোড সেট করুন। পিন সেট করার পর অ্যাপ ওপেন করতে এই পিন প্রয়োজন হবে।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = { input ->
                                if (input.length <= 4 && input.all { it.isDigit() }) {
                                    pinInput = input
                                }
                            },
                            label = { Text("৪ সংখ্যার নতুন পিন") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = {
                                if (pinInput.length != 4) {
                                    Toast.makeText(context, "অনুগ্রহ করে সঠিক ৪ সংখ্যার পিন দিন!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                SecurityManager.savePin(context, pinInput)
                                isPinActive = true
                                pinInput = ""
                                Toast.makeText(context, "অভিনন্দন! আপনার পিন সফলভাবে সেট করা হয়েছে।", Toast.LENGTH_LONG).show()
                                showPinDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("পিন লক চালু করুন")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = { showPinDialog = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("বাতিল")
                    }
                }
            }
        }
    }

    // Smart Analysis Report Sheet/Dialog
    if (showAnalysisDialog) {
        val analysis by viewModel.businessAnalysis.collectAsState()

        Dialog(onDismissRequest = { showAnalysisDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "স্মার্ট ব্যবসায়িক বিশ্লেষণ রিপোর্ট",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Best and Low sales
                    Text("পণ্য বিক্রয় প্রবণতা (গত ৭ দিন):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("সবচেয়ে বেশি বিক্রি হওয়া পণ্য:", fontSize = 11.sp, color = Color(0xFF2E7D32))
                                Text(
                                    text = if (analysis.bestSellingQty > 0.0) "${analysis.bestSellingProduct} — ${formatBengaliStock(analysis.bestSellingQty, analysis.bestSellingUnit)}" else "কোনো ডাটা নেই",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color(0xFFC62828))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("সবচেয়ে কম বিক্রি হওয়া পণ্য:", fontSize = 11.sp, color = Color(0xFFC62828))
                                Text(
                                    text = if (analysis.lowSellingQty > 0.0) "${analysis.lowSellingProduct} — ${formatBengaliStock(analysis.lowSellingQty, analysis.lowSellingUnit)}" else "কোনো ডাটা নেই",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Demand Predictions and Advice
                    Text("স্টক সতর্কতা ও চাহিদা অনুমান:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    if (analysis.predictions.isEmpty()) {
                        Text(
                            text = "বর্তমানে স্টক ফুরিয়ে যাওয়ার মতো কোনো পণ্য নেই। সব পণ্য পর্যাপ্ত মজুদ রয়েছে!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        analysis.predictions.forEach { pred ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${pred.productName} ফুরিয়ে যাবে!",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = pred.advice,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Business tips card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("💡 পরামর্শ:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "বেশি বিক্রি হওয়া পণ্যগুলোর স্টক সবসময় সচল রাখুন। স্টক শেষ হতে ৩ দিন বাকি থাকতেই পুনরায় নতুন স্টক সংগ্রহ করা লাভজনক ব্যবসার জন্য অত্যন্ত সহায়ক।",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showAnalysisDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("বন্ধ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf("") }
    var categoryUnit by remember { mutableStateOf("পিস") }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_category_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "নতুন ক্যাটাগরি")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "দোকানের ক্যাটাগরি তালিকা:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = cat.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "বিক্রয়ের একক: ${cat.unit}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            // Don't delete built-in seeded categories to keep app state safe, but allow deleting custom added ones
                            if (cat.id > 11) {
                                IconButton(
                                    onClick = {
                                        categoryToDelete = cat
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "মুছুন", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Category Dialog
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "নতুন ক্যাটাগরি তৈরি করুন",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("ক্যাটাগরির নাম (যেমন: মশলা, জুস)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("add_category_name_input")
                    )

                    // Select measurement Unit
                    Text("বিক্রয়ের একক নির্ধারণ করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        var unitDropdown by remember { mutableStateOf(false) }
                        OutlinedCard(onClick = { unitDropdown = true }, modifier = Modifier.fillMaxWidth().testTag("add_category_unit_card")) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(categoryUnit, fontSize = 13.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(expanded = unitDropdown, onDismissRequest = { unitDropdown = false }) {
                            val units = listOf("পিস", "কেজি", "লিটার", "গ্রাম", "প্যাকেট", "বোতল", "জোড়া", "ডজন")
                            units.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text(u) },
                                    onClick = {
                                        categoryUnit = u
                                        unitDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { showAddDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল")
                        }
                        Button(
                            onClick = {
                                if (categoryName.isNotBlank()) {
                                    viewModel.addCategory(categoryName, categoryUnit)
                                    categoryName = ""
                                    showAddDialog = false
                                    Toast.makeText(context, "নতুন ক্যাটাগরি সফলভাবে তৈরি হয়েছে!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_category_submit_button")
                        ) {
                            Text("তৈরি করুন")
                        }
                    }
                }
            }
        }
    }

    // Category Delete Confirmation Dialog
    if (categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("ক্যাটাগরি মুছে ফেলার সতর্কতা", fontWeight = FontWeight.Bold, color = Color.Red) },
            text = { Text("আপনি কি নিশ্চিতভাবে '${categoryToDelete?.name}' ক্যাটাগরি মুছে ফেলতে চান? এই ক্যাটাগরির সাথে যুক্ত সমস্ত তথ্য পরিবর্তন হতে পারে।") },
            confirmButton = {
                Button(
                    onClick = {
                        categoryToDelete?.let { cat ->
                            viewModel.deleteCategory(cat)
                            Toast.makeText(context, "ক্যাটাগরি মুছে ফেলা হয়েছে!", Toast.LENGTH_SHORT).show()
                        }
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("হ্যাঁ, মুছুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
