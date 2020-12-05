package com.iammoty.pego.dao

import com.iammoty.pego.model.*
import org.ehcache.*
import org.ehcache.config.*
import org.ehcache.config.persistence.*
import org.ehcache.config.units.*
import org.joda.time.*
import java.io.*

/**
 * An Ehcache based implementation for the [DAOFacade] that uses a [delegate] facade and a [storagePath]
 * and perform several caching strategies for each domain operation.
 */
class DAOFacadeCache(val delegate: DAOFacade, val storagePath: File) : DAOFacade {

    /**
     * Build a cache manager with a cache for tickets and other for users.
     * It uses the specified [storagePath] for persistence.
     * Limits the cache to 1000 entries, 10MB in memory, and 100MB in disk per both caches.
     */
    val cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .with(CacheManagerPersistenceConfiguration(storagePath))
        .withCache(
            "ticketsCache",
            CacheConfigurationBuilder.newCacheConfigurationBuilder<Int, Ticket>()
                .withResourcePools(
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(1000, EntryUnit.ENTRIES)
                        .offheap(10, MemoryUnit.MB)
                        .disk(100, MemoryUnit.MB, true)
                )
                .buildConfig(Int::class.javaObjectType, Ticket::class.java)
        )
        .withCache(
            "usersCache",
            CacheConfigurationBuilder.newCacheConfigurationBuilder<String, User>()
                .withResourcePools(
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(1000, EntryUnit.ENTRIES)
                        .offheap(10, MemoryUnit.MB)
                        .disk(100, MemoryUnit.MB, true)
                )
                .buildConfig(String::class.java, User::class.java)
        )
        .build(true)

    /**
     * Gets the cache for tickets represented by an [Int] key and a [Ticket] value.
     */
    val ticketsCache = cacheManager.getCache("ticketsCache", Int::class.javaObjectType, Ticket::class.java)

    /**
     * Gets the cache for users represented by a [String] key and a [User] value.
     */
    val usersCache = cacheManager.getCache("usersCache", String::class.java, User::class.java)

    override fun init() {
        delegate.init()
    }

    override fun createTicket(user: String, date: DateTime): Int {
        val id = delegate.createTicket(user, date)
        val ticket = Ticket(id, user, date)
        ticketsCache.put(id, ticket)
        return id
    }

    override fun deleteTicket(id: Int) {
        delegate.deleteTicket(id)
        ticketsCache.remove(id)
    }

    override fun getTicket(id: Int): Ticket {
        val cached = ticketsCache.get(id)
        if (cached != null) {
            return cached
        }

        val ticket = delegate.getTicket(id)
        ticketsCache.put(id, ticket)
        return ticket
    }

    override fun createUser(user: User) {
        if (usersCache.get(user.userId) != null) {
            throw IllegalStateException("User already exist")
        }

        delegate.createUser(user)
        usersCache.put(user.userId, user)
    }

    override fun user(userId: String, hash: String?): User? {
        // Returns a cached User when available in the cache.
        val cached = usersCache.get(userId)
        val user = if (cached == null) {
            val dbUser = delegate.user(userId)
            if (dbUser != null) {
                usersCache.put(userId, dbUser)
            }
            dbUser
        } else {
            cached
        }

        // Verifies that, if specified, the hash matches to return the user.
        return when {
            user == null -> null
            hash == null -> user
            user.passwordHash == hash -> user
            else -> null
        }
    }

    override fun userByEmail(email: String): User? {
        return delegate.userByEmail(email)
    }

    override fun userTickets(userId: String): List<Int> {
        return delegate.userTickets(userId)
    }

    override fun close() {
        try {
            delegate.close()
        } finally {
            cacheManager.close()
        }
    }
}