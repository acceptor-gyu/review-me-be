package reviewme.be.resume.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reviewme.be.resume.dto.ResumeSearchCondition;
import reviewme.be.resume.dto.response.MyResumeResponse;
import reviewme.be.resume.dto.response.QMyResumeResponse;
import reviewme.be.resume.dto.response.QResumeResponse;
import reviewme.be.resume.dto.response.ResumeResponse;

import java.util.List;
import reviewme.be.user.entity.User;

import static reviewme.be.friend.entity.QFriend.friend;
import static reviewme.be.resume.entity.QResume.resume;

@RequiredArgsConstructor
public class ResumeRepositoryImpl implements ResumeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ResumeResponse> findResumes(ResumeSearchCondition searchCondition, User user,
        Pageable pageable) {

        QueryResults<ResumeResponse> results = queryFactory
            .select(new QResumeResponse(
                resume.id,
                resume.title,
                resume.writer.id,
                resume.writer.name,
                resume.writer.profileUrl,
                resume.createdAt,
                resume.scope.scope,
                resume.occupation.occupation,
                resume.year
            ))
            .from(resume)
            .leftJoin(resume.writer)
            .leftJoin(resume.scope)
            .leftJoin(resume.occupation)
            .leftJoin(friend).on(friend.followingUser.id.eq(resume.writer.id))
            .where(
                resume.writer.id.eq(user.getId())
                    .or(scopeCondition(searchCondition.getScope(), user))
                        .and(occupationEq(searchCondition.getOccupation()))
                        .and(yearGoe(searchCondition.getStartYear()))
                        .and(yearLoe(searchCondition.getEndYear()))
                    .and(resume.deletedAt.isNull())
            )
            .distinct()
            .orderBy(resume.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        List<ResumeResponse> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MyResumeResponse> findResumesByWriterId(Pageable pageable, long userId) {

        QueryResults<MyResumeResponse> results = queryFactory
            .select(new QMyResumeResponse(
                resume.id,
                resume.title,
                resume.createdAt,
                resume.scope.scope,
                resume.occupation.occupation,
                resume.year
            ))
            .from(resume)
            .leftJoin(resume.writer)
            .leftJoin(resume.scope)
            .leftJoin(resume.occupation)
            .where(
                resume.writer.id.eq(userId),
                resume.deletedAt.isNull()
            )
            .orderBy(resume.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        List<MyResumeResponse> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression scopeCondition(int scopeId, User user) {

        int publicOnly = 1;
        int friendsOnly = 2;

        BooleanExpression publicCondition = resume.scope.id.eq(publicOnly);

        if (scopeId == friendsOnly) {

            BooleanExpression friendCondition = resume.scope.id.eq(friendsOnly)
                .and(resume.writer.id.in(
                    queryFactory.select(friend.followerUser.id)
                        .from(friend)
                        .where((friend.followingUser.id.eq(user.getId())
                            .or(friend.followerUser.id.eq(user.getId())))
                            .and(friend.accepted.isTrue())))
                );

            return publicCondition.or(friendCondition);
        }

        return publicCondition;
    }

    private BooleanExpression occupationEq(Integer occupationId) {

        return occupationId != null ? resume.occupation.id.eq(occupationId) : null;
    }

    private BooleanExpression yearGoe(Integer startYear) {

        return startYear != null ? resume.year.goe(startYear) : null;
    }

    private BooleanExpression yearLoe(Integer endYear) {

        return endYear != null ? resume.year.loe(endYear) : null;
    }
}
