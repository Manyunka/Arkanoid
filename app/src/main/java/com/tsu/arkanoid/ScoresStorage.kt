package com.tsu.arkanoid

import android.content.Context

class ScoresStorage(val context: Context) {
    fun saveScores(level: Int, scores: Int) {
        val sharedPreferences =
            context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        val strKey = when (level) {
            1 -> SCORES_KEY_1
            2 -> SCORES_KEY_2
            3 -> SCORES_KEY_3
            else -> SCORES_KEY_1
        }
        editor.putInt(strKey, scores)

        editor.apply()
    }

    fun saveRank(level: Int, rank: Int) {
        val sharedPreferences =
            context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        val rankKey = when (level) {
            1 -> RANK_KEY_1
            2 -> RANK_KEY_2
            3 -> RANK_KEY_3
            else -> RANK_KEY_1
        }
        editor.putInt(rankKey, rank)

        editor.apply()
    }

    fun readScores(level: Int): Int {
        val sharedPreferences =
            context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

        val strKey = when (level) {
            1 -> SCORES_KEY_1
            2 -> SCORES_KEY_2
            3 -> SCORES_KEY_3
            else -> SCORES_KEY_1
        }
        return sharedPreferences.getInt(strKey, 0)
    }

    fun readRank(level: Int): Int {
        val sharedPreferences =
            context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

        val rankKey = when (level) {
            1 -> RANK_KEY_1
            2 -> RANK_KEY_2
            3 -> RANK_KEY_3
            else -> RANK_KEY_1
        }
        return sharedPreferences.getInt(rankKey, 0)
    }

    companion object {
        const val PREFERENCES_FILE_NAME = "STORAGE"
        const val SCORES_KEY_1 = "score_1"
        const val SCORES_KEY_2 = "score_2"
        const val SCORES_KEY_3 = "score_3"
        const val RANK_KEY_1 = "rank_1"
        const val RANK_KEY_2 = "rank_2"
        const val RANK_KEY_3 = "rank_3"
    }

}