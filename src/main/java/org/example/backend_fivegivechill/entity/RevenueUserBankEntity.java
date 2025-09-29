package org.example.backend_fivegivechill.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "revenue_user_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueUserBankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Liên kết với revenue_user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revenue_id", nullable = false)
    private RavenueUserEntity revenueUser;

    // Thông tin ngân hàng được sao lưu
    @Column(name = "number_account_snapshot", nullable = false)
    private String bankAccountNumber;

    @Column(name = "name_account_snapshot", nullable = false)
    private String bankName;

    @Column(name = "reason", columnDefinition = "nvarchar(255) ")
    private String reason;
}
