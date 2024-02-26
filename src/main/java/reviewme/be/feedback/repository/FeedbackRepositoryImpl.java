package reviewme.be.feedback.repository;

import static reviewme.be.feedback.entity.QFeedback.feedback;
import static reviewme.be.util.entity.QLabel.label;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reviewme.be.feedback.dto.FeedbackCommentInfo;
import reviewme.be.feedback.dto.FeedbackInfo;
import reviewme.be.feedback.dto.QFeedbackCommentInfo;
import reviewme.be.feedback.dto.QFeedbackInfo;

@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements FeedbackRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FeedbackInfo> findFeedbacksByResumeIdAndResumePage(long resumeId, int resumePage,
        Pageable pageable) {

        QueryResults<FeedbackInfo> results = queryFactory
            .select(new QFeedbackInfo(
                feedback.id,
                feedback.content,
                label.content,
                feedback.commenter.id,
                feedback.commenter.name,
                feedback.commenter.profileUrl,
                feedback.createdAt,
                feedback.childCnt,
                feedback.checked
            ))
            .from(feedback)
            .innerJoin(feedback.commenter)
            .leftJoin(feedback.label, label)
            .where(feedback.resume.id.eq(resumeId)
                .and(feedback.resumePage.eq(resumePage))
                .and(feedback.parentFeedback.isNull())
                .and(feedback.deletedAt.isNull()
                    .or(feedback.deletedAt.isNotNull().and(feedback.childCnt.gt(0))))
            )
            .orderBy(feedback.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        List<FeedbackInfo> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<FeedbackCommentInfo> findFeedbackCommentsByFeedbackId(long feedbackId,
        Pageable pageable) {

        QueryResults<FeedbackCommentInfo> results = queryFactory
            .select(new QFeedbackCommentInfo(
                feedback.id,
                feedback.parentFeedback.id,
                feedback.content,
                feedback.commenter.id,
                feedback.commenter.name,
                feedback.commenter.profileUrl,
                feedback.createdAt
            ))
            .from(feedback)
            .innerJoin(feedback.commenter)
            .where(feedback.parentFeedback.id.eq(feedbackId)
                .and(feedback.deletedAt.isNull())
            )
            .orderBy(feedback.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        List<FeedbackCommentInfo> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
}
