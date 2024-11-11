package com.mog_rn.client.utils

sealed class RouteConfig(val route: String) {
    data object LoginScreen: RouteConfig("login")
    data object HomeScreen: RouteConfig("home")
    data object DeliveryScheduleListScreen: RouteConfig("delivery_schedule_list")
    data object DeliveryDetailScreen: RouteConfig("delivery_detail")
    data object DeliveryScheduleScreen: RouteConfig("delivery_schedule")
}