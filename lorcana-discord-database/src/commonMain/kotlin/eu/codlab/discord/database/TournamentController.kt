package eu.codlab.discord.database

import eu.codlab.discord.database.local.TournamentQueries
import eu.codlab.discord.database.local.TournamentUserQueries
import eu.codlab.discord.database.local.TournamentUserRoundQueries
import eu.codlab.discord.database.models.TournamentUser
import eu.codlab.discord.database.models.TournamentUserRound
import eu.codlab.discord.database.models.TrackedTournament
import eu.codlab.discord.database.utils.Queue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TournamentController
internal constructor(
    private val tournamentQueries: TournamentQueries,
    private val tournamentUserQueries: TournamentUserQueries,
    private val tournamentUserRoundQueries: TournamentUserRoundQueries
) {
    private val queue = Queue()

    fun selectTrackedTournaments() = tournamentQueries.select().executeAsList().map {
        TrackedTournament(
            id = it.id,
            discordChannel = it.discord_channel_id,
            discordGuild = it.discord_guild_id,
            tournament = it.tournament_id,
            closed = it.closed == 1L
        )
    }

    suspend fun selectForUser(
        discordGuild: Long,
        discordChannel: Long,
        discordUser: Long
    ) = post {
        tournamentUserQueries.selectForDiscordUser(
            discord_guild_id = discordGuild,
            discord_channel_id = discordChannel,
            discord_user_id = discordUser
        ).executeAsList()
            .map {
                TournamentUser(
                    tournament = TrackedTournament(
                        id = it.tracked_tournament_id,
                        discordGuild = it.discord_guild_id!!,
                        discordChannel = it.discord_channel_id!!,
                        tournament = it.tournament_id!!,
                        closed = it.closed == 1L
                    ),
                    meleeUsername = it.melee_username,
                    discordUser = it.discord_user_id,
                    id = it.id
                )
            }
    }

    suspend fun selectForTournament(tournament: TrackedTournament) = post {
        tournamentUserQueries.selectForTournament(tournament.id)
            .executeAsList()
            .map {
                TournamentUser(
                    tournament = tournament,
                    meleeUsername = it.melee_username,
                    discordUser = it.discord_user_id,
                    id = it.id
                )
            }
    }

    suspend fun selectRounds(tournament: TrackedTournament, user: TournamentUser) = post {
        tournamentUserRoundQueries.selectRounds(tournament.id, user.id)
            .executeAsList()
            .map {
                TournamentUserRound(
                    id = it.id,
                    roundIndex = it.round_index,
                    user = user,
                    tournament = tournament,
                    againstColor1 = it.against_color_1,
                    againstColor2 = it.against_color_2
                )
            }
    }

    suspend fun insert(
        discordGuild: Long,
        discordChannel: Long,
        tournament: String
    ) = post {
        println("attempt to add $discordGuild/$discordChannel/$tournament")

        val found = tournamentQueries.select()
            .executeAsList().find {
                it.tournament_id == tournament &&
                        it.discord_guild_id == discordGuild &&
                        it.discord_channel_id == discordChannel
            }

        if (found == null) {
            tournamentQueries.insert(
                tournament_id = tournament,
                discord_guild_id = discordGuild,
                discord_channel_id = discordChannel
            )
        }

        tournamentQueries.select()
            .executeAsList().filter {
                it.tournament_id == tournament &&
                        it.discord_guild_id == discordGuild &&
                        it.discord_channel_id == discordChannel
            }.map {
                TrackedTournament(
                    id = it.id,
                    tournament = it.tournament_id,
                    discordChannel = it.discord_channel_id,
                    discordGuild = it.discord_guild_id,
                    closed = it.closed == 1L
                )
            }.first()
    }

    suspend fun insert(
        trackedTournament: TrackedTournament,
        discordUser: Long,
        meleeUserName: String
    ) = post {
        println("attempt to add $discordUser/$meleeUserName/$trackedTournament")

        val found = tournamentUserQueries.selectForDiscordUser(
            discord_user_id = discordUser,
            discord_channel_id = trackedTournament.discordChannel,
            discord_guild_id = trackedTournament.discordGuild
        ).executeAsList().firstOrNull()

        if (found == null) {
            tournamentUserQueries.insert(
                discord_user_id = discordUser,
                tracked_tournament_id = trackedTournament.id,
                melee_username = meleeUserName
            )
        }

        tournamentUserQueries.selectForDiscordUser(
            discord_user_id = discordUser,
            discord_channel_id = trackedTournament.discordChannel,
            discord_guild_id = trackedTournament.discordGuild
        ).executeAsList().map {
            TournamentUser(
                tournament = trackedTournament,
                meleeUsername = it.melee_username,
                discordUser = it.discord_user_id,
                id = it.id
            )
        }.first()
    }

    suspend fun insert(
        trackedTournament: TrackedTournament,
        tournamentUser: TournamentUser,
        roundId: String,
        againstColor1: String,
        againstColor2: String
    ): TournamentUserRound {
        return post {
            val found = tournamentUserRoundQueries.selectRounds(
                tournament_id = trackedTournament.id,
                user_id = tournamentUser.id
            ).executeAsList().find { it.round_index == roundId }

            if (null == found) {
                tournamentUserRoundQueries.insert(
                    tournament_id = trackedTournament.id,
                    user_id = tournamentUser.id,
                    round_index = roundId,
                    against_color_1 = againstColor1,
                    against_color_2 = againstColor2
                )
            } else {
                tournamentUserRoundQueries.update(
                    against_color_1 = againstColor1,
                    against_color_2 = againstColor2,
                    tournament_id = trackedTournament.id,
                    user_id = tournamentUser.id,
                    round_index = roundId
                )
            }

            return@post tournamentUserRoundQueries.selectRounds(
                tournament_id = trackedTournament.id,
                user_id = tournamentUser.id
            ).executeAsList().map {
                TournamentUserRound(
                    id = it.id,
                    tournament = trackedTournament,
                    user = tournamentUser,
                    againstColor1 = it.against_color_1,
                    againstColor2 = it.against_color_2,
                    roundIndex = it.round_index
                )
            }.find { it.roundIndex == roundId }!!
        }
    }

    suspend fun untrackAll(discordGuild: Long, discordChannel: Long) {
        post {
            tournamentQueries.untrack(
                discord_guild_id = discordGuild,
                discord_channel_id = discordChannel
            )
        }
    }

    suspend fun unclose(trackedTournament: TrackedTournament) {
        post {
            tournamentQueries.unclose(trackedTournament.id)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun <T> post(block: () -> T): T =
        suspendCoroutine { continuation ->
            queue.post {
                try {
                    val result = block()
                    continuation.resume(result)
                } catch (exception: Throwable) {
                    continuation.resumeWithException(exception)
                }
            }
        }
}
