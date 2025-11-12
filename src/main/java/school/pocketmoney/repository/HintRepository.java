package school.pocketmoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.pocketmoney.domain.Hint;

public interface HintRepository extends JpaRepository<Hint, Long> {

}
