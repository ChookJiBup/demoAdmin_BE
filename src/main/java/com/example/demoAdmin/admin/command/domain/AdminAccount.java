package com.example.demoadmin.admin.command.domain;

import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.common.domain.BaseTimeEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * 축제별 관리자 계정 Aggregate이다.
 */
@Entity
@Table(
        name = "admin_accounts",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_admin_accounts_email",
                columnNames = "email"
        )
)
public class AdminAccount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "email", nullable = false, length = 255)
    )
    private AdminEmail email;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "name", nullable = false, length = 100)
    )
    private AdminName name;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "organization", nullable = false, length = 255)
    )
    private AdminOrganization organization;

    @Column(name = "festival_id", nullable = false)
    private Long festivalId;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AdminRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AdminStatus status;

    @Column(name = "invited_by_admin_id")
    private Long invitedByAdminId;

    protected AdminAccount() {
    }

    private AdminAccount(
            AdminEmail email,
            AdminName name,
            AdminOrganization organization,
            Long festivalId,
            String passwordHash,
            AdminRole role,
            Long invitedByAdminId
    ) {
        this.email = email;
        this.name = name;
        this.organization = organization;
        this.festivalId = festivalId;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = AdminStatus.ACTIVE;
        this.invitedByAdminId = invitedByAdminId;
    }

    /**
     * 한 축제의 최상위 관리자인 1관리자 계정을 생성한다.
     */
    public static AdminAccount createFestivalOwner(
            AdminEmail email,
            AdminName name,
            AdminOrganization organization,
            Long festivalId,
            String passwordHash
    ) {
        return new AdminAccount(
                email,
                name,
                organization,
                festivalId,
                passwordHash,
                AdminRole.FESTIVAL_OWNER,
                null
        );
    }

    /**
     * 1관리자의 초대를 통해 서브관리자 계정을 생성한다.
     */
    public static AdminAccount createSubAdmin(
            AdminEmail email,
            AdminName name,
            AdminOrganization organization,
            Long festivalId,
            String passwordHash,
            Long invitedByAdminId
    ) {
        return new AdminAccount(
                email,
                name,
                organization,
                festivalId,
                passwordHash,
                AdminRole.SUB_ADMIN,
                invitedByAdminId
        );
    }

    /**
     * 로그인과 API 사용이 가능한 활성 계정인지 확인한다.
     */
    public boolean isActive() {
        return status == AdminStatus.ACTIVE;
    }

    /**
     * 서브관리자 초대 권한을 가진 계정인지 확인한다.
     */
    public boolean canInviteSubAdmin() {
        return role.canInviteSubAdmin();
    }

    /**
     * 행사 기본 정보 수정 권한을 가진 계정인지 확인한다.
     */
    public boolean canModifyFestivalInfo() {
        return role.canModifyFestivalInfo();
    }

    /**
     * 현장 줄 끝 라인 갱신 권한을 가진 계정인지 확인한다.
     */
    public boolean canUpdateQueueTail() {
        return role.canUpdateQueueTail();
    }

    public Long getId() {
        return id;
    }

    public AdminEmail getEmail() {
        return email;
    }

    public String getEmailValue() {
        return email.getValue();
    }

    public AdminName getName() {
        return name;
    }

    public String getNameValue() {
        return name.getValue();
    }

    public AdminOrganization getOrganization() {
        return organization;
    }

    public String getOrganizationValue() {
        return organization.getValue();
    }

    public Long getFestivalId() {
        return festivalId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AdminRole getRole() {
        return role;
    }

    public AdminStatus getStatus() {
        return status;
    }

    public Long getInvitedByAdminId() {
        return invitedByAdminId;
    }
}
