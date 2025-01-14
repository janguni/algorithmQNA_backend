package algorithm_QnA_community.algorithm_QnA_community.domain.post;

import algorithm_QnA_community.algorithm_QnA_community.domain.BaseTimeEntity;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikePost;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Badge;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
import algorithm_QnA_community.algorithm_QnA_community.utils.listner.CommentListener;
import algorithm_QnA_community.algorithm_QnA_community.utils.listner.PostListener;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain
 * fileName       : Post
 * author         : solmin
 * date           : 2023/04/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/04/26        solmin       최초 생성
 * 2023/05/01        solmin       불필요한 setter 삭제 및 일부 Validation 추가
 *                                TEXT->LONGTEXT
 * 2023/05/02        solmin       조회수 기능 추가 -> 추후 쿠키를 이용해서 중복 피할 예정
 *                                LONGTEXT -> TEXT로 변경 (요구사항이 default page size = 16K를 초과하지 않음)
 *                                추가로 XSS 방지를 위해 스크립트를 HTML 엔티티로 인코딩 이후 조회 시 디코딩하는 작업 필요
 * 2023/05/11        solmin       DynamicInsert, DynamicUpdate 추가
 * 2023/05/11        janguni      PostType 변수 추가
 * 2023/05/12        janguni      updateTitle, updateContent, updateCategory, updateType 추가
 * 2023/05/16        solmin       엔티티 삭제를 위한 orphanRemoval 추가
 * 2023/05/16        janguni      updateViews 추가
 * 2023/05/18        janguni      Member연관관계 CascadeType.ALL -> CascadeType.PERSIST로 변경
 * 2023/05/23        solmin       삭제 편의 연관관계 메소드 추가
 * 2023/05/26        solmin       생성 시 멤버 뱃지 카운트 변경
 * 2023/05/26        solmin       삭제 시 s3 이미지 삭제해주는 EntityListener 연동
 * 2023/05/30        janguni      keyWords 변수 추가
 */

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@DynamicInsert // RequestDto에 특정 필드가 빈 값으로 들어오는 상황에서 insert query에 null을 넣지 않고 값이 삽입되는 필드만 set
@DynamicUpdate // RequestDto에 특정 필드가 빈 빈 값으로 들어오는 상황에서 update query에 null을 넣지 않고 변경된 필드만 set
@EntityListeners({AuditingEntityListener.class, PostListener.class})
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank
    private String content;

    private int likeCnt;
    private int dislikeCnt;

    private int views;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory postCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType type;

    private String keyWords;

    @Builder(builderClassName = "createPost", builderMethodName = "createPost")
    public Post(Member member, String title, String content, PostCategory postCategory, PostType type, List<String> keyWords){
        this.member = member;
        member.getPosts().add(this);
        this.title = title;
        this.content = content;
        this.postCategory = postCategory;
        this.type = type;
        this.keyWords = combineKeyWords(keyWords);
    }

    private String combineKeyWords(List<String> keyWords) {
        if (keyWords == null || keyWords.isEmpty()) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String keyword : keyWords) {
            stringBuilder.append(keyword).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1); // 마지막 쉼표 삭제

        return stringBuilder.toString();
    }

    //----------------- 연관관계 필드 시작 -----------------//

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<LikePost> likePosts = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<ReportPost> reportPosts = new ArrayList<>();



    //----------------- 연관관계 메소드 시작 -----------------//

    public void updateLikeCnt(boolean isLike, boolean isIncrement){
        if(isLike){
            likeCnt = isIncrement? likeCnt+1 : likeCnt-1;
        }else{
            dislikeCnt = isIncrement? dislikeCnt+1 : dislikeCnt-1;
        }
    }

    public void updateTitle(String changedTitle){
        this.title = changedTitle;
    }

    public void updateContent(String changedContent){
        this.content = changedContent;
    }

    public void updateCategory(PostCategory changedCategory) {
        this.postCategory = changedCategory;
    }

    public void updateType(PostType changedType) {
        this.type = changedType;
    }

    public void updateViews(){
        this.views +=1;
    }

    public void updateKeyWords(List<String> keyWords) {
        this.keyWords = combineKeyWords(keyWords);
    }


    public void deletePost(){

        for(Comment comment : comments){
            comment.deleteComment();
        }
        for(ReportPost reportPost : reportPosts){
            reportPost.deleteReportPost();
        }
        for(LikePost likePost : likePosts){
            likePost.deleteLikePost();
        }
        this.member.updateMemberBadgeCnt(Badge.POST, -1);
        this.member = null;
    }

    @PrePersist
    public void beforeSave(){
        this.member.updateMemberBadgeCnt(Badge.POST, 1);
    }


}
