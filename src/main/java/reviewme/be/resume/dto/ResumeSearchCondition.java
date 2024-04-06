package reviewme.be.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reviewme.be.resume.dto.request.ResumeSearchConditionParam;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumeSearchCondition {

    private int scope;
    private Integer occupation;
    private Integer startYear;
    private Integer endYear;

    public ResumeSearchCondition(ResumeSearchConditionParam resumeSearchConditionParam) {

        // Default scope is 2 (public, friends only)
        int friendsOnly = 2;

        this.scope = friendsOnly;
        this.occupation = resumeSearchConditionParam.getOccupationId();
        this.startYear = resumeSearchConditionParam.getStartYear();
        this.endYear = resumeSearchConditionParam.getEndYear();
    }

    public void onlyPublic() {

        // is public only
        int publicOnly = 1;

        this.scope = publicOnly;
    }
}
