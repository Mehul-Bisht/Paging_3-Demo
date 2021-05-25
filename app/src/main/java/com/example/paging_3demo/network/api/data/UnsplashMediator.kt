package com.example.paging_3demo.network.api.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.paging_3demo.Utils.toUnsplashPhotoEntity
import com.example.paging_3demo.db.remoteKeysDb.RemoteKeys
import com.example.paging_3demo.db.unplashdb.UnsplashDatabase
import com.example.paging_3demo.db.unplashdb.UnsplashPhotoEntity
import com.example.paging_3demo.network.api.UnsplashApi
import retrofit2.HttpException
import java.io.IOException

private const val UNSPLASH_STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
class UnsplashMediator(
    private val unsplashDatabase: UnsplashDatabase,
    private val unsplashApi: UnsplashApi
): RemoteMediator<Int,UnsplashPhotoEntity>() {

    override suspend fun initialize(): InitializeAction {

        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.

        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UnsplashPhotoEntity>
    ): MediatorResult{

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                Log.d("page"," refresh: ${remoteKeys?.nextKey?.minus(1)}")
                remoteKeys?.nextKey?.minus(1) ?: UNSPLASH_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)

                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.

                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                Log.d("page"," prepend: $prevKey")
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)

                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for append.

                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                Log.d("page"," append: $nextKey")
                nextKey
            }
        }

        try {
            val apiResponse = unsplashApi.searchPhotos("dogs",page,20)

            val repos = apiResponse.results.map { it.toUnsplashPhotoEntity() }

            val endOfPaginationReached = repos.isEmpty()

            unsplashDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    unsplashDatabase.getRemoteKeysDao().clearRemoteKeys()
                    unsplashDatabase.getDao().deleteAllPhotos()
                }
                val prevKey = if (page == UNSPLASH_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = repos.map {
                    RemoteKeys(repoId = it.id.hashCode().toLong(), prevKey = prevKey, nextKey = nextKey)
                }
                unsplashDatabase.getRemoteKeysDao().insertAll(keys)
                unsplashDatabase.getDao().insertPhotos(repos)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, UnsplashPhotoEntity>): RemoteKeys? {

        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item

        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                // Get the remote keys of the last item retrieved
                val remoteKey = unsplashDatabase.getRemoteKeysDao().remoteKeysRepoId(repo.id.hashCode().toLong())
                remoteKey
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, UnsplashPhotoEntity>): RemoteKeys? {

        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item

        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                // Get the remote keys of the first items retrieved
                unsplashDatabase.getRemoteKeysDao().remoteKeysRepoId(repo.id.hashCode().toLong())
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, UnsplashPhotoEntity>
    ): RemoteKeys? {

        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position

        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                unsplashDatabase.getRemoteKeysDao().remoteKeysRepoId(repoId.hashCode().toLong())
            }
        }
    }
}