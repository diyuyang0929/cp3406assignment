package com.example.assignment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.ui.theme.AssignmentTheme
import androidx.compose.ui.Alignment
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import kotlin.math.roundToInt
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.AccountCircle


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssignmentTheme {
                FinanceHomeScreen()
            }
        }
    }
}


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceHomeScreen() {
    var expenses by remember { mutableStateOf(listOf<Pair<String, Float>>()) }
    var income by remember { mutableFloatStateOf(0f) }
    var budget by remember { mutableFloatStateOf(0f) }
    var savingsGoal by remember { mutableFloatStateOf(0f) }
    var currentSavings by remember { mutableFloatStateOf(0f) }
    var goalName by remember { mutableStateOf("") }
    var billReminders by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showAddIncomeDialog by remember { mutableStateOf(false) }
    var showSetSavingsGoalDialog by remember { mutableStateOf(false) }
    var showAddBillDialog by remember { mutableStateOf(false) }
    var suggestionText by remember { mutableStateOf("") }
    val colors = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Cyan, Color.Magenta, Color.Gray, Color.DarkGray, Color.LightGray
    )
    val clearData = {
        expenses = listOf()
        income = 0f
        budget = 0f
        savingsGoal = 0f
        currentSavings = 0f
        billReminders = listOf()
    }

    val generateSuggestion = {
        val totalSpent = expenses.sumOf { it.second.toDouble() }
        val remainingBalance = budget - totalSpent
        suggestionText = when {
            remainingBalance < 0 -> "You have exceeded your budget by ${String.format("%.2f", -remainingBalance)}. Try reducing expenses."
            remainingBalance > 0 -> "You have ${String.format("%.2f", remainingBalance)} left. Consider saving more!"
            else -> "You're on track with your budget!"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Personal Finance Tracker", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFC0CB))
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                        Icon(imageVector = Icons.Rounded.Home, contentDescription = "Home Screen", tint = Color.Black)
                        Icon(imageVector = Icons.Rounded.ShoppingCart, contentDescription = "Cart Screen", tint = Color.Black)
                        Icon(imageVector = Icons.Rounded.AccountCircle, contentDescription = "Account Screen", tint = Color.Black)
                },
                containerColor = Color(0xFFFFC0CB)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item { BudgetOverviewCard(budget, income, expenses) { remainingBalance -> currentSavings += remainingBalance } }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { ExpensePieChart(expenses) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { ExpenseList(expenses, colors) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                QuickActions(
                    onAddExpense = { showAddExpenseDialog = true },
                    onAddIncome = { showAddIncomeDialog = true },
                    onClearData = clearData
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { SavingsGoalCard(goalName, savingsGoal, currentSavings) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Button(
                    onClick = { showSetSavingsGoalDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB))
                ) {
                    Text("Set Savings Goal", color = Color.White)
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Button(
                    onClick = generateSuggestion,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB))
                ) {
                    Text("Suggestions", color = Color.White)
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    text = suggestionText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Button(
                    onClick = { showAddBillDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB))
                ) {
                    Text("Add Bill Reminder", color = Color.White)
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { BillReminderList(billReminders) }
        }
    }

    if (showSetSavingsGoalDialog) {
        SetSavingsGoalDialog(
            onDismiss = { showSetSavingsGoalDialog = false },
            onSubmit = { goalNameInput, goal ->
                goalName = goalNameInput
                savingsGoal = goal
                showSetSavingsGoalDialog = false
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onSubmit = { expenseName, amount ->
                expenses = expenses + Pair(expenseName, amount)
                showAddExpenseDialog = false
            }
        )
    }

    if (showAddIncomeDialog) {
        AddIncomeDialog(
            onDismiss = { showAddIncomeDialog = false },
            onSubmit = { amount ->
                income += amount
                budget = (income * 0.60f).roundToInt().toFloat()
                showAddIncomeDialog = false
            }
        )
    }

    if (showAddBillDialog) {
        AddBillReminderDialog(
            onDismiss = { showAddBillDialog = false },
            onSubmit = { billName, dueDate ->
                billReminders = billReminders + Pair(billName, dueDate)
                showAddBillDialog = false
            }
        )
    }
}

@Composable
fun QuickActions(
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onClearData: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAddExpense,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB))
        ) {
            Text("Add Exp", color = Color.White)
        }
        Button(
            onClick = onAddIncome,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB))
        ) {
            Text("Add Inc", color = Color.White)
        }
        Button(
            onClick = onClearData,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB))
        ) {
            Text("Clear", color = Color.White)
        }
    }
}

@Composable
fun AddBillReminderDialog(onDismiss: () -> Unit, onSubmit: (String, String) -> Unit) {
    var billName by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bill Reminder", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(
                    value = billName,
                    onValueChange = { billName = it },
                    label = { Text("Bill Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                TextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (billName.isNotEmpty() && dueDate.isNotEmpty()) {
                        onSubmit(billName, dueDate)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5))
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

@Composable
fun BillReminderList(billReminders: List<Pair<String, String>>) {
    if (billReminders.isEmpty()) {
        Text("No bill reminders set", modifier = Modifier.padding(16.dp))
    } else {
        Column(modifier = Modifier.fillMaxWidth()) {
            billReminders.forEach { (billName, dueDate) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Bill: $billName", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Due Date: $dueDate", fontSize = 16.sp, color = Color.Red)
                    }
                }
            }
        }
    }
}


@Composable
fun BudgetOverviewCard(budget: Float, income: Float, expenses: List<Pair<String, Float>>, onSaveRemainingBalance: (Float) -> Unit) {
    val totalSpent = expenses.sumOf { it.second.toDouble() }.toFloat()
    val remainingBalance = budget - totalSpent

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Monthly Income: \$$income", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Monthly Budget: \$$budget", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Spent: \$$totalSpent", fontSize = 16.sp, color = Color.Red)
            Text(
                "Remaining: \$$remainingBalance",
                fontSize = 16.sp,
                color = if (remainingBalance >= 0) Color.Green else Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onSaveRemainingBalance(remainingBalance) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB))
            ) {
                Text("Add Remaining to Savings", color = Color.White)
            }
        }
    }
}


@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onSubmit: (String, Float) -> Unit) {
    var expenseName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(
                    value = expenseName,
                    onValueChange = { expenseName = it },
                    label = { Text("Expense Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountFloat = amount.toFloatOrNull() ?: 0f
                    if (expenseName.isNotEmpty() && amountFloat > 0) {
                        onSubmit(expenseName, amountFloat)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5))
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

@Composable
fun ExpensePieChart(expenses: List<Pair<String, Float>>) {
    val total = expenses.sumOf { it.second.toDouble() }.toFloat()
    if (total == 0f) {
        Text("No expenses to display", modifier = Modifier.padding(16.dp))
        return
    }

    val colors = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Cyan, Color.Magenta, Color.Gray, Color.DarkGray, Color.LightGray
    )

    val angles = expenses.map { it.second / total * 360f }
    var startAngle = 0f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
    ) {
        var colorIndex = 0
        for (angle in angles) {
            drawArc(
                color = colors[colorIndex % colors.size],
                startAngle = startAngle,
                sweepAngle = angle,
                useCenter = true
            )
            startAngle += angle
            colorIndex++
        }
    }
}

@Composable
fun AddIncomeDialog(onDismiss: () -> Unit, onSubmit: (Float) -> Unit) {
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Income", fontWeight = FontWeight.Bold) },
        text = {
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountFloat = amount.toFloatOrNull() ?: 0f
                    if (amountFloat > 0) {
                        onSubmit(amountFloat)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5))) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

@Composable
fun SavingsGoalCard(goalName: String, savingsGoal: Float, currentSavings: Float) {
    val progress = if (savingsGoal > 0) (currentSavings / savingsGoal) * 100 else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Savings Goal Name: $goalName", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Savings Goal: \$$savingsGoal", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Current Savings: \$$currentSavings", fontSize = 16.sp)
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 8.dp),
                color = Color.Green,
            )
            Text(
                "${progress.roundToInt()}% Achieved",
                fontSize = 14.sp,
                color = if (progress >= 100f) Color.Green else Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun SetSavingsGoalDialog(onDismiss: () -> Unit, onSubmit: (String, Float) -> Unit) {
    var savingsGoal by remember { mutableStateOf("") }
    var goalName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Savings Goal", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(
                    value = goalName,
                    onValueChange = { goalName = it },
                    label = { Text("Goal Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = savingsGoal,
                    onValueChange = { savingsGoal = it },
                    label = { Text("Goal Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goalFloat = savingsGoal.toFloatOrNull() ?: 0f
                    if (goalFloat > 0 && goalName.isNotBlank()) {
                        onSubmit(goalName, goalFloat)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text("Set Goal", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5))) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

@Composable
fun ExpenseList(expenses: List<Pair<String, Float>>, colors: List<Color>) {
    if (expenses.isEmpty()) {
        Text("No expenses added yet", modifier = Modifier.padding(16.dp))
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Expenses:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            expenses.forEachIndexed { index, (name, amount) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(colors[index % colors.size], shape = RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("- $name", modifier = Modifier.weight(1f))
                    Text(
                        "$${String.format("%.2f", amount)}",
                        modifier = Modifier.weight(0.3f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


