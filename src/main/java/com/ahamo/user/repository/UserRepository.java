package com.ahamo.user.repository;

import com.ahamo.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByContractNumber(String contractNumber);

    @Query("SELECT u FROM User u WHERE u.contractNumber = :contractNumber AND u.birthDate = :birthDate")
    Optional<User> findByContractNumberAndBirthDate(@Param("contractNumber") String contractNumber, 
                                                   @Param("birthDate") LocalDate birthDate);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByContractNumber(String contractNumber);
}
