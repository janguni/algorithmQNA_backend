package algorithm_QnA_community.algorithm_QnA_community.domain.member;

import algorithm_QnA_community.algorithm_QnA_community.domain.BaseTimeEntity;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikeComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikePost;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain
 * fileName       : Member
 * author         : solmin
 * date           : 2023/04/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/04/26        solmin       최초 생성, Member 엔티티와 다대다 관계를 갖는 각 엔티티에 대해
 *                                멤버가 이러한 매핑정보들을 필수적으로 알아야 하는지 궁금
 */

@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Role role;

    private int commentBadgeCnt;
    private int postBadgeCnt;
    private int likeBadgeCnt;
    private String profileImgUrl;

    @Builder(builderClassName = "createMember", builderMethodName = "createMember")
    public Member(String name, String email, Role role, String profileImgUrl){
        this.name = name;
        this.email = email;
        this.role = role;
        this.profileImgUrl = profileImgUrl;
    }

    //----------------- 연관관계 필드 시작 -----------------//

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();


    // 멤버가 이 정보들을 알고 있지 않는게 낫다고 판단
//    @OneToMany(mappedBy = "member")
//    private List<LikeComment> likeComments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<LikePost> likePosts = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<ReportComment> reportComments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<ReportPost> reportPosts = new ArrayList<>();

    //----------------- 연관관계 메소드 시작 -----------------//



}
