// use an integer for version numbers
version = 21

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

cloudstream {
    language = "en"
    description = "HDRezka source with movies, series, anime, cartoons, shows, and Korean dramas."
    authors = listOf("freelanceagent1")
    status = 1
    tvTypes = listOf("Movie", "TvSeries", "Anime", "Cartoon", "AsianDrama")
    iconUrl = "https://www.google.com/s2/favicons?domain=hdrezka.ag&sz=%size%"
}
