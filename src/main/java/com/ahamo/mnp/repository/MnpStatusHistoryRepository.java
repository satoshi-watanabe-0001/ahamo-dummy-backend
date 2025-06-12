package com.ahamo.mnp.repository;

import com.ahamo.mnp.model.MnpStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MnpStatusHistoryRepository extends JpaRepository<MnpStatusHistory, Long> {

    List<MnpStatusHistory> findByMnpRequestIdOrderByCreatedAtDesc(Long mnpRequestId);

    List<MnpStatusHistory> findByMnpRequestId(Long mnpRequestId);
}
