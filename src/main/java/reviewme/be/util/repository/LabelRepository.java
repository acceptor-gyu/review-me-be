package reviewme.be.util.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import reviewme.be.util.entity.Label;

import java.util.List;
import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Integer> {

    List<Label> findByResumeIsNull();

    List<Label> findByResumeIdOrderByContentAsc(long resumeId);

    Optional<Label> findById(long id);

    Optional<Label> findByResumeIdAndContent(long resumeId, String content);
}
