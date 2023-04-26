package algorithm_QnA_community.algorithm_QnA_community.domain.report;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import lombok.*;

import javax.persistence.*;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain
 * fileName       : LikeComment
 * author         : solmin
 * date           : 2023/04/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/04/26        solmin       최초 생성
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class ReportPost {
    @Id
    @GeneratedValue
    @Column(name = "report_post_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportCategory category;

    @Column(nullable = false)
    private String detail;

    @Builder(builderClassName = "createReportPost", builderMethodName = "createReportPost")
    public ReportPost(Post post, Member member, ReportCategory category, String detail){
        this.member = member;
        this.post = post;
        this.category = category;
        this.detail = detail;
    }

    public void updateCategory(ReportCategory category){
        this.category = category;
    }

    public void updateDetail(String detail){
        this.detail = detail;
    }

    //----------------- 연관관계 필드 시작 -----------------//

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
