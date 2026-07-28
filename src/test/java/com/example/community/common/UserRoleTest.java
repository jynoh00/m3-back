package com.example.community.common;

import com.example.community.entity.main.user.User;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.community.TestUserConstant.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Test
    void 사용자_권한이_따로_지정되지_않으면_기본_권한은_일반_사용자_권한이다() {
        User user = NORMAL.toUser();

        assertThat(user.getRole()).isEqualTo(UserRole.USER.getRole());
    }

    @Test
    void 사용자는_일반_사용자_또는_관리자_권한_중_하나를_가진다() {
        Set<String> roles = Arrays.stream(UserRole.values())
                .map(UserRole::getRole)
                .collect(Collectors.toSet());

        assertThat(roles)
                .containsExactlyInAnyOrder(
                        UserRole.USER.getRole(),
                        UserRole.ADMIN.getRole()
                );
    }
}