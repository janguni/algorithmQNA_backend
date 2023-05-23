package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.repository
 * fileName       : PostRepository
 * author         : solmin
 * date           : 2023/05/01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/01        solmin       최초 생성(테스트용)
 * 2023/05/16        janguni      정렬 메소드 생성
 * 2023/05/21        janguni      정렬 메소드에 페이징기능 추가
 * 2023/05/22        solmin       findByPostIds <- AdminService에서 사용중
 */
public interface PostRepository extends JpaRepository<Post, Long> {
    // 최신순으로 정렬
    List<Post> findByCategoryAndTypeOrderByCreatedDateDesc(PostCategory category, PostType postType, Pageable pageable);

    // 오래된 순으로 정렬
    List<Post> findByCategoryAndTypeOrderByCreatedDateAsc(PostCategory category, PostType postType, Pageable pageable);

    @Query("select count(c) from Post p left join p.comments c")
    int countCommentByPost(@Param("post") Post post);

    // 추천-비추천 내림차순
    @Query("select p from Post p" +
            " where p.category = :category" +
            " and p.type = :postType" +
            " order by p.likeCnt-p.dislikeCnt desc")
    List<Post> findByCategoryOrderByLike_DislikeDESC(@Param("category") PostCategory category, @Param("postType") PostType postType, Pageable pageable);

    // 추천-비추천 오름차순
    @Query("select p from Post p" +
            " where p.category = :category" +
            " and p.type = :postType" +
            " order by p.likeCnt-p.dislikeCnt asc")
    List<Post> findByCategoryOrderByLike_DislikeASC(@Param("category") PostCategory category, @Param("postType") PostType postType, Pageable pageable);

    // 댓글 내림차순
    @Query("select p from Post p" +
            " left join p.comments c" +
            " where p.category = :category" +
            " and p.type = :postType" +
            " group by p order by count(c) desc")
    List<Post> findPostOrderByCommentCntDesc(@Param("category") PostCategory category, @Param("postType") PostType postType, Pageable pageable);

    // 댓글 오름차순
    @Query("select p from Post p" +
            " left join p.comments c" +
            " where p.category = :category" +
            " and p.type = :postType" +
            " group by p order by count(c) asc")
    List<Post> findPostOrderByCommentCntAsc(@Param("category") PostCategory category, @Param("postType") PostType postType, Pageable pageable);


    // 인기순
    @Query(value = "select p.* from post p" +
            " left join comment c on p.post_id = c.post_id" +
            " where p.category = :category" +
            " and p.type = :postType" +
            " group by p.post_id" +
            " order by p.views * 0.5 + (POW(p.like_cnt, 2) / (p.like_cnt + p.dislike_cnt)) * 0.3 + count(c.comment_id) * 0.2 desc" +
            " limit 20 OFFSET :start", nativeQuery = true)
    List<Post> findByPostOrderByPopular(@Param("category") String category, @Param("postType") String postType,  @Param("start") int start);

    // 조회수 오름차순
    List<Post> findByCategoryAndTypeOrderByViewsAsc(PostCategory category, PostType postType, Pageable pageable);

    // 조회수 내림차순
    List<Post> findByCategoryAndTypeOrderByViewsDesc(PostCategory category, PostType postType, Pageable pageable);

    // postId 리스트에 해당하는 post를 Pageable하게 가져오기
    // AdminService에서 사용중입니다!
    @Query(value = "select p from Post p where p.id in :postIds")
    Page<Post> findByPostIds(@Param("postIds") List<Long> postIds, Pageable pageable);
}
