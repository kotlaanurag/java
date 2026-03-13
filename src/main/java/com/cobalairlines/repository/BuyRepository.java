package com.cobalairlines.repository;

import com.cobalairlines.entity.Buy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BuyRepository extends JpaRepository<Buy, Integer> {
    List<Buy> findByPassengerClientId(Integer clientId);
    List<Buy> findByEmployeeEmpId(String empId);
    List<Buy> findByBuyDate(LocalDate date);
}
