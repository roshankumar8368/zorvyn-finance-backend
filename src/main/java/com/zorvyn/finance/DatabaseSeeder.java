package com.zorvyn.finance;

import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.Role;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FinancialRecordRepository recordRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only seed if the database is empty
        if (userRepository.count() == 0) {

            // 1. Create Users
            User admin = userRepository.save(User.builder()
                    .username("admin_user").password("pass123").role(Role.ADMIN).active(true).build());

            User analyst = userRepository.save(User.builder()
                    .username("analyst_user").password("pass123").role(Role.ANALYST).active(true).build());

            User viewer = userRepository.save(User.builder()
                    .username("viewer_user").password("pass123").role(Role.VIEWER).active(true).build());

            // 2. Create Dummy Financial Records
            recordRepository.save(FinancialRecord.builder()
                    .amount(new BigDecimal("5000.00")).type(TransactionType.INCOME)
                    .category("Software Contract").date(LocalDate.now().minusDays(2))
                    .description("Payment from client A").createdBy(admin).build());

            recordRepository.save(FinancialRecord.builder()
                    .amount(new BigDecimal("1200.50")).type(TransactionType.EXPENSE)
                    .category("Cloud Infrastructure").date(LocalDate.now().minusDays(1))
                    .description("AWS Monthly Bill").createdBy(admin).build());

            recordRepository.save(FinancialRecord.builder()
                    .amount(new BigDecimal("300.00")).type(TransactionType.EXPENSE)
                    .category("Software Licenses").date(LocalDate.now())
                    .description("GitHub & JetBrains").createdBy(admin).build());

            System.out.println("✅ Database seeded with mock Users and Financial Records.");
            System.out.println("👨‍💻 Admin ID: " + admin.getId());
            System.out.println("📊 Analyst ID: " + analyst.getId());
            System.out.println("👀 Viewer ID: " + viewer.getId());
        }
    }
}