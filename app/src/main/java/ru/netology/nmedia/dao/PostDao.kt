@file:Suppress("IMPLICIT_CAST_TO_ANY")

package ru.netology.nmedia.dao


import androidx.lifecycle.LiveData
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
    fun getAll(): LiveData<List<PostEntity>>

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

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun likeById(id: Long): PostEntity

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Long): PostEntity?


    @Query(
        """
    UPDATE PostEntity SET
        shares = shares + 1,
        likes = likes + CASE WHEN likedByMe = 1 THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe = 1 THEN 0 ELSE 1 END
    WHERE id = :id
    """
    )
    suspend fun sharedById(id: Long)

}