package com.sametyilmaz.jobtrackr.repository;

import com.sametyilmaz.jobtrackr.entity.Application;
import com.sametyilmaz.jobtrackr.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    List<Application> findByUserId(Long userId);

    List<Application> findByUserIdAndStatus(
            Long userId, ApplicationStatus status);

    List<Application> findByUserIdAndCompanyContainingIgnoreCase(
            Long userId, String company);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.user.id = :userId AND a.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId,
            @Param("status") ApplicationStatus status);
}