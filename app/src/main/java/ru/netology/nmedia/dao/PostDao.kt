@file:Suppress("IMPLICIT_CAST_TO_ANY")

package ru.netology.nmedia.dao


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.javafaker.Faker
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.util.getTime

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM PostEntity WHERE hidden = 0 ORDER BY id DESC ")
    fun getAllVisible(): Flow<List<PostEntity>>

    @Query("UPDATE PostEntity SET hidden = 0")
    suspend fun showAll()

    @Query("SELECT COUNT(*) FROM PostEntity WHERE hidden = 1")
    fun getHiddenCount(): Flow<Int>

    @Query("SELECT id FROM PostEntity ORDER BY id DESC LIMIT 1")
    fun getLastPostId(): Flow<Long?>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    suspend fun save(post: PostEntity) =
        if (post.id == 0L) {
            insert(
                post.copy(
                    author = Faker().name().fullName(),
                    published = getTime()
                )
            )

        } else {
            updateContentById(
                post.id,
                post.content
            )
        }

    @Query(
        """
    UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe = 1 THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe = 1 THEN 0 ELSE 1 END
    WHERE id = :id
    """
    )
    suspend fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM PostEntity")
    suspend fun clear()

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Long): PostEntity?


    @Query(
        """
    UPDATE PostEntity SET
        shares = shares + 1
    WHERE id = :id
    """
    )
    suspend fun sharedById(id: Long)
}