package eu.baroncelli.dkmpsample.shared.datalayer.sources.localdb.countries

import app.cash.sqldelight.async.coroutines.awaitAsList
import eu.baroncelli.dkmpsample.shared.datalayer.objects.CountryListData
import mylocal.db.LocalDb

suspend fun LocalDb.getCountriesList(): List<CountryListData> {
    return countriesQueries.getCountriesList(mapper = ::CountryListData).awaitAsList()
}

suspend fun LocalDb.setCountriesList(list: List<CountryListData>) {
    countriesQueries.transaction {
        list.forEach {
            countriesQueries.upsertCountry(
                name = it.name,
                population = it.population,
                first_doses = it.firstDoses,
                fully_vaccinated = it.fullyVaccinated,
            )
        }
    }
}

suspend fun LocalDb.toggleFavoriteCountry(country: String) {
    countriesQueries.updateFavorite(country)
}

suspend fun LocalDb.getFavoriteCountriesMap(): Map<String, Boolean> {
    return countriesQueries.getFavorites().awaitAsList().associateBy({ it }, { true })
}