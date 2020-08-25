package com.example.demo7.domain.entity;

import lombok.*;
import org.omg.PortableServer.ServantActivator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@ToString(exclude = "userRoles")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String age;
//    private String role;

    // CustomAuthenticationProvider 에서 @Transactional 을 사용하여 LAZY 오류를 해결해준다
    // LAZY 는 DB와 session 이 유지되어 있어야지만 가능하기 때무네
    // session 이 유지되지 못하면서 불러올 수 없어 에러를 발생한다
    // 그럴 땐 EAGER 를 사용하는 것도 하나의 방법이다
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "account_roles",
            joinColumns = { @JoinColumn(name = "account_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    private Set<Role> userRoles = new HashSet<>();

}
