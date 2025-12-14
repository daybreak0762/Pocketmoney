package school.pocketmoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.pocketmoney.domain.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
