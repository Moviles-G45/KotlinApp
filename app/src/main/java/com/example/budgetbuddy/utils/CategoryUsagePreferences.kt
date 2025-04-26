object CategoryUsagePreferences {
    private const val PREF_NAME = "category_usage_pref"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun incrementCategoryUsage(context: Context, userToken: String, categoryId: Int) {
        val key = "$userToken-category-$categoryId"
        val prefs = getPrefs(context)
        val current = prefs.getInt(key, 0)
        prefs.edit().putInt(key, current + 1).apply()
    }

    fun getCategoryUsageMap(context: Context, userToken: String): Map<Int, Int> {
        val prefs = getPrefs(context)
        return prefs.all
            .filterKeys { it.startsWith("$userToken-category-") }
            .mapKeys { it.key.removePrefix("$userToken-category-").toInt() }
            .mapValues { it.value as Int }
    }
}
