package com.example.fonksiyonel.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fonksiyonel.model.UserType
import com.example.fonksiyonel.ui.screens.appointment.AppointmentScreen
import com.example.fonksiyonel.ui.screens.auth.LoginScreen
import com.example.fonksiyonel.ui.screens.auth.RegisterScreen
import com.example.fonksiyonel.ui.screens.badges.BadgesScreen
import com.example.fonksiyonel.ui.screens.doctor.DoctorHomeScreen
import com.example.fonksiyonel.ui.screens.home.HomeScreen
import com.example.fonksiyonel.ui.screens.reports.ReportDetailScreen
import com.example.fonksiyonel.ui.screens.reports.ReportHistoryScreen
import com.example.fonksiyonel.ui.screens.scan.ScanScreen
import com.example.fonksiyonel.ui.screens.share.ShareWithDoctorScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object DoctorHome : Screen("doctor_home")
    object ReportHistory : Screen("report_history")
    object ReportDetail : Screen("report_detail/{reportId}") {
        fun createRoute(reportId: String) = "report_detail/$reportId"
    }
    object Scan : Screen("scan")
    object ShareWithDoctor : Screen("share_with_doctor/{reportId}") {
        fun createRoute(reportId: String) = "share_with_doctor/$reportId"
    }
    object Appointment : Screen("appointment")
    object Badges : Screen("badges")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = { userType ->
                    if (userType == UserType.DOCTOR) {
                        navController.navigate(Screen.DoctorHome.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { userType ->
                    if (userType == UserType.DOCTOR) {
                        navController.navigate(Screen.DoctorHome.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToReportHistory = {
                    navController.navigate(Screen.ReportHistory.route)
                },
                onNavigateToScan = {
                    navController.navigate(Screen.Scan.route)
                },
                onNavigateToAppointment = {
                    navController.navigate(Screen.Appointment.route)
                },
                onNavigateToBadges = {
                    navController.navigate(Screen.Badges.route)
                },
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(Screen.ReportDetail.createRoute(reportId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.DoctorHome.route) {
            DoctorHomeScreen(
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(Screen.ReportDetail.createRoute(reportId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.DoctorHome.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.ReportHistory.route) {
            ReportHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(Screen.ReportDetail.createRoute(reportId))
                }
            )
        }
        
        composable(Screen.ReportDetail.route) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            ReportDetailScreen(
                reportId = reportId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToShareWithDoctor = { id ->
                    navController.navigate(Screen.ShareWithDoctor.createRoute(id))
                }
            )
        }
        
        composable(Screen.Scan.route) {
            ScanScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onScanComplete = { reportId ->
                    navController.navigate(Screen.ReportDetail.createRoute(reportId)) {
                        popUpTo(Screen.Scan.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.ShareWithDoctor.route) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            ShareWithDoctorScreen(
                reportId = reportId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onShareComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Appointment.route) {
            AppointmentScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAppointmentBooked = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Badges.route) {
            BadgesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
