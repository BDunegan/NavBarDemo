package com.example.navbardemo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //Login Logic should be first then call MainAppNav() for Brandon's Part
            MainAppNav()
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainAppNav() {
    val navController = rememberNavController()
    var isExpanded by remember { mutableStateOf(false) }
    // This 'name' mutable variable should come from the login OAuth for the user
    var name by remember { mutableStateOf("Anonymous") }

    Scaffold(
        //User Greeting
        topBar = { FixedTopBar(name) },
        //Bottom Navigation Bar (Main, New, Settings)
        bottomBar = { ExpandableBottomNavigationBar(navController, isExpanded, onExpandToggle = { isExpanded = !isExpanded }) }
    ) {
        NavigationGraph(navController)
    }
}

@Composable
fun ExpandableBottomNavigationBar(
    navController: NavHostController,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Surface(elevation = 8.dp) {
        Column {
            // Expand/Collapse Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = onExpandToggle) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = "Expand/Collapse"
                    )
                }
            }

            // Navigation Row Visibility
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(50)) + fadeIn(),
                exit = shrinkVertically(animationSpec = tween(50)) + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Navigation Tabs
                    BottomNavigationTab("Main", navController, "main")
                    BottomNavigationTab("New", navController, "new")
                    BottomNavigationTab("Settings", navController, "settings")
                }
            }
        }
    }
}

@Composable
fun FixedTopBar(name: String) {
    TopAppBar(
        title = { Text("Hello, $name") },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 8.dp
    )
}

@Composable
fun BottomNavigationTab(label: String, navController: NavHostController, route: String) {
    Text(
        text = label,
        modifier = Modifier
            .padding(16.dp)
            .clickable { navController.navigate(route) }
    )
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "new") {
        composable("main") { MainScreen() }
        composable("new") { NewScreen() }
        composable("settings") { SettingsScreen() }
    }
}
//Main Screen ---------------------------------------------------------------------------------
@Composable
fun MainScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Total Budget Breakdown",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        item {
            BudgetTotalPieChart()
        }
        item {
            Text(
                text = "Detailed Budget Breakdown",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        item {
            BudgetBreakdownDonutChart()
        }
        // Add more charts or components here
        items(10) { index -> // Example: Adding multiple charts dynamically
            ExampleChart(title = "Chart $index")
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ExampleChart(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // Replace with your chart component
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Gray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Chart Content")
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BudgetBreakdownDonutChart() {
    val totalBudget = 5000f // Total budget in dollars

    // Donut Chart Data
    val donutChartData = PieChartData(
        slices = listOf(
            PieChartData.Slice("Housing", 1750f, Color(0xFF5F0A87)),
            PieChartData.Slice("Food", 1250f, Color(0xFF20BF55)),
            PieChartData.Slice("Transportation", 750f, Color(0xFFEC9F05)),
            PieChartData.Slice("Entertainment", 500f, Color(0xFFF53844)),
            PieChartData.Slice("Savings", 750f, Color(0xFF0496FF))
        ),
        plotType = PlotType.Donut
    )

    // Donut Chart Configuration
    val donutChartConfig = PieChartConfig(
        labelVisible = true,
        strokeWidth = 120f,
        labelColor = Color.White,
        labelFontSize = 14.sp,
        activeSliceAlpha = 0.9f,
        isAnimationEnable = true,
        chartPadding = 25,
        labelType = PieChartConfig.LabelType.VALUE,
    )

    // State to hold the currently selected slice
    var selectedSlice by remember { mutableStateOf<PieChartData.Slice?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        // Donut Pie Chart
        DonutPieChart(
            modifier = Modifier.fillMaxSize(),
            pieChartData = donutChartData,
            pieChartConfig = donutChartConfig,
            onSliceClick = { slice ->
                selectedSlice = slice
            }
        )

        // Centered Total Budget and Selected Slice Details
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Total Budget Display (Always Fixed)
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "$${totalBudget.toInt()}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Selected Slice Details
            selectedSlice?.let { slice ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = slice.label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Text(
                    text = "$${slice.value.toInt()}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = slice.color
                )
            }
        }
    }
}

@Composable
fun BudgetTotalPieChart() {
    val totalBudget = 5000f // Total budget in dollars
    val currentBudget = 3000f // Current budget in dollars

    // Donut Chart Data
    val totalPieChartData = PieChartData(
        slices = listOf(
            PieChartData.Slice("Total", totalBudget, Color(0xFF5F0A87)),
            PieChartData.Slice("Current", currentBudget, Color(0xFF03A9F4)),
        ),
        plotType = PlotType.Pie
    )

    // Donut Chart Configuration
    val totalPieChartConfig = PieChartConfig(
        labelVisible = true,
        strokeWidth = 120f,
        labelColor = Color.White,
        labelFontSize = 14.sp,
        activeSliceAlpha = 0.9f,
        isAnimationEnable = true,
        chartPadding = 25,
        labelType = PieChartConfig.LabelType.VALUE,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        // Total Pie Chart
        PieChart(
            modifier = Modifier.fillMaxSize(),
            pieChartData = totalPieChartData,
            pieChartConfig = totalPieChartConfig,
        )
    }
}

//New Screen ---------------------------------------------------------------------------------
@Composable
fun NewScreen() {
    //Here we should add the logic to add a new receipt to the database (picture or manual)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("This is where we are gonna add receipts")
    }
}

//Settings Screen ---------------------------------------------------------------------------------
@Composable
fun SettingsScreen() {
    // Generate 100 fake settings
    val settingsList = List(100) { index ->
        "Setting ${index + 1}" to "Description for Setting ${index + 1}"
    }

    // LazyColumn to display settings
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(settingsList) { setting ->
            SettingItem(title = setting.first, description = setting.second)
        }
    }
}

@Composable
fun SettingItem(title: String, description: String) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.body2)
        }
    }
}
