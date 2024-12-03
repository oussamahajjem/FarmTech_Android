package com.example.jetpackcomposeauthui.data.models

data class WeatherDto(
    val summary: SummaryDto,
    val forecasts: List<ForecastDto>
)

data class SummaryDto(
    val startDate: String,
    val severity: Int,
    val phrase: String,
    val category: String
)

data class ForecastDto(
    val date: String,
    val temperature: TemperatureRangeDto,
    val realFeelTemperature: TemperatureRangeDto,
    val realFeelTemperatureShade: TemperatureRangeDto,
    val hoursOfSun: Double,
    val degreeDaySummary: DegreeDaySummaryDto,
    val airAndPollen: List<AirAndPollenDto>,
    val day: PeriodDto,
    val night: PeriodDto,
    val sources: List<String>
)

data class TemperatureRangeDto(
    val minimum: TemperatureDto,
    val maximum: TemperatureDto
)

data class TemperatureDto(
    val value: Double,
    val unit: String,
    val unitType: Int
)

data class DegreeDaySummaryDto(
    val heating: TemperatureDto,
    val cooling: TemperatureDto
)

data class AirAndPollenDto(
    val name: String,
    val value: Int,
    val category: String,
    val categoryValue: Int,
    val type: String? = null
)

data class PeriodDto(
    val iconCode: Int,
    val iconPhrase: String,
    val hasPrecipitation: Boolean,
    val precipitationType: String? = null,
    val precipitationIntensity: String? = null,
    val shortPhrase: String,
    val longPhrase: String,
    val precipitationProbability: Int,
    val thunderstormProbability: Int,
    val rainProbability: Int,
    val snowProbability: Int,
    val iceProbability: Int,
    val wind: WindDto,
    val windGust: WindDto,
    val totalLiquid: PrecipitationDto,
    val rain: PrecipitationDto,
    val snow: PrecipitationDto,
    val ice: PrecipitationDto,
    val hoursOfPrecipitation: Double,
    val hoursOfRain: Double,
    val hoursOfSnow: Double,
    val hoursOfIce: Double,
    val cloudCover: Int
)

data class WindDto(
    val direction: DirectionDto,
    val speed: SpeedDto
)

data class DirectionDto(
    val degrees: Int,
    val localizedDescription: String
)

data class SpeedDto(
    val value: Double,
    val unit: String,
    val unitType: Int
)

data class PrecipitationDto(
    val value: Double,
    val unit: String,
    val unitType: Int
)
