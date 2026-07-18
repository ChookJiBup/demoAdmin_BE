package com.example.demoadmin.admin.command.domain;

import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.common.domain.BaseTimeEntity;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 축제별 관리자 계정 Aggregate이다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "festival_id")
    private Long festivalId;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private AdminRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AdminStatus status;

    @Column(name = "invited_by_admin_id")
    private Long invitedByAdminId;

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
     * 아직 축제를 만들거나 초대받지 않은 일반 관리자 계정을 생성한다.
     */
    public static AdminAccount createAdmin(
            AdminEmail email,
            AdminName name,
            AdminOrganization organization,
            String passwordHash
    ) {
        return new AdminAccount(
                email,
                name,
                organization,
                null,
                passwordHash,
                null,
                null
        );
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
        return role != null && role.canInviteSubAdmin();
    }

    /**
     * 행사 기본 정보 수정 권한을 가진 계정인지 확인한다.
     */
    public boolean canModifyFestivalInfo() {
        return role != null && role.canModifyFestivalInfo();
    }

    /**
     * 현장 줄 끝 라인 갱신 권한을 가진 계정인지 확인한다.
     */
    public boolean canUpdateQueueTail() {
        return role != null && role.canUpdateQueueTail();
    }

    /**
     * 축제를 생성한 관리자를 해당 축제의 1관리자로 배정한다.
     */
    public void assignFestivalOwner(Long festivalId) {
        if (festivalId == null || this.festivalId != null || this.role != null) {
            throw new CustomException(ErrorCode.AUTH_ADMIN_ALREADY_ASSIGNED);
        }

        this.festivalId = festivalId;
        this.role = AdminRole.FESTIVAL_OWNER;
    }

    public String getEmailValue() {
        return email.getValue();
    }

    public String getNameValue() {
        return name.getValue();
    }

    public String getOrganizationValue() {
        return organization.getValue();
    }
}
