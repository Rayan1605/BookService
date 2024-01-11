package Backend.libaryproject.Repository;

import Backend.libaryproject.Entity.Histroy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface HistoryRepository extends JpaRepository<Histroy,Long> {


    Page<Histroy> findBooksByUserEmail(@Param("user_email") String userEmail, Pageable pageable);
}
